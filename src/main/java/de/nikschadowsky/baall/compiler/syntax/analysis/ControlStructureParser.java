package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.ConditionalNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.ControlStructureNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.ForLoopNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;

import java.util.List;

/**
 * @since 25.08.2024
 */
public interface ControlStructureParser {
    @PartialParse
    PartialParseResult<ControlStructureNode> parseControlStructure(TokenQueue queue);

    @PartialParse
    PartialParseResult<ConditionalNode> parseConditional(TokenQueue queue);

    @PartialParse
    PartialParseResult<ConditionalNode> parseElseBlock(TokenQueue queue);

    @PartialParse
    PartialParseResult<ForLoopNode> parseForLoop(TokenQueue queue);

    @PartialParse
    PartialParseResult<WhileLoopNode> parseWhileLoop(TokenQueue queue);

    @PartialParse
    PartialParseResult<TryStatementNode> parseTryStatement(TokenQueue queue);

    @PartialParse
    PartialParseResult<List<InterceptStatementNode>> parseInterceptStatements(TokenQueue queue);

    @PartialParse
    PartialParseResult<InterceptStatementNode> parseInterceptStatement(TokenQueue queue);

    @PartialParse
    PartialParseResult<EnsureStatementNode> parseEnsureStatement(TokenQueue queue);
}
