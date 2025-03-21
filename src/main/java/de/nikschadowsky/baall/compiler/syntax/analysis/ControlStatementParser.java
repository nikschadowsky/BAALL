package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;

/**
 * @since 25.08.2024
 */
public interface ControlStatementParser {

    /**
     * Parses a control flow statement. A control statement is either a break statement, a continue statement,
     * or a return statement.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed control statement
     */
    @CompleteParse
    ParseResult<ControlStatementNode> parseControlStatement(TokenQueue queue);
}
