package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNode;

import java.util.List;
import java.util.Map;

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
    ParseResult<List<Map.Entry<TypeNode, Token>>> parseFieldDeclarations(TokenQueue queue);

    /**
     * Parses a single field declaration in the format of 'TYPE : IDENTIFIER'.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed field declaration
     */
    @CompleteParse
    ParseResult<Map.Entry<TypeNode, Token>> parseFieldDeclaration(TokenQueue queue);

    /**
     * Parses a list of arguments in the form of
     * {@link ExpressionParser#parseExpression(TokenQueue) expressions} with a leading comma. The list
     * returned can be empty.
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
    ParseResult<Token> parseBinaryOperator(TokenQueue queue);

    /**
     * Parses a unary operator from the {@link de.nikschadowsky.baall.compiler.util.SyntaxSet syntax set}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed unary operator
     */
    @CompleteParse
    ParseResult<Token> parseUnaryOperator(TokenQueue queue);

    /**
     * Parses a shorthand operator from the {@link de.nikschadowsky.baall.compiler.util.SyntaxSet syntax set}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed shorthand operator
     */
    @CompleteParse
    ParseResult<Token> parseShorthandOperator(TokenQueue queue);

    /**
     * Parses an identifier.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed identifier
     */
    @CompleteParse
    ParseResult<Token> parseIdentifier(TokenQueue queue);

    /**
     * Parses a type declaration consisting of a {@link #parseSimpleType(TokenQueue) simple type} and
     * optional {@link #parseArrayTypeDefinition(TokenQueue) array type definitions}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed type
     */
    @CompleteParse
    ParseResult<TypeNode> parseType(TokenQueue queue);

    /**
     * Parses a simple type specified by the {@link de.nikschadowsky.baall.compiler.util.SyntaxSet syntax set}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the simple type
     */
    @CompleteParse
    ParseResult<Token> parseSimpleType(TokenQueue queue);

    /**
     * Parses a list of array type definitions. Each type definition is represented either an
     * {@link ExpressionParser#parseExpression(TokenQueue) expression} node specifying its size or
     * {@code null} to indicate an unknown or inferred size. The list returned can be empty.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the array type definitions
     */
    @CompleteParse
    ParseResult<List<ExpressionNode>> parseArrayTypeDefinition(TokenQueue queue);

    /**
     * Parses a list of array index information. Each index information is represented by an
     * {@link ExpressionParser#parseExpression(TokenQueue) expression} specifying a concrete index. The
     * list returned can be empty.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the array index information
     */
    @CompleteParse
    ParseResult<List<ExpressionNode>> parseArrayIndexInformation(TokenQueue queue);

    /**
     * Parses access to a value referenced by an {@link #parseIdentifier(TokenQueue) identifier} and optional
     * {@link #parseArrayIndexInformation(TokenQueue) array index information}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the identifier access
     */
    @CompleteParse
    ParseResult<IdentifierAccessNode> parseIdentifierAccess(TokenQueue queue);

    /**
     * Parses a list of accesses to values referenced by {@link #parseIdentifierAccess(TokenQueue) identifier accesses}.
     * The list returned can be empty. The result may be partial.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the list of identifier accesses
     */
    @PartialParse
    PartialParseResult<List<IdentifierAccessNode>> parseAdditionalIdentifierAccesses(TokenQueue queue);

    /**
     * Parses a block of {@link StatementParser#parseStatements(TokenQueue) statements} surrounded by
     * '{' curly braces '}'. The result may be partial.
     *
     * @param queue queue of the tokens
     * @return partial parse result of the codeblock
     */
    @PartialParse
    PartialParseResult<StatementsNode> parseCodeBlock(TokenQueue queue);
}
