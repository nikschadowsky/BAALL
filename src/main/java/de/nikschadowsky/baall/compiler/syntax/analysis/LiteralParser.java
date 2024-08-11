package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ExceptionCallNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.FunctionCallNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 11.08.2024
 */
public class LiteralParser {
    @CompleteParse
    public static ParseResult<ArrayLiteralNode> parseArrayLiteral(TokenQueue queue, ASTNodeFactory astFactory) {
        ArrayLiteralNodeImpl node = astFactory.createArrayLiteralNode();
        List<ExpressionNode> elements = new LinkedList<>();

        if (!Parser.TERMINAL_MAP.get("[").symbolMatches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '['!"));
        }
        queue.poll();
        // optimization - empty array literal
        if (Parser.TERMINAL_MAP.get("]").symbolMatches(queue.peek())) {
            queue.poll();
            node.setElements(elements);
            return ParseResult.successfulParse(node);
        }
        ParseResult<ExpressionNode> parsedExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
        if (parsedExpression.isUnsuccessful()) {
            queue.skipOver(Parser.TERMINAL_MAP.get("]"));
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
        }
        queue.mergeBranch();
        elements.add(parsedExpression.getParseResult());

        ParseResult<List<ExpressionNode>> parsedArrayElements = AuxiliaryParser.parseArgumentList(queue.branchOff(), astFactory);
        if (parsedArrayElements.isUnsuccessful()) {
            queue.skipOver(Parser.TERMINAL_MAP.get("]"));
            return ParseResult.unsuccessfulParse(parsedArrayElements.getDiagnostic());
        }
        queue.mergeBranch();
        elements.addAll(parsedArrayElements.getParseResult());

        if (Parser.TERMINAL_MAP.get("]").symbolMatches(queue.peek())) {
            node.setElements(elements);
            return ParseResult.successfulParse(node);
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'!"));
    }

    @CompleteParse
    public static ParseResult<StructDefinitionLiteralNode> parseStructDefinition(TokenQueue queue, ASTNodeFactory astFactory) {
        StructDefinitionLiteralNodeImpl node =
                astFactory.createStructDefinitionLiteralNode();

        if (!Parser.TERMINAL_MAP.get("(").symbolMatches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected '('!"));
        }
        queue.poll();

        ParseResult<Map.Entry<TypeNode, Token>> parsedStructField = AuxiliaryParser.parseFieldDeclaration(queue.branchOff(), astFactory);
        if (parsedStructField.isUnsuccessful()) {
            queue.skipOver(Parser.TERMINAL_MAP.get(")"));
            return ParseResult.unsuccessfulParse(parsedStructField.getDiagnostic());
        }
        queue.mergeBranch();

        ParseResult<List<Map.Entry<TypeNode, Token>>> parsedStructFields =
                AuxiliaryParser.parseFieldDeclarations(queue.branchOff(), astFactory);
        if (parsedStructFields.isUnsuccessful()) {
            queue.skipOver(Parser.TERMINAL_MAP.get(")"));
            return ParseResult.unsuccessfulParse(parsedStructFields.getDiagnostic());
        }
        queue.mergeBranch();

        List<Map.Entry<TypeNode, Token>> fields = new LinkedList<>();
        fields.add(parsedStructField.getParseResult());
        fields.addAll(parsedStructFields.getParseResult());
        node.setFields(fields);

        if (!Parser.TERMINAL_MAP.get(")").symbolMatches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ')'!"));
        }
        queue.poll();
        return ParseResult.successfulParse(node);
    }

    @CompleteParse
    public static ParseResult<StructInitializationLiteralNode> parseStructInitialization(TokenQueue queue, ASTNodeFactory astFactory) {
        StructInitializationLiteralNodeImpl node = astFactory.createStructInitializationLiteralNode();

        if (!Parser.TERMINAL_MAP.get("(").symbolMatches(queue.peek())) {
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '('!"));
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch();
            ParseResult<List<ExpressionNode>> parsedArguments = AuxiliaryParser.parseArgumentList(queue.branchOff(), astFactory);
            if (parsedArguments.isSuccessful()) {
                queue.mergeBranch();

                if (Parser.TERMINAL_MAP.get(")").symbolMatches(queue.peek())) {
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
        } else if (Parser.TERMINAL_MAP.get("none").symbolMatches(queue.peek())) {
            queue.poll();
            return ParseResult.successfulParse(astFactory.createStructInitializationNone());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected expression or 'none'!"));
    }

    @CompleteParse
    public static ParseResult<FunctionDefinitionNode> parseFunctionDefinition(TokenQueue queue, ASTNodeFactory astFactory) {
        FunctionDefinitionNodeImpl node = astFactory.createFunctionDefinitionNode();
        List<Map.Entry<TypeNode, Token>> parameters = new LinkedList<>();

        if (Parser.TERMINAL_MAP.get("{").symbolMatches(queue.peek())) {
            queue.poll();
            node.setParameters(new LinkedList<>());

            ParseResult<StatementsNode> parsedStatements = Parser.parseStatements(queue.branchOff(), astFactory);
            if (!parsedStatements.isSuccessful()) {
                queue.skipOver(Parser.TERMINAL_MAP.get("}"));
                return ParseResult.unsuccessfulParse(parsedStatements.getDiagnostic());
            }
            queue.mergeBranch();
            node.setFunctionBody(parsedStatements.getParseResult());

            if (!Parser.TERMINAL_MAP.get("}").symbolMatches(queue.peek())) {
                return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
            }
            return ParseResult.successfulParse(node);

        }
        ParseResult<Map.Entry<TypeNode, Token>> parsedFunctionParameter =
                AuxiliaryParser.parseFieldDeclaration(queue.branchOff(), astFactory);
        if (parsedFunctionParameter.isSuccessful()) {
            queue.mergeBranch();
            ParseResult<List<Map.Entry<TypeNode, Token>>> parsedFunctionParameters =
                    AuxiliaryParser.parseFieldDeclarations(queue.branchOff(), astFactory);
            if (parsedFunctionParameters.isUnsuccessful()) {
                return ParseResult.unsuccessfulParse(parsedFunctionParameters.getDiagnostic());
            }
            queue.mergeBranch();
            parameters.addAll(parsedFunctionParameters.getParseResult());
            parameters.add(parsedFunctionParameter.getParseResult());
            node.setParameters(parameters);
            if (Parser.TERMINAL_MAP.get("{").symbolMatches(queue.poll())) {
                ParseResult<StatementsNode> parsedStatements = Parser.parseStatements(queue.branchOff(), astFactory);
                if (parsedStatements.isUnsuccessful()) {
                    return ParseResult.unsuccessfulParse(parsedStatements.getDiagnostic());
                }
                queue.mergeBranch();
                node.setFunctionBody(parsedStatements.getParseResult());
                if (!Parser.TERMINAL_MAP.get("}").symbolMatches(queue.peek())) {
                    return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '}'!"));
                }
                queue.poll();
                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '{'!"));
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(
                queue.poll(),
                "Expected function parameter definition or '{'!"
        ));
    }

    @CompleteParse
    public static ParseResult<LiteralNode> parseLiteral(TokenQueue queue, ASTNodeFactory astFactory) {
        ParseResult<PrimitiveLiteralNode> parsedPrimitiveLiteral =
                parsePrimitiveLiteral(queue.branchOff(), astFactory);
        if (parsedPrimitiveLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedPrimitiveLiteral.getParseResult());
        }

        ParseResult<ArrayLiteralNode> parsedArrayLiteral = parseArrayLiteral(queue.branchOff(), astFactory);
        if (parsedArrayLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedArrayLiteral.getParseResult());
        }

        ParseResult<StructDefinitionLiteralNode> parseStructDefinitionLiteral =
                parseStructDefinition(queue.branchOff(), astFactory);
        if (parseStructDefinitionLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseStructDefinitionLiteral.getParseResult());
        }

        ParseResult<StructInitializationLiteralNode>
                parsedStructInitializationLiteral =
                parseStructInitialization(queue.branchOff(), astFactory);
        if (parsedStructInitializationLiteral.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedStructInitializationLiteral.getParseResult());
        }

        ParseResult<FunctionDefinitionNode> parsedFunctionDefinition = parseFunctionDefinition(queue.branchOff(), astFactory);
        if (parsedFunctionDefinition.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parsedFunctionDefinition.getParseResult());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a literal!"));
    }

    @CompleteParse
    public static ParseResult<PrimitiveLiteralNode> parsePrimitiveLiteral(TokenQueue queue, ASTNodeFactory astFactory) {
        Set<Parser.TerminalSymbol> validPrimitives = Stream.of("_STRING", "_NUMBER", "_BOOLEAN")
                                                           .map(Parser.TERMINAL_MAP::get)
                                                           .collect(Collectors.toSet());
        Token nextToken = queue.peek();

        if (validPrimitives.stream().anyMatch(e -> e.symbolMatches(nextToken))) {
            queue.poll();
            PrimitiveLiteralNodeImpl node = astFactory.createPrimitiveLiteralNode();
            node.setPrimitiveValue(nextToken);
            return ParseResult.successfulParse(node);
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a primitive literal"));
    }

    @PartialParse
    public static PartialParseResult<ExceptionCallNode> parseExceptionCall(TokenQueue queue, ASTNodeFactory astFactory) {
        FunctionCallNodeImpl node = astFactory.createFunctionCallNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<IdentifierAccessNode> parsedIdentifierValueAccess = AuxiliaryParser.parseIdentifierAccess(queue.branchOff(), astFactory);
        if (parsedIdentifierValueAccess.isSuccessful()) {
            return PartialParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Not a statement!"));
        }
        queue.mergeBranch();
        node.setFunctionIdentifier(parsedIdentifierValueAccess.getParseResult());

        if (Parser.TERMINAL_MAP.get("(").symbolMatches(queue.peek())) {
            queue.poll();
            if (Parser.TERMINAL_MAP.get(")").symbolMatches(queue.peek())) {
                queue.poll();
                node.setArguments(new LinkedList<>());
                return PartialParseResult.successfulParse(node);
            }

            ParseResult<ExpressionNode> parsedExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                ParseResult<List<ExpressionNode>> parsedFunctionArguments =
                        AuxiliaryParser.parseArgumentList(queue.branchOff(), astFactory);
                if (parsedFunctionArguments.isSuccessful()) {
                    queue.mergeBranch();

                    List<ExpressionNode> functionArguments = new LinkedList<>();
                    functionArguments.add(parsedExpression.getParseResult());
                    functionArguments.addAll(parsedFunctionArguments.getParseResult());
                    node.setArguments(functionArguments);
                } else {
                    diagnostics.add(parsedFunctionArguments.getDiagnostic());
                    isPartial = true;
                }
            } else {
                diagnostics.add(parsedExpression.getDiagnostic());
                isPartial = true;
            }

            if (!Parser.TERMINAL_MAP.get(")").symbolMatches(queue.peek())) {
                diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected ')'!"));
                isPartial = true;
                queue.skipOver(Parser.TERMINAL_MAP.get(")"));
            }

        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '('!"));
            isPartial = true;
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0));
        }
        return PartialParseResult.successfulParse(node);
    }
}
