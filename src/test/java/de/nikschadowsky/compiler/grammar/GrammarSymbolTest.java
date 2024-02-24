package de.nikschadowsky.compiler.grammar;

import de.nikschadowsky.compiler.lexer.tokens.Token;
import de.nikschadowsky.compiler.lexer.tokens.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrammarSymbolTest {

    private Token token1, token2, token3, token4, token5, token6;

    private GrammarNonterminal A, B, C;

    @BeforeEach
    void setUp() {
        token1 = new Token(TokenType.STRING, "value");
        token2 = new Token(TokenType.KEYWORD, "same type a");
        token3 = new Token(TokenType.KEYWORD, "same type b");
        token4 = new Token(TokenType.STRING, "same value");
        token5 = new Token(TokenType.STRING, "same value");
        token6 = new Token(TokenType.ANY, "same value");


        A = new GrammarNonterminal("identifier");
        B = new GrammarNonterminal("identifier");
        C = new GrammarNonterminal("different identifier");

    }

    @Test
    void testSymbolMatchesTokens() {
        // always check for reflexivity
        assertTrue(token4.symbolMatches(token5) && token5.symbolMatches(token4));
        assertTrue(token5.symbolMatches(token6) && token6.symbolMatches(token5));

        // also both directions on same type but different value
        assertFalse(token2.symbolMatches(token3) || token3.symbolMatches(token2));

        // for good measure
        assertFalse(token1.symbolMatches(token2));
        assertFalse(token1.symbolMatches(token3));
        assertTrue(token1.symbolMatches(token4)); // Strings match regardless of content
        assertTrue(token1.symbolMatches(token5));
        assertFalse(token1.symbolMatches(token6));

        // and just checking that one symbol matches itself
        assertTrue(token1.symbolMatches(token1));
    }

    @Test
    void testSymbolMatchesNonterminal() {
        // checking for reflexivity just in case String.equals isn't reflexive
        assertTrue(A.symbolMatches(B) && B.symbolMatches(A));

        assertFalse(B.symbolMatches(C) || C.symbolMatches(B));
    }

    @Test
    void testSymbolMatchesMixed() {
        assertFalse(A.symbolMatches(token1));
        assertFalse(token2.symbolMatches(B));

    }
}
