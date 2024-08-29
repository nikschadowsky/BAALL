package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.UnaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ValueNode;

/**
 * @since 25.08.2024
 */
public interface ExpressionParser {
    @CompleteParse
    ParseResult<ExpressionNode> parseExpression(TokenQueue queue);

    @CompleteParse
    ParseResult<ValueNode> parseValue(TokenQueue queue);

    @PartialParse
    PartialParseResult<FunctionCallNode> parseFunctionCall(TokenQueue queue);

    @CompleteParse
    ParseResult<UnaryExpressionNode> parseUnaryExpression(TokenQueue queue);

    @CompleteParse
    ParseResult<Token> parsePrefixOperator(TokenQueue queue);
}
