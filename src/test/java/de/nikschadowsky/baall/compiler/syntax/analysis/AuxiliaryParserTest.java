package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler._utility.TokenQueueTestBuilder;
import de.nikschadowsky.baall.compiler.lexer.tokenizer.TokenType;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.syntax.analysis.result.ParseResult;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.ASTNodeFactory;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.*;

/**
 * @since 13.08.2024
 */
class AuxiliaryParserTest {

    private final ASTNodeFactory astFactory = new ASTNodeFactory(new NodeDiagnosticCollector());

    @Test
    void parseFieldDeclarations() {
        fail("Not yet implemented. Requires #parseFieldDeclaration()");
    }

    @Test
    void parseFieldDeclaration() {
        fail("Not yet implemented. Requires #parseType()");
    }

    @Test
    void parseArgumentList() {
        fail("Not yet implemented. Requires #parseExpression()");
    }

    @Test
    void parseBinaryOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("+")
                                                      .operator(">>")
                                                      .operator(">")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(AuxiliaryParser.parseBinaryOperator(queue, astFactory)).isSuccessful()
                                                                          .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                          .resultMatches(token -> "+".equals(token.value()));
        assertThat(AuxiliaryParser.parseBinaryOperator(queue, astFactory)).isSuccessful()
                                                                          .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                          .resultMatches(token -> ">>".equals(token.value()));
        assertThat(AuxiliaryParser.parseBinaryOperator(queue, astFactory)).isSuccessful()
                                                                          .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                          .resultMatches(token -> ">".equals(token.value()));
        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains("Expected a assignment operator!");
        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains("Expected a assignment operator!");
    }

    @Test
    void parseUnaryOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator("++")
                                                      .operator("--")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(AuxiliaryParser.parseUnaryOperator(queue, astFactory)).isSuccessful()
                                                                         .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                         .resultMatches(token -> ("++").equals(token.value()));
        assertThat(AuxiliaryParser.parseUnaryOperator(queue, astFactory)).isSuccessful()
                                                                         .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                         .resultMatches(token -> "--".equals(token.value()));

        assertThat(AuxiliaryParser.parseUnaryOperator(queue, astFactory)).isUnsuccessful()
                                                                         .syntaxDiagnosticContains("Expected a unary operator!");
        assertThat(AuxiliaryParser.parseUnaryOperator(queue, astFactory)).isUnsuccessful()
                                                                         .syntaxDiagnosticContains("Expected a unary operator!");

    }

    @Test
    void parseShorthandOperator() {
        TokenQueue queue = new TokenQueueTestBuilder().operator(":=")
                                                      .operator("=")
                                                      .operator("+=")
                                                      .operator("|=")
                                                      .operator("++")
                                                      .keyword("keyword")
                                                      .string("anyString")
                                                      .build();

        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isSuccessful()
                                                                             .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                             .resultMatches(token -> ":=".equals(token.value()));
        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isSuccessful()
                                                                             .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                             .resultMatches(token -> "=".equals(token.value()));
        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isSuccessful()
                                                                             .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                             .resultMatches(token -> "+=".equals(token.value()));
        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isSuccessful()
                                                                             .resultMatches(token -> TokenType.OPERATOR.equals(token.type()))
                                                                             .resultMatches(token -> "|=".equals(token.value()));

        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains("Expected a assignment operator!");
        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains("Expected a assignment operator!");
        assertThat(AuxiliaryParser.parseShorthandOperator(queue, astFactory)).isUnsuccessful()
                                                                             .syntaxDiagnosticContains("Expected a assignment operator!");
    }

    @Test
    void parseIdentifier() {
        TokenQueue queue = new TokenQueueTestBuilder().identifier("MyIdentifier").number("1234").build();

        // parsing identifier
        assertThat(AuxiliaryParser.parseIdentifier(queue, astFactory)).isSuccessful()
                                                                      .resultMatches(token -> TokenType.IDENTIFIER.equals(token.type()))
                                                                      .resultMatches(token -> "MyIdentifier".equals(token.value()));
        // parsing number
        assertThat(AuxiliaryParser.parseIdentifier(queue, astFactory)).isUnsuccessful()
                                                                      .syntaxDiagnosticContains("Expected an identifier!");
    }

    @Test
    void parseType() {
        fail("Not yet implemented. Requires #parseArrayIndexInformation()");
    }

    @Test
    void parseSimpleType() {
        TokenQueue queue = new TokenQueueTestBuilder().keyword("number")
                                                      .keyword("string")
                                                      .identifier("MyIdentifier")
                                                      .bool("true")
                                                      .build();

        assertThat(AuxiliaryParser.parseSimpleType(queue, astFactory)).isSuccessful()
                                                                      .resultMatches(token -> TokenType.KEYWORD.equals(token.type()))
                                                                      .resultMatches(token -> "number".equals(token.value()));
        ;
        assertThat(AuxiliaryParser.parseSimpleType(queue, astFactory)).isSuccessful()
                                                                      .resultMatches(token -> TokenType.KEYWORD.equals(token.type()))
                                                                      .resultMatches(token -> "string".equals(token.value()));
        ;
        assertThat(AuxiliaryParser.parseSimpleType(queue, astFactory)).isSuccessful()
                                                                      .resultMatches(token -> TokenType.IDENTIFIER.equals(token.type()))
                                                                      .resultMatches(token -> "MyIdentifier".equals(token.value()));
        ;
        assertThat(AuxiliaryParser.parseSimpleType(queue, astFactory)).isUnsuccessful()
                                                                      .syntaxDiagnosticContains("Expected a type!");
        ;
    }


    @Test
    void parseIdentifierAccess() {
        fail("Not yet implemented. Requires #parseArrayIndexInformation()");
    }

    @Test
    void parseArrayIndexInformation() {
        MockedStatic<ExpressionParser> mockedExpressionParser = mockStatic(ExpressionParser.class);
        ExpressionNode mockedExpression = mock(ExpressionNode.class);
        when(ExpressionParser.parseExpression(any(), any())).thenReturn(ParseResult.successfulParse(mockedExpression));

        ExpressionParser.parseExpression(new TokenQueueTestBuilder().build(), null);
        TokenQueue queue = new TokenQueueTestBuilder().separator("[").separator("]").build();

        assertThat(AuxiliaryParser.parseArrayIndexInformation(queue, astFactory)).isSuccessful();

        fail("Not yet implemented. Requires #parseExpression()");
    }

    @Test
    void parseIdentifierAccesses() {
        fail("Not implemented yet. Requires #parseIdentifierAccess()");
    }

    @Test
    void parseCodeBlock() {
        fail("Not implemented yet. Requires #parseStatement()");
    }
}