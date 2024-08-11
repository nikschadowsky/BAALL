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
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNodeImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 11.08.2024
 */
public class AuxiliaryParser {
    @CompleteParse
    public static ParseResult<List<Map.Entry<TypeNode, Token>>> parseFieldDeclarations(TokenQueue queue, ASTNodeFactory astFactory) {
        if (Parser.TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<Map.Entry<TypeNode, Token>> parsedFieldDeclaration = parseFieldDeclaration(queue.branchOff(), astFactory);
            if (parsedFieldDeclaration.isSuccessful()) {
                queue.mergeBranch();
                ParseResult<List<Map.Entry<TypeNode, Token>>> parsedFunctionParameterList =
                        parseFieldDeclarations(queue.branchOff(), astFactory);
                if (parsedFunctionParameterList.isSuccessful()) {
                    queue.mergeBranch();

                    List<Map.Entry<TypeNode, Token>> parameterFields = new LinkedList<>();
                    parameterFields.add(parsedFieldDeclaration.getParseResult());
                    parameterFields.addAll(parsedFunctionParameterList.getParseResult());
                    return ParseResult.successfulParse(parameterFields);
                }
                return ParseResult.unsuccessfulParse(parsedFieldDeclaration.getDiagnostic());
            }
            return ParseResult.unsuccessfulParse(parsedFieldDeclaration.getDiagnostic());
        }
        return ParseResult.successfulParse(new LinkedList<>());
    }

