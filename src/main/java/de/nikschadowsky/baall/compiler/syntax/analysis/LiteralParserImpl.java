package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
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
        ParseResult<PrimitiveLiteralNode> parsedPrimitiveLiteral =
                parsePrimitiveLiteral(queue.branchOff());
        if (parsedPrimitiveLiteral.isSuccessful()) {
            queue.mergeBranch(parsedPrimitiveLiteral.getTokenQueueId());
            return ParseResult.successfulParse(parsedPrimitiveLiteral.getParseResult(), queue.getId());
        }

        ParseResult<ArrayLiteralNode> parsedArrayLiteral = parseArrayLiteral(queue.branchOff());
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

        ParseResult<StructInitializationLiteralNode>
                parsedStructInitializationLiteral =
                parseStructInitialization(queue.branchOff());
        if (parsedStructInitializationLiteral.isSuccessful()) {
            queue.mergeBranch(parsedStructInitializationLiteral.getTokenQueueId());
            return ParseResult.successfulParse(parsedStructInitializationLiteral.getParseResult(), queue.getId());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected a literal!"), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<ArrayLiteralNode> parseArrayLiteral(TokenQueue queue) {
        ArrayLiteralNodeImpl node = astFactory.createArrayLiteralNode();
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
        List<TypeNode> fieldTypes = new ArrayList<>();
        List<Token> fieldNames = new ArrayList<>();
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

    @CompleteParse
    @Override
    public ParseResult<StructInitializationLiteralNode> parseStructInitialization(TokenQueue queue) {
        StructInitializationLiteralNodeImpl node = astFactory.createStructInitializationLiteralNode();

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("none").matches(queue.peek())) {
            queue.poll();
            node.setArguments(new LinkedList<>());
            return ParseResult.successfulParse(node, queue.getId());
        }

        ParseResult<IdentifierAccessNode> parsedStructType =
                programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
        if (parsedStructType.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(parsedStructType.getDiagnostic(), queue.getId());
        }
        queue.mergeBranch(parsedStructType.getTokenQueueId());
        node.setIdentifier(parsedStructType.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("(").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '('!"), queue.getId());
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch(parsedExpression.getTokenQueueId());
            ParseResult<List<ExpressionNode>> parsedArguments =
                    programParser.getAuxiliaryParser().parseArgumentList(queue.branchOff());
            if (parsedArguments.isSuccessful()) {
                queue.mergeBranch(parsedArguments.getTokenQueueId());

                if (SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
                    queue.poll();
                    List<ExpressionNode> arguments = new LinkedList<>();
                    arguments.add(parsedExpression.getParseResult());
                    arguments.addAll(parsedArguments.getParseResult());
                    node.setArguments(arguments);
                    return ParseResult.successfulParse(node, queue.getId());
                }
                return ParseResult.unsuccessfulParse(
                        new SyntaxDiagnostic(queue.poll(), "Expected ')'!"),
                        queue.getId()
                );
            }
            return ParseResult.unsuccessfulParse(parsedArguments.getDiagnostic(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected an expression or 'none'!"),
                queue.getId()
        );
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

    @CompleteParse
    @Override
    public ParseResult<PrimitiveLiteralNode> parsePrimitiveLiteral(TokenQueue queue) {
        Token nextToken = queue.peek();

        if (SyntaxSet.PRIMITIVES.stream().anyMatch(e -> e.matches(nextToken))) {
            queue.poll();
            PrimitiveLiteralNodeImpl node = astFactory.createPrimitiveLiteralNode();
            node.setPrimitiveValue(nextToken);
            node.setPrimitiveType(
                    switch (nextToken.type()) {
                        case NUMBER -> PrimitiveLiteralNode.PrimitiveType.NUMBER;
                        case STRING -> PrimitiveLiteralNode.PrimitiveType.STRING;
                        case BOOLEAN -> PrimitiveLiteralNode.PrimitiveType.BOOLEAN;
                        default -> throw new IllegalStateException("Unexpected value: " + nextToken.type());
                    });

            return ParseResult.successfulParse(node, queue.getId());
        }

        return ParseResult.unsuccessfulParse(
                new SyntaxDiagnostic(queue.poll(), "Expected a primitive literal"),
                queue.getId()
        );
    }
}
