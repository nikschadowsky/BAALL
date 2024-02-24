package de.nikschadowsky.compiler.grammar;

import de.nikschadowsky.compiler.lexer.tokens.Token;
import de.nikschadowsky.compiler.lexer.tokens.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * File created on 02.01.2024
 */
class GrammarProductionTest {

    @Test
    void testEquals() {

        GrammarNonterminal lss = new GrammarNonterminal("A");

        GrammarProduction
                a1 = new GrammarProduction(0, lss, new Token(TokenType.ANY, "A"), lss, new Token(TokenType.ANY, "A"));
        GrammarProduction a2 = new GrammarProduction(1, lss, new Token(TokenType.ANY, "A"), lss, new Token(TokenType.ANY, "A"));

        GrammarProduction b = new GrammarProduction(2, lss, lss, new Token(TokenType.ANY, "A"));
        GrammarProduction c = new GrammarProduction(3, lss, new Token(TokenType.ANY, "A"), lss);


        assertEquals(a1, a2);
        assertNotEquals(a1, b);
        assertNotEquals(a1, c);

    }
}