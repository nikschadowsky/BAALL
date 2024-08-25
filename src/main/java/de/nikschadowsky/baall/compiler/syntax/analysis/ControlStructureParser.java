package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
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
    PartialParseResult<ControlStructureNode> parseControlStructure(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<ConditionalNode> parseConditional(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<ConditionalNode> parseElseBlock(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<ForLoopNode> parseForLoop(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<WhileLoopNode> parseWhileLoop(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<TryStatementNode> parseTryStatement(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<List<InterceptStatementNode>> parseInterceptStatements(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<InterceptStatementNode> parseInterceptStatement(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<EnsureStatementNode> parseEnsureStatement(TokenQueue queue, ASTNodeFactory astFactory);
}
