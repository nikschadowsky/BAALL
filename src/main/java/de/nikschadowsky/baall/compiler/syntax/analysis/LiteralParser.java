package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

/**
 * @since 25.08.2024
 */
public interface LiteralParser {

    /**
     * Parses a literal. A literal is either a  {@link #parsePrimitiveLiteral primitive value}, an
     * {@link #parseArrayLiteral array}, a {@link #parseStructDefinition struct definition}, a
     * {@link #parseStructNone struct none}, or a {@link #parseFunctionDefinition function definition}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed literal
     */
    @CompleteParse
    ParseResult<LiteralNode> parseLiteral(TokenQueue queue);

    /**
     * Parses a literal representing an array in the format of '[ element1, element2, ... ]'.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed array literal
     */
    @CompleteParse
    ParseResult<ArrayLiteralNode> parseArrayLiteral(TokenQueue queue);

    /**
     * Parses a literal representing a definition of a struct in the format of '( type: field_name, ... )'. A struct
     * definition is a list of 1..n {@link AuxiliaryParser#parseFieldDeclaration(TokenQueue)}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the struct definition
     */
    @CompleteParse
    ParseResult<StructDefinitionLiteralNode> parseStructDefinition(TokenQueue queue);

    /**
     * Parses a struct none. A struct none is the default value for user type structs and can be expressed by 'none' in
     * the source code
     *
     * @param queue queue of the tokens
     * @return complete parse result of the struct none
     */
    @CompleteParse
    ParseResult<StructNoneLiteralNode> parseStructNone(TokenQueue queue);

    /**
     * Parses a function definition in the format of '( type: field_name, ... ) { statements }'. A function definition
     * always consists of 0..n {@link AuxiliaryParser#parseFieldDeclaration(TokenQueue) parameter declarations} and a
     * function body consisting of 0..n {@link StatementParser#parseDelimitedStatement(TokenQueue) statements}.
     *
     * @param queue queue of the tokens
     * @return complete parse of the function definition
     */
    @CompleteParse
    ParseResult<FunctionDefinitionNode> parseFunctionDefinition(TokenQueue queue);

    /**
     * Parses a literal of a primitive value. A primitive value can be a string, a number, a boolean or a character as
     * specified by {@link SyntaxSet}.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed primitive
     */
    @CompleteParse
    ParseResult<PrimitiveLiteralNode> parsePrimitiveLiteral(TokenQueue queue);

}
