package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ExceptionCallNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal.*;

/**
 * @since 25.08.2024
 */
public interface LiteralParser {
    @CompleteParse
    ParseResult<ArrayLiteralNode> parseArrayLiteral(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<StructDefinitionLiteralNode> parseStructDefinition(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<StructInitializationLiteralNode> parseStructInitialization(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<FunctionDefinitionNode> parseFunctionDefinition(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<LiteralNode> parseLiteral(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<PrimitiveLiteralNode> parsePrimitiveLiteral(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<ExceptionCallNode> parseExceptionCall(TokenQueue queue, ASTNodeFactory astFactory);
}
