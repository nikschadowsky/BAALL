package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.FunctionCallNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ValueNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 11.08.2024
 */
public class ExpressionParser {
    @CompleteParse
    public static ParseResult<ExpressionNode> parseExpression(TokenQueue queue, ASTNodeFactory astFactory) {
        if (Parser.TERMINAL_MAP.get("(").symbolMatches(queue.peek())) {
            queue.poll();

            ParenthesizedExpressionNodeImpl node = astFactory.createParenthesizedExpressionNode();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff(), astFactory);
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                if (Parser.TERMINAL_MAP.get(")").symbolMatches(queue.peek())) {
                    node.setInnerExpression(parsedExpression.getParseResult());
                    return ParseResult.successfulParse(node);
                }
                return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected ')'!"));
            }
            queue.skipOver(Parser.TERMINAL_MAP.get(")"));
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic());
        }

        ParseResult<Token> parsedPrefixOperator = parsePrefixOperator(queue.branchOff(), astFactory);
        if (parsedPrefixOperator.isSuccessful()) {
            queue.mergeBranch();

            PrefixOperationNodeImpl node = astFactory.createPrefixOperationNode();
            node.setOperator(parsedPrefixOperator.getParseResult());

            ParseResult<ExpressionNode> parsedPrefixOperationExpression = parseExpression(queue.branchOff(), astFactory);
            if (parsedPrefixOperationExpression.isSuccessful()) {
                queue.mergeBranch();

                node.setOperand(parsedPrefixOperationExpression.getParseResult());
                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(parsedPrefixOperationExpression.getDiagnostic());
        }

        ParseResult<ValueNode> parsedValue = parseValue(queue.branchOff(), astFactory);
        if (parsedValue.isSuccessful()) {
            queue.mergeBranch();

            ParseResult<Token> parsedBinaryOperator = AuxiliaryParser.parseBinaryOperator(queue.branchOff(), astFactory);
            if (parsedBinaryOperator.isSuccessful()) {
                queue.mergeBranch();

                ParseResult<ExpressionNode> parsedBinaryRightExpression = parseExpression(queue.branchOff(), astFactory);
                if (parsedBinaryRightExpression.isSuccessful()) {
                    queue.mergeBranch();

                    BinaryExpressionNodeImpl node = astFactory.createBinaryExpressionNode();
                    node.setLeftOperand(parsedValue.getParseResult());
                    node.setOperator(parsedBinaryOperator.getParseResult());
                    node.setRightOperand(parsedBinaryRightExpression.getParseResult());
                    return ParseResult.successfulParse(node);
                }
                return ParseResult.unsuccessfulParse(parsedBinaryRightExpression.getDiagnostic());
            }
            return ParseResult.successfulParse(parsedValue.getParseResult());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not an expression!"));
    }

    @CompleteParse
    public static ParseResult<ValueNode> parseValue(TokenQueue queue, ASTNodeFactory astFactory) {
        ParseResult<? extends ValueNode> parseResult;

        parseResult = LiteralParser.parseLiteral(queue.branchOff(), astFactory);
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        parseResult = AuxiliaryParser.parseIdentifierAccess(queue.branchOff(), astFactory);
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        parseResult = parseFunctionCall(queue.branchOff(), astFactory);
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        parseResult = parseUnaryExpression(queue.branchOff(), astFactory);
        if (parseResult.isSuccessful()) {
            queue.mergeBranch();
            return ParseResult.successfulParse(parseResult.getParseResult());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not an expression!"));
    }

    @PartialParse
    public static PartialParseResult<FunctionCallNode> parseFunctionCall(TokenQueue queue, ASTNodeFactory astFactory) {
        FunctionCallNodeImpl node = astFactory.createFunctionCallNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<IdentifierAccessNode> parsedIdentifierValueAccess = AuxiliaryParser.parseIdentifierAccess(queue.branchOff(), astFactory);
1        if (parsedIdentifierValueAccess.isUnsuccessful()) {
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

            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff(), astFactory);
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

    @CompleteParse
    public static ParseResult<UnaryExpressionNode> parseUnaryExpression(TokenQueue queue, ASTNodeFactory astFactory) {
        ParseResult<Token> parsedUnaryOperator;
        ParseResult<IdentifierAccessNode> parsedIdentifier;
        UnaryExpressionNodeImpl node;

        parsedUnaryOperator = AuxiliaryParser.parseUnaryOperator(queue.branchOff(), astFactory);
        if (parsedUnaryOperator.isSuccessful()) {
            queue.mergeBranch();

            node = astFactory.createUnaryExpressionNode();
            node.setIsPrefix(true);
            node.setOperator(parsedUnaryOperator.getParseResult());
            parsedIdentifier = AuxiliaryParser.parseIdentifierAccess(queue.branchOff(), astFactory);
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch();

                node.setIdentifierAccess(parsedIdentifier.getParseResult());
                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic());
        }

        parsedIdentifier = AuxiliaryParser.parseIdentifierAccess(queue.branchOff(), astFactory);
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch();

            node = astFactory.createUnaryExpressionNode();
            node.setIdentifierAccess(parsedIdentifier.getParseResult());
            parsedUnaryOperator = AuxiliaryParser.parseUnaryOperator(queue.branchOff(), astFactory);
            if (parsedUnaryOperator.isSuccessful()) {
                queue.mergeBranch();
                node.setOperator(parsedUnaryOperator.getParseResult());
                node.setIsPrefix(false);

                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(parsedUnaryOperator.getDiagnostic());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Not a statement!"));
    }

    @CompleteParse
    public static ParseResult<Token> parsePrefixOperator(TokenQueue queue, ASTNodeFactory astFactory) {
        Set<Parser.TerminalSymbol> validShorthandOperators =
                Stream.of("+", "-", "!")
                      .map(Parser.TERMINAL_MAP::get)
                      .collect(Collectors.toSet());

        if (validShorthandOperators.stream().anyMatch(e -> e.symbolMatches(queue.peek()))) {
            return ParseResult.successfulParse(queue.poll());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected a prefix operator!"));
    }
}
