package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal.*;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedPrimitiveLiteral.getParseResult());
        }

        ParseResult<ArrayLiteralNode> parsedArrayLiteral = parseArrayLiteral(queue.branchOff());
        if (parsedArrayLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedArrayLiteral.getParseResult());
        }

        ParseResult<FunctionDefinitionNode> parsedFunctionDefinition =
                parseFunctionDefinition(queue.branchOff());
        if (parsedFunctionDefinition.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedFunctionDefinition.getParseResult());
        }

        ParseResult<StructDefinitionLiteralNode> parseStructDefinitionLiteral =
                parseStructDefinition(queue.branchOff());
        if (parseStructDefinitionLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseStructDefinitionLiteral.getParseResult());
        }

        ParseResult<StructInitializationLiteralNode>
                parsedStructInitializationLiteral =
                parseStructInitialization(queue.branchOff());
        if (parsedStructInitializationLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedStructInitializationLiteral.getParseResult());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected a literal!"));
    }

    @CompleteParse
    @Override
    public ParseResult<ArrayLiteralNode> parseArrayLiteral(TokenQueue queue) {
        ArrayLiteralNodeImpl node = astFactory.createArrayLiteralNode();
        List<ExpressionNode> elements = new LinkedList<>();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("[").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '['!"));
        }
        queue.poll();
        // optimization - empty array literal
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            queue.poll();
            node.setElements(elements);
            return ParseResult.successfulParse(node);
        }
        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isUnsuccessful()) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("]"));
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
        }
        queue.mergeBranch();
        elements.add(parsedExpression.getParseResult());

        ParseResult<List<ExpressionNode>> parsedArrayElements =
                programParser.getAuxiliaryParser().parseArgumentList(queue.branchOff());
        if (parsedArrayElements.isUnsuccessful()) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("]"));
            return ParseResult.unsuccessfulParse(parsedArrayElements.getDiagnostic());
        }
        queue.mergeBranch();
        elements.addAll(parsedArrayElements.getParseResult());

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("]").matches(queue.peek())) {
            queue.poll();
            node.setElements(elements);
            return ParseResult.successfulParse(node);
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'!"));
    }

    @CompleteParse
    @Override
    public ParseResult<StructDefinitionLiteralNode> parseStructDefinition(TokenQueue queue) {
        StructDefinitionLiteralNodeImpl node =
                astFactory.createStructDefinitionLiteralNode();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("(").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '('!"));
        }
        queue.poll();

        ParseResult<Map.Entry<TypeNode, Token>> parsedStructField =
                programParser.getAuxiliaryParser().parseFieldDeclaration(queue.branchOff());
        if (parsedStructField.isUnsuccessful()) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get(")"));
            return ParseResult.unsuccessfulParse(parsedStructField.getDiagnostic());
        }
        queue.mergeBranch();

        ParseResult<List<Map.Entry<TypeNode, Token>>> parsedStructFields =
                programParser.getAuxiliaryParser().parseFieldDeclarations(queue.branchOff());
        if (parsedStructFields.isUnsuccessful()) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get(")"));
            return ParseResult.unsuccessfulParse(parsedStructFields.getDiagnostic());
        }
        queue.mergeBranch();

        List<Map.Entry<TypeNode, Token>> fields = new LinkedList<>();
        fields.add(parsedStructField.getParseResult());
        fields.addAll(parsedStructFields.getParseResult());
        node.setFields(fields);

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ')'!"));
        }
        queue.poll();
        return ParseResult.successfulParse(node);
    }

    @CompleteParse
    @Override
    public ParseResult<StructInitializationLiteralNode> parseStructInitialization(TokenQueue queue) {
        StructInitializationLiteralNodeImpl node = astFactory.createStructInitializationLiteralNode();

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("none").matches(queue.peek())) {
            queue.poll();
            node.setArguments(new LinkedList<>());
            return ParseResult.successfulParse(node);
        }

        ParseResult<IdentifierAccessNode> parsedStructType =
                programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
        if (parsedStructType.isUnsuccessful()) {
            return ParseResult.unsuccessfulParse(parsedStructType.getDiagnostic());
        }
        queue.mergeBranch();
        node.setIdentifier(parsedStructType.getParseResult());

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("(").matches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '('!"));
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression =
                programParser.getExpressionParser().parseExpression(queue.branchOff());
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch();
            ParseResult<List<ExpressionNode>> parsedArguments =
                    programParser.getAuxiliaryParser().parseArgumentList(queue.branchOff());
            if (parsedArguments.isSuccessful()) {
                queue.mergeBranch();

                if (SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
                    queue.poll();
                    List<ExpressionNode> arguments = new LinkedList<>();
                    arguments.add(parsedExpression.getParseResult());
                    arguments.addAll(parsedArguments.getParseResult());
                    node.setArguments(arguments);
                    return ParseResult.successfulParse(node);
                }
                return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ')'!"));
            }
            return ParseResult.unsuccessfulParse(parsedArguments.getDiagnostic());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected an expression or 'none'!"));
    }

    @CompleteParse
    @Override
    public ParseResult<FunctionDefinitionNode> parseFunctionDefinition(TokenQueue queue) {
        FunctionDefinitionNodeImpl node = astFactory.createFunctionDefinitionNode();
        List<Map.Entry<TypeNode, Token>> parameterDeclarations = new LinkedList<>();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get("(").matches(queue.peek())) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '('!"));
        }
        queue.poll();

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
            ParseResult<Map.Entry<TypeNode, Token>> parsedFunctionParameter =
                    programParser.getAuxiliaryParser().parseFieldDeclaration(queue.branchOff());
            if (parsedFunctionParameter.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(parsedFunctionParameter.getDiagnostic());
            }
            queue.mergeBranch();
            parameterDeclarations.add(parsedFunctionParameter.getParseResult());

            ParseResult<List<Map.Entry<TypeNode, Token>>> parsedParameterDeclarations =
                    programParser.getAuxiliaryParser().parseFieldDeclarations(queue.branchOff());
            if (parsedParameterDeclarations.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(parsedParameterDeclarations.getDiagnostic());
            }
            queue.mergeBranch();
            parameterDeclarations.addAll(parsedParameterDeclarations.getParseResult());
        }

        if (!SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get("}"));
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ')'!"));
        }
        queue.poll();
        node.setParameters(parameterDeclarations);

        ParseResult<StatementsNode> parsedBody = programParser.getAuxiliaryParser().parseCodeBlock(queue.branchOff());
        if (!parsedBody.isSuccessful()) {
            return ParseResult.unsuccessfulParse(parsedBody.getDiagnostic());
        }
        queue.mergeBranch();
        node.setFunctionBody(parsedBody.getParseResult());
        return ParseResult.successfulParse(node);
    }

    @CompleteParse
    @Override
    public ParseResult<PrimitiveLiteralNode> parsePrimitiveLiteral(TokenQueue queue) {
        Token nextToken = queue.peek();

        if (SyntaxSet.PRIMITIVES.stream().anyMatch(e -> e.matches(nextToken))) {
            queue.poll();
            PrimitiveLiteralNodeImpl node = astFactory.createPrimitiveLiteralNode();
            node.setPrimitiveValue(nextToken);
            return ParseResult.successfulParse(node);
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected a primitive literal"));
    }
}
