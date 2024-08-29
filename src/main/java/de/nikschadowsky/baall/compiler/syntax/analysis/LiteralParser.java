package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ExceptionCallNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal.*;

/**
 * @since 25.08.2024
 */
public interface LiteralParser {

    /**
     * Parses a literal representing an array in the format of '[ element1, element2, element3 ]'.
     *
     * @param queue queue of the tokens
     * @return complete parse result of the parsed array literal
     */
    @CompleteParse
    ParseResult<ArrayLiteralNode> parseArrayLiteral(TokenQueue queue);

    /**
     * Parses a literal representing a definition of a struct in the format of '( type: field_name )'. A struct
     * definition is a list of {@link AuxiliaryParser#parseFieldDeclaration(TokenQueue)}.
     *
     * @param queue queue of the tokens
     * @return
     */
    @CompleteParse
    ParseResult<StructDefinitionLiteralNode> parseStructDefinition(TokenQueue queue);

    /**
     * @param queue
     * @return
     */
    @CompleteParse
    ParseResult<StructInitializationLiteralNode> parseStructInitialization(TokenQueue queue);

    /**
     * @param queue
     * @return
     */
    @CompleteParse
    ParseResult<FunctionDefinitionNode> parseFunctionDefinition(TokenQueue queue);

    /**
     * @param queue
     * @return
     */
    @CompleteParse
    ParseResult<LiteralNode> parseLiteral(TokenQueue queue);

    /**
     * @param queue
     * @return
     */
    @CompleteParse
    ParseResult<PrimitiveLiteralNode> parsePrimitiveLiteral(TokenQueue queue);

    /**
     * Parses
     *
     * @param queue
     * @return
     */
    @CompleteParse
    ParseResult<ExceptionCallNode> parseExceptionCreation(TokenQueue queue);
}
