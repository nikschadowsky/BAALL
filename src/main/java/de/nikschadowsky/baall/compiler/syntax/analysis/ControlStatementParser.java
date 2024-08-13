package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.ReturnStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ExceptionCallNode;

/**
 * @since 11.08.2024
 */
public class ControlStatementParser {
    @CompleteParse
    public static ParseResult<ControlStatementNode> parseControlStatement(TokenQueue queue, ASTNodeFactory astFactory) {
        if (Parser.TERMINAL_MAP.get("break").symbolMatches(queue.peek())) {
            queue.poll();
            return ParseResult.successfulParse(astFactory.createBreakStatementNode());
        }
        if (Parser.TERMINAL_MAP.get("continue").symbolMatches(queue.peek())) {
            queue.poll();
            return ParseResult.successfulParse(astFactory.createContinueStatementNode());
        }
        if (Parser.TERMINAL_MAP.get("return").symbolMatches(queue.peek())) {
            queue.poll();
            ReturnStatementNodeImpl node = astFactory.createReturnStatementNode();

            ParseResult<ExpressionNode> parsedExpression =
                    ExpressionParser.parseExpression(queue.branchOff(), astFactory);
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch();

                node.setReturnExpression(parsedExpression.getParseResult());
                return ParseResult.successfulParse(node);
            }

            return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(
                    queue.peek(),
                    "Expected an expression!"
            ));
        }
        if (Parser.TERMINAL_MAP.get("raise").symbolMatches(queue.peek())) {
            queue.poll();
            RaiseStatementNodeImpl node = astFactory.createRaiseStatementNode();

            ParseResult<ExceptionCallNode> parsedExceptionCall =
                    LiteralParser.parseExceptionCall(queue.branchOff(), astFactory);
            if (parsedExceptionCall.isSuccessful()) {
                queue.mergeBranch();
                node.setException(parsedExceptionCall.getParseResult());
                return ParseResult.successfulParse(node);
            }
            return ParseResult.unsuccessfulParse(parsedExceptionCall.getDiagnostic());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.peek(), "Expected control statement!"));
    }
}
