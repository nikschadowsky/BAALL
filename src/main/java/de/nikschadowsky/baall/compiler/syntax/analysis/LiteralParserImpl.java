package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 11.08.2024
 */
public class LiteralParserImpl implements LiteralParser {

    private final ProgramParser programParser;
    private final ASTNodeFactory astFactory;

    public LiteralParserImpl(ProgramParser programParser, ASTNodeFactory astFactory) {
        this.programParser = programParser;
        this.astFactory = astFactory;
    }

    @CompleteParse
    @Override
    public ParseResult<LiteralNode> parseLiteral(TokenQueue queue) {
        ParseResult<BooleanLiteralNode> parsedBooleanLiteral = parseBooleanLiteral(queue.branchOff());
        if (parsedBooleanLiteral.isSuccessful()) {
            queue.mergeBranch(parsedBooleanLiteral.getTokenQueueId());
            return ParseResult.successfulParse(parsedBooleanLiteral.getParseResult(), queue.getId());
        }

        ParseResult<NumberLiteralNode> parsedNumberLiteral = parseNumberLiteral(queue.branchOff());
        if (parsedNumberLiteral.isSuccessful()) {
            queue.mergeBranch(parsedNumberLiteral.getTokenQueueId());
            return ParseResult.successfulParse(parsedNumberLiteral.getParseResult(), queue.getId());
        }

        ParseResult<StringLiteralNode> parsedStringLiteral = parseStringLiteral(queue.branchOff());
        if (parsedStringLiteral.isSuccessful()) {
            queue.mergeBranch(parsedStringLiteral.getTokenQueueId());
            return ParseResult.successfulParse(parsedStringLiteral.getParseResult(), queue.getId());
        }

        ParseResult<ListLiteralNode> parsedArrayLiteral = parseListLiteral(queue.branchOff());
        if (parsedArrayLiteral.isSuccessful()) {
            queue.mergeBranch(parsedArrayLiteral.getTokenQueueId());
            return ParseResult.successfulParse(parsedArrayLiteral.getParseResult(), queue.getId());
        }

        ParseResult<FunctionDefinitionNode> parsedFunctionDefinition =
                parseFunctionDefinition(queue.branchOff());
        if (parsedFunctionDefinition.isSuccessful()) {
            queue.mergeBranch(parsedFunctionDefinition.getTokenQueueId());
            return ParseResult.successfulParse(parsedFunctionDefinition.getParseResult(), queue.getId());
        }

        ParseResult<StructDefinitionLiteralNode> parseStructDefinitionLiteral =
                parseStructDefinition(queue.branchOff());
        if (parseStructDefinitionLiteral.isSuccessful()) {
            queue.mergeBranch(parseStructDefinitionLiteral.getTokenQueueId());
            return ParseResult.successfulParse(parseStructDefinitionLiteral.getParseResult(), queue.getId());
        }

        ParseResult<StructNoneLiteralNode> parsedStructNone = parseStructNone(queue.branchOff());
        if (parsedStructNone.isSuccessful()) {
            queue.mergeBranch(parsedStructNone.getTokenQueueId());
            return ParseResult.successfulParse(parsedStructNone.getParseResult(), queue.getId());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected a literal!"), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<ListLiteralNode> parseListLiteral(TokenQueue queue) {
        ListLiteralNodeImpl node = astFactory.createListLiteralNode();
        List<ExpressionNode> elements = new LinkedList<>();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("[").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '['!"), queue.getId());
        }
        queue.poll();
        // optimization - empty array literal
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            queue.poll();
            node.setElements(elements);
            return ParseResult.successfulParse(node, queue.getId());
        }
        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isUnsuccessful()) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("]"));
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedExpression.getTokenQueueId());
        elements.add(parsedExpression.getParseResult());

        ParseResult<List<ExpressionNode>> parsedArrayElements =
                programParser.getAuxiliaryParser().parseArgumentList(queue.branchOff());
        if (parsedArrayElements.isUnsuccessful()) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("]"));
            return ParseResult.unsuccessfulParse(parsedArrayElements.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedArrayElements.getTokenQueueId());
        elements.addAll(parsedArrayElements.getParseResult());

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            queue.poll();
            node.setElements(elements);
            return ParseResult.successfulParse(node, queue.getId());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'!"), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<StructDefinitionLiteralNode> parseStructDefinition(TokenQueue queue) {
        List<FieldNode> fields = new ArrayList<>();

        StructDefinitionLiteralNodeImpl node =
                astFactory.createStructDefinitionLiteralNode();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("(").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '('!"), queue.getId());
        }
        queue.poll();

        ParseResult<FieldNode> parsedStructField =
                programParser.getAuxiliaryParser().parseFieldDeclaration(queue.branchOff());
        if (parsedStructField.isUnsuccessful()) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get(")"));
            return ParseResult.unsuccessfulParse(parsedStructField.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedStructField.getTokenQueueId());
        fields.add(parsedStructField.getParseResult());

        ParseResult<List<FieldNode>> parsedStructFields =
                programParser.getAuxiliaryParser().parseFieldDeclarations(queue.branchOff());
        if (parsedStructFields.isUnsuccessful()) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get(")"));
            return ParseResult.unsuccessfulParse(parsedStructFields.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedStructFields.getTokenQueueId());

        fields.addAll(parsedStructFields.getParseResult());

        node.setFields(fields);

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ')'!"), queue.getId());
        }
        queue.poll();
        return ParseResult.successfulParse(node, queue.getId());
    }

    @Override
    public ParseResult<StructNoneLiteralNode> parseStructNone(TokenQueue queue) {
        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("none").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected 'none'!"), queue.getId());
        }
        queue.poll();
        StructNoneLiteralNodeImpl node = astFactory.createStructNoneLiteralNode();
        return ParseResult.successfulParse(node, queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<FunctionDefinitionNode> parseFunctionDefinition(TokenQueue queue) {
        FunctionDefinitionNodeImpl node = astFactory.createFunctionDefinitionNode();
        List<FieldNode> parameters = new ArrayList<>();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("(").matches(queue.peek())) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '('!"), queue.getId());
        }
        queue.poll();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
            ParseResult<FieldNode> parsedFunctionParameter =
                    programParser.getAuxiliaryParser().parseFieldDeclaration(queue.branchOff());
            if (parsedFunctionParameter.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(parsedFunctionParameter.getDiagnostic(), queue.getId());
            }
            queue.mergeBranch(parsedFunctionParameter.getTokenQueueId());
            parameters.add(parsedFunctionParameter.getParseResult());

            ParseResult<List<FieldNode>> parsedParameterDeclarations =
                    programParser.getAuxiliaryParser().parseFieldDeclarations(queue.branchOff());
            if (parsedParameterDeclarations.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(parsedParameterDeclarations.getDiagnostic(), queue.getId());
            }
            queue.mergeBranch(parsedParameterDeclarations.getTokenQueueId());
            parameters.addAll(parsedParameterDeclarations.getParseResult());
        }

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ')'!"), queue.getId());
        }
        queue.poll();
        node.setParameters(parameters);

        ParseResult<StatementsNode> parsedBody = programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        if (!parsedBody.isSuccessful()) {
            return ParseResult.unsuccessfulParse(parsedBody.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedBody.getTokenQueueId());
        node.setFunctionBody(parsedBody.getParseResult());
        return ParseResult.successfulParse(node, queue.getId());
    }

    @Override
    public ParseResult<BooleanLiteralNode> parseBooleanLiteral(TokenQueue queue) {
        BooleanLiteralNodeImpl node = astFactory.createBooleanLiteralNode();
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("boolean_primitive").matches(queue.peek())) {
            node.setValue(queue.poll());
            return ParseResult.successfulParse(node, queue.getId());
        }

        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.peek(), "Expected a boolean primitive"),
                queue.getId()
        );
    }

    @Override
    public ParseResult<NumberLiteralNode> parseNumberLiteral(TokenQueue queue) {
        NumberLiteralNodeImpl node = astFactory.createNumberLiteralNode();
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("number_primitive").matches(queue.peek())) {
            node.setValue(queue.poll());
            return ParseResult.successfulParse(node, queue.getId());
        }

        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.peek(), "Expected a number primitive"),
                queue.getId()
        );
    }

    @Override
    public ParseResult<StringLiteralNode> parseStringLiteral(TokenQueue queue) {
        StringLiteralNodeImpl node = astFactory.createStringLiteralNode();
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("string_primitive").matches(queue.peek())) {
            node.setValue(queue.poll());
            return ParseResult.successfulParse(node, queue.getId());
        }

        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.peek(), "Expected a string primitive"),
                queue.getId()
        );
    }
}