    @CompleteParse
    public static ParseResult<Map.Entry<TypeNode, Token>> parseFieldDeclaration(TokenQueue queue, ASTNodeFactory astFactory) {
        ParseResult<TypeNode> parsedTypeNode = parseType(queue.branchOff(), astFactory);
        if (parsedTypeNode.isSuccessful()) {
            queue.mergeBranch();
            if (Parser.TERMINAL_MAP.get(":").symbolMatches(queue.peek())) {
                queue.poll();
                ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff(), astFactory);
                if (parsedIdentifier.isSuccessful()) {
                    queue.mergeBranch();
                    return ParseResult.successfulParse(Map.entry(
                            parsedTypeNode.getParseResult(),
                            parsedIdentifier.getParseResult()
                    ));
                }
                return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic());
            }
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ':'!"));
        }
        return ParseResult.unsuccessfulParse(parsedTypeNode.getDiagnostic());
    }

    @CompleteParse
    public static ParseResult<List<ExpressionNode>> parseArgumentList(TokenQueue queue, ASTNodeFactory astFactory) {
        if (Parser.TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<ExpressionNode> parsedExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();
                ParseResult<List<ExpressionNode>> parsedArguments = parseArgumentList(queue.branchOff(), astFactory);
                if (parsedArguments.isSuccessful()) {
                    queue.mergeBranch();

                    List<ExpressionNode> arguments = new LinkedList<>();
                    arguments.add(parsedExpression.getParseResult());
                    arguments.addAll(parsedArguments.getParseResult());
                    return ParseResult.successfulParse(arguments);
                }
                return ParseResult.unsuccessfulParse(parsedArguments.getDiagnostic());
            }
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
        }
        return ParseResult.successfulParse(new LinkedList<>());
    }

    @CompleteParse
    public static ParseResult<Token> parseBinaryOperator(TokenQueue queue, ASTNodeFactory astFactory) {
        // todo use syntax set
        Set<Parser.TerminalSymbol> validBinaryOperators =
                Stream.of("+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>", "==", "<", ">", "&&", "||")
                      .map(Parser.TERMINAL_MAP::get)
                      .collect(Collectors.toSet());

        if (validBinaryOperators.stream().anyMatch(e -> e.symbolMatches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected an operator!"));
    }

    @CompleteParse
    public static ParseResult<Token> parseUnaryOperator(TokenQueue queue, ASTNodeFactory astFactory) {
        Set<Parser.TerminalSymbol> validShorthandOperators =
                Stream.of("++", "--")
                      .map(Parser.TERMINAL_MAP::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.symbolMatches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a unary operator!"));
    }

    @CompleteParse
    public static ParseResult<Token> parseShorthandOperator(TokenQueue queue, ASTNodeFactory astFactory) {
        Set<Parser.TerminalSymbol> validShorthandOperators =
                Stream.of("=", ":=", "+=", "-=", "*=", "/=", "&=", "|=", "^=")
                      .map(Parser.TERMINAL_MAP::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.symbolMatches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(
                queue.peek(),
                "Expected a assignment operator!"
        ));
    }

    /**
     * Parses an identifier token and writes its value into the passed node.
     *
     * @param queue
     * @param node
     * @return a parse result containing the filled passed node on success, or an unsuccessful parse on failure.
     */
    @CompleteParse
    public static ParseResult<Token> parseIdentifier(TokenQueue queue, ASTNodeFactory astFactory) {
        if (Parser.TERMINAL_MAP.get("_IDENTIFIER").symbolMatches(queue.peek())) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected an identifier!"));
    }

    @CompleteParse
    public static ParseResult<TypeNode> parseType(TokenQueue queue, ASTNodeFactory astFactory) {
        TypeNodeImpl node = astFactory.createTypeNode();
        ParseResult<Token> parsedSimpleType = parseSimpleType(queue.branchOff(), astFactory);
        if (parsedSimpleType.isSuccessful()) {
            queue.mergeBranch();

            node.setType(parsedSimpleType.getParseResult());
            ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                    parseArrayTypeDefinitions(queue.branchOff(), astFactory);
            if (parsedArrayTypeDefinitions.isSuccessful()) {
                queue.mergeBranch();

                node.setArrayDimensionDefinitions(parsedArrayTypeDefinitions.getParseResult());
                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a type!"));
    }

    @CompleteParse
    public static ParseResult<Token> parseSimpleType(TokenQueue queue, ASTNodeFactory astFactory) {
        Set<Parser.TerminalSymbol> validSimpleTypes = Stream.of("string", "number", "boolean", "struct", "function")
                                                            .map(Parser.TERMINAL_MAP::get)
                                                            .collect(Collectors.toSet());
        Token nextToken = queue.peek();

        if (validSimpleTypes.stream().anyMatch(e -> e.symbolMatches(nextToken))) {
            return ParseResult.successfulParse(queue.poll());
        } else {
            // check if type is identifier
            ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff(), astFactory);
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch();
                return ParseResult.successfulParse(parsedIdentifier.getParseResult());
            }
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a type!"));
    }

    /**
     * Parses optional array type definition on types
     *
     * @param queue
     * @return
     */
    @CompleteParse
    public static ParseResult<List<ExpressionNode>> parseArrayTypeDefinitions(TokenQueue queue, ASTNodeFactory astFactory) {
        List<ExpressionNode> arrayDimensions = new LinkedList<>();

        if (!Parser.TERMINAL_MAP.get("[").symbolMatches(queue.peek())) {
            return ParseResult.successfulParse(new LinkedList<>());
        }
        queue.poll();

        ParseResult<ExpressionNode> parsedExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
        if (parsedExpression.isSuccessful()) {
            queue.mergeBranch();
            if (Parser.TERMINAL_MAP.get("]").symbolMatches(queue.peek())) {
                queue.poll();
                ParseResult<List<ExpressionNode>> parsedArrayTypeDefinitions =
                        parseArrayTypeDefinitions(queue.branchOff(), astFactory);
                if (parsedArrayTypeDefinitions.isSuccessful()) {
                    queue.mergeBranch();

                    List<ExpressionNode> arrayTypeDefinitions = new LinkedList<>();
                    arrayTypeDefinitions.add(parsedExpression.getParseResult());
                    arrayTypeDefinitions.addAll(parsedArrayTypeDefinitions.getParseResult());

                    return ParseResult.successfulParse(arrayTypeDefinitions);
                }
                return ParseResult.unsuccessfulParse(parsedArrayTypeDefinitions.getDiagnostic());
            }
            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'!"));
        }
        queue.skipOver(Parser.TERMINAL_MAP.get("]"));
        return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
    }

    /**
     * Parses an access of a value through an identifier with optional array indexing.
     *
     * @param queue
     * @return
     */
    @CompleteParse
    public static ParseResult<IdentifierAccessNode> parseIdentifierAccess(TokenQueue queue, ASTNodeFactory astFactory) {
        IdentifierAccessNodeImpl node = astFactory.createIdentifierAccessNode();
        ParseResult<Token> parsedIdentifier = parseIdentifier(queue.branchOff(), astFactory);
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch();
            node.setIdentifier(parsedIdentifier.getParseResult());
            ParseResult<List<ExpressionNode>> parsedOptionalArrayIndices =
                    parseOptionalArrayIndex(queue.branchOff(), astFactory);
            if (parsedOptionalArrayIndices.isSuccessful()) {
                queue.mergeBranch();
                node.setArrayIndexes(parsedOptionalArrayIndices.getParseResult());
                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(parsedOptionalArrayIndices.getDiagnostic());
        }
        return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic());
    }

    /**
     * @param queue
     * @return
     */
    @CompleteParse
    public static ParseResult<List<ExpressionNode>> parseOptionalArrayIndex(TokenQueue queue, ASTNodeFactory astFactory) {
        if (Parser.TERMINAL_MAP.get("[").symbolMatches(queue.peek())) {
            queue.poll();

            ParseResult<ExpressionNode> parsedExpression = ExpressionParser.parseExpression(queue.branchOff(), astFactory);
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                if (Parser.TERMINAL_MAP.get("]").symbolMatches(queue.peek())) {
                    queue.poll();
                    ParseResult<List<ExpressionNode>> parsedOptionalArrayIndex =
                            parseOptionalArrayIndex(queue.branchOff(), astFactory);

                    if (parsedOptionalArrayIndex.isSuccessful()) {
                        queue.mergeBranch();

                        List<ExpressionNode> arrayIndices = new LinkedList<>();
                        arrayIndices.add(parsedExpression.getParseResult());
                        arrayIndices.addAll(parsedOptionalArrayIndex.getParseResult());
                        return ParseResult.successfulParse(arrayIndices);
                    }
                    return ParseResult.unsuccessfulParse(parsedOptionalArrayIndex.getDiagnostic());
                }
                return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ']'"));
            }
            queue.skipOver(Parser.TERMINAL_MAP.get("]"));
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
        }
        // epsilon
        return ParseResult.successfulParse(new LinkedList<>());
    }

    /**
     * Parses 2..n exported elements and writes them into the passed node
     *
     * @param queue
     * @param node
     * @return
     */
    @PartialParse
    public static PartialParseResult<List<IdentifierAccessNode>> parseIdentifierAccesses(TokenQueue queue, ASTNodeFactory astFactory) {
        boolean isPartial = false;
        if (Parser.TERMINAL_MAP.get(",").symbolMatches(queue.peek())) {
            queue.poll();
            ParseResult<IdentifierAccessNode> parsedIdentifierAccess = parseIdentifierAccess(queue.branchOff(), astFactory);
            if (parsedIdentifierAccess.isSuccessful()) {
                queue.mergeBranch();
                PartialParseResult<List<IdentifierAccessNode>> parsedAdditionalExports =
                        parseIdentifierAccesses(queue.branchOff(), astFactory);
                if (parsedAdditionalExports.isSuccessful()) {
                    queue.mergeBranch();


                    List<IdentifierAccessNode> exports = new LinkedList<>();
                    exports.add(parsedIdentifierAccess.getParseResult());
                    exports.addAll(parsedAdditionalExports.getParseResult());

                    return PartialParseResult.successfulParse(exports);
                } else if (parsedAdditionalExports.isPartial()) {
                    return PartialParseResult.partialParse(new LinkedList<>(), parsedAdditionalExports.getDiagnostic());
                }
                return PartialParseResult.unsuccessfulParse(parsedAdditionalExports.getDiagnostic());
            }

            return PartialParseResult.unsuccessfulParse(parsedIdentifierAccess.getDiagnostic());
        }
        // epsilon
        return PartialParseResult.successfulParse(new LinkedList<>());
    }
}
