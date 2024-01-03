package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.*;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.lexer.tokens.TokenType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * File created on 02.01.2024
 */
class ProductionRuleCandidateSelectorTest {

    static Grammar g;

    @BeforeAll
    static void setup() {
        g = new GrammarReader("test_resources/ProductionRuleCandidateSelectorTestGrammar.txt").generateGrammar();
    }

    /**
     * Tests the production rule A -> "a" | "b". Depth: 1
     */
    @Test
    void testOneStepProductionTerminalOnly() {
        GrammarNonterminal a = getNonterminal("A");

        GrammarProduction target = getProductionRule(a, getToken("a"));

        assertEquals(target, ProductionRuleCandidateSelector
                .determineCandidate(a, getQueue(getToken("a")))
                .orElse(null));

    }

    /**
     * Tests the production rule B -> "c" C | "d" D. Depth: 1
     */
    @Test
    void testOneStepProductionMixedSymbols() {
        GrammarNonterminal b = getNonterminal("B");

        GrammarProduction target = getProductionRule(b, getToken("c"), getNonterminal("C"));

        assertEquals(target, ProductionRuleCandidateSelector
                .determineCandidate(b, getQueue(getToken("c"), getToken("e")))
                .orElse(null));
        assertEquals(target, ProductionRuleCandidateSelector
                .determineCandidate(b, getQueue(getToken("c"), getToken("f")))
                .orElse(null));
        assertEquals(target, ProductionRuleCandidateSelector
                .determineCandidate(b, getQueue(getToken("c")))
                .orElse(null));
    }

    @Test
    void testProductionWithEpsilon() {
        GrammarNonterminal f = getNonterminal("F");

        GrammarProduction target = getProductionRule(f, getNonterminal("G"), getToken("y"));

        assertEquals(target, ProductionRuleCandidateSelector.determineCandidate(f, getQueue(getToken("y"), getToken("z"))).orElse(null));
        assertNotEquals(target, ProductionRuleCandidateSelector.determineCandidate(f, getQueue(getToken("u"), getToken("z"))).orElse(null));
    }

    @Test
    void testProductionMultipleSteps() {
        GrammarNonterminal x = getNonterminal("X");

        GrammarProduction target = getProductionRule(
                x,
                getNonterminal("Y"),
                getToken("?"),
                getNonterminal("Z"),
                getToken("!")
        );

        assertEquals(target, ProductionRuleCandidateSelector
                .determineCandidate(x, getQueue(getToken("z"), getToken("?"), getToken("z"), getToken("!")))
                .orElse(null));
    }


    private GrammarNonterminal getNonterminal(String identifier) {
        return g.getAllNonterminals().stream().filter(
                        grammarNonterminal -> grammarNonterminal.symbolMatches(
                                new GrammarNonterminal(identifier)))
                .findAny().orElse(null);
    }

    private GrammarProduction getProductionRule(@NotNull GrammarNonterminal lss, GrammarSymbol... prod) {
        return lss.getProductionRules().stream().filter(gp -> gp.equals(new GrammarProduction(-1, lss, prod))).findAny().orElse(null);
    }

    private Token getToken(String value) {
        return new Token(TokenType.ANY, value);
    }

    private Queue<Token> getQueue(Token... tokens) {
        return new LinkedList<>(List.of(tokens));
    }
}
