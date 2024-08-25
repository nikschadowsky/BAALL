package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.*;

import java.util.List;

/**
 * @since 25.08.2024
 */
public interface StatementParser {
    @PartialParse
    PartialParseResult<StatementsNode> parseStatements(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<StatementNode> parseStatement(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<StatementNode> parseRawStatement(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<DeclarationNode> parseDeclaration(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<VariableDeclarationNode> parseVariableDeclaration(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<ConstantDeclarationNode> parseConstantDeclaration(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<ReassignmentNode> parseReassignment(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<VariableReassignmentNode> parseVariableReassignment(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<List<Token>> parseImports(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<ExportsNode> parseExports(TokenQueue queue, ASTNodeFactory astFactory);
}
