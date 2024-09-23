package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.*;

import java.util.List;

/**
 * @since 25.08.2024
 */
public interface StatementParser {

    /**
     * Parses a list of consecutive {@link #parseDelimitedStatement(TokenQueue) statements}. The list may be empty.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed statements
     */
    @PartialParse
    PartialParseResult<StatementsNode> parseStatements(TokenQueue queue);

    /**
     * Parses a single statement terminated by a semicolon or a closing curly bracket. A statement is either a
     * {@link #parseDeclaration(TokenQueue) declaration}, a {@link #parseReassignment(TokenQueue) reassignment}, a
     * {@link ExpressionParser#parseFunctionCall(TokenQueue) function call}, a
     * {@link ControlStatementParser#parseControlStatement(TokenQueue) control statement} or a
     * {@link ControlStructureParser#parseControlStructure(TokenQueue) control structure}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed statement
     */
    @PartialParse
    PartialParseResult<StatementNode> parseDelimitedStatement(TokenQueue queue);

    /**
     * @param queue
     * @return
     */
    @PartialParse
    PartialParseResult<StatementNode> parseStatement(TokenQueue queue);

    /**
     * Parses a declaration of a {@link #parseVariableDeclaration(TokenQueue) variable} or
     * {@link #parseConstantDeclaration(TokenQueue) constant}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed declaration
     */
    @PartialParse
    PartialParseResult<DeclarationNode> parseDeclaration(TokenQueue queue);

    /**
     * Parses a declaration of a variable. A variable declaration is always in the format of
     * {@code 'TYPE : NAME = EXPRESSION'}. The assignment of an expression is optional.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed variable declaration
     */
    @PartialParse
    PartialParseResult<VariableDeclarationNode> parseVariableDeclaration(TokenQueue queue);

    /**
     * Parses a declaration of a constant. A constant declaration is always in the format of
     * {@code 'TYPE : NAME := EXPRESSION'}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed constant declaration
     */
    @PartialParse
    PartialParseResult<ConstantDeclarationNode> parseConstantDeclaration(TokenQueue queue);

    /**
     * Parses a reassignment statement. A reassignment statement is either a
     * {@link #parseVariableReassignment(TokenQueue) variable reassignment} or a
     * {@link ExpressionParser#parseUnaryExpression(TokenQueue) unary expression}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed reassignment statement
     */
    @PartialParse
    PartialParseResult<ReassignmentNode> parseReassignment(TokenQueue queue);

    /**
     * Parses a reassignment statement of a variable. A variable reassignment is always in the format of
     * {@link 'IDENTIFIER ASSIGNMENT_OPERATOR EXPRESSION'}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed variable reassignment
     */
    @PartialParse
    PartialParseResult<VariableReassignmentNode> parseVariableReassignment(TokenQueue queue);

    /**
     * Parses a list of consecutive import statements. An import statement is always in the format of
     * {@code 'use STRING;'}. It parses 0..n import statements.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed import statements
     */
    @PartialParse
    PartialParseResult<List<Token>> parseImports(TokenQueue queue);

    /**
     * Parses all exported elements. An export statement is always in the format of
     * {@code 'export { IDENTIFIER_ACCESS, ... };'}. It parses 0..n identifier accesses.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed export statements
     */
    @PartialParse
    PartialParseResult<ExportsNode> parseExports(TokenQueue queue);
}
