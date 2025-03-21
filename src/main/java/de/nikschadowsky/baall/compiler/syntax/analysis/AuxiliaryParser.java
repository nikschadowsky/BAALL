package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.OperatorNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;

import java.util.List;

/**
 * @since 25.08.2024
 */
public interface AuxiliaryParser {
    /**
     * Parses a list of {@link #parseFieldDeclaration(TokenQueue)  field declarations} with a
     * <b>leading</b> comma. The list may be empty.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed field declarations
     */
    @CompleteParse
    ParseResult<List<FieldNode>> parseFieldDeclarations(TokenQueue queue);

    /**
     * Parses a single field declaration in the format of 'TYPE : IDENTIFIER'.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed field declaration
     */
    @CompleteParse
    ParseResult<FieldNode> parseFieldDeclaration(TokenQueue queue);

    /**
     * Parses a list of arguments in the form of {@link ExpressionParser#parseExpression(TokenQueue) expressions} with a
     * leading comma. The list returned can be empty.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed expressions
     */
    @CompleteParse
    ParseResult<List<ExpressionNode>> parseArgumentList(TokenQueue queue);

    /**
     * Parses a binary operator from the {@link de.nikschadowsky.baall.compiler.util.SyntaxSet syntax set}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed binary operator
     */
    @CompleteParse
    ParseResult<OperatorNode> parseBinaryOperator(TokenQueue queue);

    /**
     * Parses a unary operator from the {@link de.nikschadowsky.baall.compiler.util.SyntaxSet syntax set}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed unary operator
     */
    @CompleteParse
    ParseResult<OperatorNode> parseUnaryOperator(TokenQueue queue);

    /**
     * Parses a variable assignment operator from the
     * {@link de.nikschadowsky.baall.compiler.util.SyntaxSet syntax set}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed shorthand operator
     */
    @CompleteParse
    ParseResult<OperatorNode> parseVariableAssignmentOperator(TokenQueue queue);

    /**
     * Parses a prefix operator from the {@link de.nikschadowsky.baall.compiler.util.SyntaxSet syntax set}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed prefix operator
     */
    @CompleteParse
    ParseResult<OperatorNode> parsePrefixOperator(TokenQueue queue);

    /**
     * Parses an identifier.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed identifier
     */
    @CompleteParse
    ParseResult<IdentifierNode> parseIdentifier(TokenQueue queue);

    /**
     * Parses a type declaration consisting of a base or custom type and optional
     * {parseArrayTypeDefinition(TokenQueue) array type definitions}. Custom types can be flagged as none-safe.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed type
     */
    @CompleteParse
    ParseResult<TypeNode> parseType(TokenQueue queue);

    /**
     * Parses a comma separated list of parsed types. The first element must be preceded by a comma, or an empty list
     * will be returned.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed types
     */
    @CompleteParse
    ParseResult<List<TypeNode>> parseAdditionalTypes(TokenQueue queue);

    /**
     * Parses a list of array index information. Each index information is represented by an
     * {@link ExpressionParser#parseExpression(TokenQueue) expression} specifying a concrete index. The list returned
     * can be empty.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the array index information
     */
    @CompleteParse
    ParseResult<List<ExpressionNode>> parseListIndexInformation(TokenQueue queue);

    /**
     * Parses a single list index in the format of {@code '[' EXPR ']'}.
     * @param queue queue of the tokens
     * @return complete parse result of the list index
     */
    @CompleteParse
    ParseResult<ExpressionNode> parseListIndex(TokenQueue queue);

    /**
     * Parses access to an element. This method parses all element accesses, with no regards to complexity.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the element access
     */
    @CompleteParse
    ParseResult<ElementAccessNode> parseElementAccess(TokenQueue queue);

    /**
     * Parses a comma separated list of element accesses. The first element access must be preceded by a comma, or an
     * empty list will be returned.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed element accesses
     */
    @PartialParse
    PartialParseResult<List<ElementAccessNode>> parseAdditionalElementAccesses(TokenQueue queue);

    /**
     * Parses a block of {@link StatementParser#parseStatements(TokenQueue) statements} surrounded by '{' curly braces
     * '}'. The result may be partial.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the codeblock
     */
    @PartialParse
    PartialParseResult<StatementsNode> parseCodeBlock(TokenQueue queue);
}
