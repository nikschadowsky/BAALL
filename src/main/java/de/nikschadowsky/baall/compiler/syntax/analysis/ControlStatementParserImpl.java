package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.StructInitializationLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

/**
 * @since 11.08.2024
 */
public class ControlStatementParserImpl implements ControlStatementParser {

    private final ProgramParser programParser;
    private final ASTNodeFactory astFactory;

    public ControlStatementParserImpl(ProgramParser programParser, ASTNodeFactory astFactory) {
        this.programParser = programParser;
        this.astFactory = astFactory;
    }

    @CompleteParse
    @Override
    public ParseResult<ControlStatementNode> parseControlStatement(TokenQueue queue) {
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("break").matches(queue.peek())) {
            queue.poll();
            return ParseResult.successfulParse(astFactory.createBreakStatementNode(), queue.getId());
        }
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("continue").matches(queue.peek())) {
            queue.poll();
            return ParseResult.successfulParse(astFactory.createContinueStatementNode(), queue.getId());
        }
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("return").matches(queue.peek())) {
            queue.poll();
            ReturnStatementNodeImpl node = astFactory.createReturnStatementNode();

            ParseResult<ExpressionNode> parsedExpression =
                    programParser.getExpressionParser().parseExpression(queue.branchOff());
            if (parsedExpression.isSuccessful()) {
                queue.mergeBranch(parsedExpression.getTokenQueueId());

                node.setReturnExpression(parsedExpression.getParseResult());
                return ParseResult.successfulParse(node, queue.getId());
            }

            return ParseResult.unsuccessfulParse(parsedExpression.getDiagnostic(), queue.getId());
        }
        if (SyntaxSet.LANGUAGE_ELEMENTS.get("raise").matches(queue.peek())) {
            queue.poll();
            RaiseStatementNodeImpl node = astFactory.createRaiseStatementNode();

            ParseResult<StructInitializationLiteralNode> parsedExceptionCall =
                    programParser.getLiteralParser().parseStructInitialization(queue.branchOff());
            if (parsedExceptionCall.isSuccessful()) {
                queue.mergeBranch(parsedExceptionCall.getTokenQueueId());
                node.setException(parsedExceptionCall.getParseResult());
                return ParseResult.successfulParse(node, queue.getId());
            }
            return ParseResult.unsuccessfulParse(parsedExceptionCall.getDiagnostic(), queue.getId());
        }

        return ParseResult.unsuccessfulParse(new SyntaxDiagnostic(queue.poll(), "Not a statement!"), queue.getId());
    }
}
