package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ParenthesizedExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.UnaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.TermNode;

/**
 * @since 25.08.2024
 */
public interface ExpressionParser {

    /**
     * Parses an expression. An expression can either be a binary expression, a prefix operation, a
     * {@link #parseTerm(TokenQueue) value} or another expression wrapped in parentheses.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed expression
     */
    @CompleteParse
    ParseResult<ExpressionNode> parseExpression(TokenQueue queue);

    /**
     * Parses a value. A value can be either a literal, an identifier access, a function call or a unary expression.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed value
     */
    @CompleteParse
    ParseResult<TermNode> parseTerm(TokenQueue queue);

    /**
     * Parses an expression wrapped in parentheses in the format of '( expression )'.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed expression wrapped in parentheses
     */
    @CompleteParse
    ParseResult<ParenthesizedExpressionNode> parseParenthesizedExpression(TokenQueue queue);

    /**
     * Parses a function call. This parse result can be partial.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed function call.
     */
    @PartialParse
    PartialParseResult<FunctionCallNode> parseFunctionCall(TokenQueue queue);

    /**
     * Parses a unary expression in the format of '++IDENTIFIER' or 'IDENTIFIER++'.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed unary expression
     */
    @CompleteParse
    ParseResult<UnaryExpressionNode> parseUnaryExpression(TokenQueue queue);

}
