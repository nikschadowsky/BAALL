package de.nikschadowsky.baall.compiler.syntax.analysis;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.PartialParseResult;
import de.nikschadowsky.baall.compiler.syntax.util.CompleteParse;
import de.nikschadowsky.baall.compiler.syntax.util.PartialParse;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
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
    @CompleteParse
    ParseResult<List<Map.Entry<TypeNode, Token>>> parseFieldDeclarations(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<Map.Entry<TypeNode, Token>> parseFieldDeclaration(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<List<ExpressionNode>> parseArgumentList(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<Token> parseBinaryOperator(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<Token> parseUnaryOperator(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<Token> parseShorthandOperator(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<Token> parseIdentifier(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<TypeNode> parseType(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<Token> parseSimpleType(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<List<ExpressionNode>> parseArrayTypeDefinition(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<List<ExpressionNode>> parseArrayIndexInformation(TokenQueue queue, ASTNodeFactory astFactory);

    @CompleteParse
    ParseResult<IdentifierAccessNode> parseIdentifierAccess(TokenQueue queue, ASTNodeFactory
            astFactory);

    @PartialParse
    PartialParseResult<List<IdentifierAccessNode>> parseAdditionalIdentifierAccesses(TokenQueue queue, ASTNodeFactory astFactory);

    @PartialParse
    PartialParseResult<StatementsNode> parseCodeBlock(TokenQueue queue, ASTNodeFactory astFactory);
}
