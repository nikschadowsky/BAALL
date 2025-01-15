package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;

/**
 * @since 25.08.2024
 */
public interface ControlStatementParser {

    @CompleteParse
    ParseResult<ControlStatementNode> parseControlStatement(TokenQueue queue);
}
