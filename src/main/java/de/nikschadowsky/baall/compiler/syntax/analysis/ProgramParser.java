package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ProgramNode;

/**
 * @since 25.08.2024
 */
public interface ProgramParser {
    @PartialParse
    PartialParseResult<ProgramNode> parseProgram(TokenQueue queue);

    AuxiliaryParser getAuxiliaryParser();

    StatementParser getStatementParser();

    ExpressionParser getExpressionParser();

    ControlStructureParser getControlStructureParser();

    ControlStatementParser getControlStatementParser();

    LiteralParser getLiteralParser();
}
