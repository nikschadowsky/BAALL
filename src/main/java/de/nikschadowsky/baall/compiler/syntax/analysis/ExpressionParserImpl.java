package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 11.08.2024
 */
public class ExpressionParserImpl implements ExpressionParser {

    private final ProgramParser programParser;
    private final ASTNodeFactory astFactory;

    public ExpressionParserImpl(ProgramParser programParser, ASTNodeFactory astFactory) {
        this.programParser = programParser;
        this.astFactory = astFactory;
    }

    @CompleteParse
    @Override
    public ParseResult<ExpressionNode> parseExpression(TokenQueue queue) {
        ParseResult<Token> parsedPrefixOperator =
                programParser.getAuxiliaryParser().parsePrefixOperator(queue.branchOff());
        if (parsedPrefixOperator.isSuccessful()) {
            queue.mergeBranch(parsedPrefixOperator.getTokenQueueId());

            PrefixOperationNodeImpl node = astFactory.createPrefixOperationNode();
            node.setOperator(parsedPrefixOperator.getParseResult());

            ParseResult<ExpressionNode> parsedPrefixOperationExpression = parseExpression(queue.branchOff());
            if (parsedPrefixOperationExpression.isSuccessful()) {
                queue.mergeBranch(parsedPrefixOperationExpression.getTokenQueueId());

                node.setOperand(parsedPrefixOperationExpression.getParseResult());
                return ParseResult.successfulParse(node, queue.getId());
            }
            return ParseResult.unsuccessfulParse(parsedPrefixOperationExpression.getDiagnostic(), queue.getId());
        }

        ParseResult<TermNode> parsedValue = parseTerm(queue.branchOff());
        if (parsedValue.isSuccessful()) {
            queue.mergeBranch(parsedValue.getTokenQueueId());

            ParseResult<Token> parsedBinaryOperator =
                    programParser.getAuxiliaryParser().parseBinaryOperator(queue.branchOff());
            if (parsedBinaryOperator.isSuccessful()) {
                queue.mergeBranch(parsedBinaryOperator.getTokenQueueId());

                ParseResult<ExpressionNode> parsedBinaryRightExpression = parseExpression(queue.branchOff());
                if (parsedBinaryRightExpression.isSuccessful()) {
                    queue.mergeBranch(parsedBinaryRightExpression.getTokenQueueId());

                    BinaryExpressionNodeImpl node = astFactory.createBinaryExpressionNode();
                    node.setLeftOperand(parsedValue.getParseResult());
                    node.setOperator(parsedBinaryOperator.getParseResult());
                    node.setRightOperand(parsedBinaryRightExpression.getParseResult());
                    return ParseResult.successfulParse(node, queue.getId());
                }
                return ParseResult.unsuccessfulParse(parsedBinaryRightExpression.getDiagnostic(), queue.getId());
            }
            return ParseResult.successfulParse(parsedValue.getParseResult(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not an expression!"), queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<TermNode> parseTerm(TokenQueue queue) {
        ParseResult<? extends TermNode> parseResult;

        parseResult = programParser.getLiteralParser().parseLiteral(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return ParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        }

        parseResult = parseParenthesizedExpression(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return ParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        }

        parseResult = parseFunctionCall(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return ParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        }

        parseResult = programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return ParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        }

        parseResult = parseUnaryExpression(queue.branchOff());
        if (parseResult.isSuccessful()) {
            queue.mergeBranch(parseResult.getTokenQueueId());
            return ParseResult.successfulParse(parseResult.getParseResult(), queue.getId());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not an expression!"), queue.getId());
    }

    @CompleteParse
    public ParseResult<ParenthesizedExpressionNode> parseParenthesizedExpression(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("(").matches(queue.peek())) {
            queue.poll();

            ParenthesizedExpressionNodeImpl node = astFactory.createParenthesizedExpressionNode();
            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch(parsedExpression.getTokenQueueId());

                if (SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
                    queue.poll();
                    node.setInnerExpression(parsedExpression.getParseResult());
                    return ParseResult.successfulParse(node, queue.getId());
                }
                return ParseResult.unsuccessfulParse(
                        new SyntaxDiagnostic(queue.poll(), "Expected ')'!"),
                        queue.getId()
                );
            }
            queue.skipOver(SyntaxSet.LANGUAGE_ELEMENTS.get(")"));
            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Expected '('!"), queue.getId());
    }

    @PartialParse
    @Override
    public PartialParseResult<FunctionCallNode> parseFunctionCall(TokenQueue queue) {
        FunctionCallNodeImpl node = astFactory.createFunctionCallNode();
        List<SyntaxDiagnostic> diagnostics = new ArrayList<>();
        boolean isPartial = false;

        ParseResult<IdentifierAccessNode> parsedIdentifierValueAccess =
                programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
        if (parsedIdentifierValueAccess.isUnsuccessful()) {
            return PartialParseResult.unsuccessfulParse(
                    new SyntaxDiagnostic(queue.poll(), "Not a statement!"),
                    queue.getId()
            );
        }
        queue.mergeBranch(parsedIdentifierValueAccess.getTokenQueueId());
        node.setFunctionIdentifier(parsedIdentifierValueAccess.getParseResult());

        if (SyntaxSet.LANGUAGE_ELEMENTS.get("(").matches(queue.peek())) {
            queue.poll();
            if (SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
                queue.poll();
                node.setArguments(new LinkedList<>());
                return PartialParseResult.successfulParse(node, queue.getId());
            }

            ParseResult<ExpressionNode> parsedExpression = parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch(parsedExpression.getTokenQueueId());

                ParseResult<List<ExpressionNode>> parsedFunctionArguments =
                        programParser.getAuxiliaryParser().parseArgumentList(queue.branchOff());
                if (parsedFunctionArguments.isSuccessful()) {
                    queue.mergeBranch(parsedFunctionArguments.getTokenQueueId());

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

            if (!SyntaxSet.LANGUAGE_ELEMENTS.get(")").matches(queue.peek())) {
                diagnostics.add(new SyntaxDiagnostic(queue.peek(), "Expected ')'!"));
                isPartial = true;
                queue.skipTo(SyntaxSet.LANGUAGE_ELEMENTS.get(")"));
            }
            queue.poll();
        } else {
            diagnostics.add(new SyntaxDiagnostic(queue.poll(), "Expected '('!"));
            isPartial = true;
        }

        if (isPartial) {
            return PartialParseResult.partialParse(node, diagnostics.get(0), queue.getId());
        }
        return PartialParseResult.successfulParse(node, queue.getId());
    }

    @CompleteParse
    @Override
    public ParseResult<UnaryExpressionNode> parseUnaryExpression(TokenQueue queue) {
        ParseResult<Token> parsedUnaryOperator;
        ParseResult<IdentifierAccessNode> parsedIdentifier;
        UnaryExpressionNodeImpl node;

        parsedUnaryOperator = programParser.getAuxiliaryParser().parseUnaryOperator(queue.branchOff());
        if (parsedUnaryOperator.isSuccessful()) {
            queue.mergeBranch(parsedUnaryOperator.getTokenQueueId());

            node = astFactory.createUnaryExpressionNode();
            node.setIsPrefix(true);
            node.setOperator(parsedUnaryOperator.getParseResult());
            parsedIdentifier = programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
            if (parsedIdentifier.isSuccessful()) {
                queue.mergeBranch(parsedIdentifier.getTokenQueueId());

                node.setIdentifierAccess(parsedIdentifier.getParseResult());
                return ParseResult.successfulParse(node, queue.getId());
            }
            return ParseResult.unsuccessfulParse(parsedIdentifier.getDiagnostic(), queue.getId());
        }

        parsedIdentifier = programParser.getAuxiliaryParser().parseIdentifierAccess(queue.branchOff());
        if (parsedIdentifier.isSuccessful()) {
            queue.mergeBranch(parsedIdentifier.getTokenQueueId());

            node = astFactory.createUnaryExpressionNode();
            node.setIdentifierAccess(parsedIdentifier.getParseResult());
            parsedUnaryOperator = programParser.getAuxiliaryParser().parseUnaryOperator(queue.branchOff());
            if (parsedUnaryOperator.isSuccessful()) {
                queue.mergeBranch(parsedUnaryOperator.getTokenQueueId());
                node.setOperator(parsedUnaryOperator.getParseResult());
                node.setIsPrefix(false);

                return ParseResult.successfulParse(node, queue.getId());
            }
            return ParseResult.unsuccessfulParse(parsedUnaryOperator.getDiagnostic(), queue.getId());
        }
        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"), queue.getId());
    }
}
