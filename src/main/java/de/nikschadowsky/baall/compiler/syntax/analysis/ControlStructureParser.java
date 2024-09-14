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
    /**
     * Parses a control structure. A control structure can either be a
     * {@link #parseConditional(TokenQueue) conditional statement}, a {@link #parseForLoop(TokenQueue) for} or
     * {@link #parseWhileLoop(TokenQueue) while} loop or a {@link #parseTryStatement(TokenQueue) try-intercept-ensure}
     * construct.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed control structure
     */
    @PartialParse
    PartialParseResult<ControlStructureNode> parseControlStructure(TokenQueue queue);

    /**
     * Parses a conditional statement. A conditional statement is either an if- or an else-if statement. An if statement
     * is always in the format of {@code CONDITION ? { BODY }} and an else-if in the format of '| CONDITION ? { BODY
     * }'.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed conditional statement
     */
    @PartialParse
    PartialParseResult<ConditionalNode> parseConditional(TokenQueue queue);

    /**
     * Parses an else statement. An else statement is always in the format of {@code | { BODY }}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed else statement
     */
    @PartialParse
    PartialParseResult<ConditionalNode> parseElseBlock(TokenQueue queue);

    /**
     * Parses a for loop. A for loop is always in the format of {@code for IDENTIFIER = EXPRESSION..EXPRESSION}. There
     * can also be an optional stepper function in the format of {@code :: REASSIGNMENT}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed for loop
     */
    @PartialParse
    PartialParseResult<ForLoopNode> parseForLoop(TokenQueue queue);

    /**
     * Parses a while loop. A while loop is always in the format of {@code while EXPRESSION { BODY }}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed while loop
     */
    @PartialParse
    PartialParseResult<WhileLoopNode> parseWhileLoop(TokenQueue queue);

    /**
     * Parses a try statement. A try statement consists of a try block followed by 1..n intercept statements and an
     * optional ensure block. A try statement follows the following format:
     * {@code try { EXCEPTION_RAISING_STATEMENTS } intercept ... { STATEMENTS } ensure { STATEMENTS }}
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed try construct
     */
    @PartialParse
    PartialParseResult<TryStatementNode> parseTryStatement(TokenQueue queue);

    /**
     * Parses 1..n {@link #parseInterceptStatement(TokenQueue) intercept statements}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed intercept statements
     */
    @PartialParse
    PartialParseResult<List<InterceptStatementNode>> parseInterceptStatements(TokenQueue queue);

    /**
     * Parses an intercept statement. An intercept statement is always in the format of
     * {@code INTERCEPT EXCEPTION_TYPE, ... : EXCEPTION_IDENTIFIER { BODY } }.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the parsed intercept statement
     */
    @PartialParse
    PartialParseResult<InterceptStatementNode> parseInterceptStatement(TokenQueue queue);

    /**
     * Parses an ensure statement. An ensure statement is always in the format of {@code ensure { BODY }}.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the ensure statement
     */
    @PartialParse
    PartialParseResult<EnsureStatementNode> parseEnsureStatement(TokenQueue queue);
}
