package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler._utility.GrammarUtility;
import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.grammar.GrammarReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.GrammarUtility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * File created on 02.01.2024
 */
class ProductionRuleCandidateSelectorTest {

    static Grammar g;

    @BeforeAll
    static void setup() {
        g = new GrammarReader("test_resources/ProductionRuleCandidateSelectorTestGrammar.txt").generateGrammar();
    }

    @Test
    void testSingleTerminal() {
        GrammarProduction target = getProductionRule(getNonterminal("A"), getTokenWithTypeAny("?"));
        assertEquals(target, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("A"), getTokenQueue("?")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("A"), getTokenQueue("!")).isEmpty());
    }

    @Test
    void testTerminalOnlyChoice() {
        GrammarProduction targetA = getProductionRule(getNonterminal("B"), getTokenWithTypeAny("?"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("B"), getTokenQueue("?")).orElse(null));

        GrammarProduction targetB = getProductionRule(getNonterminal("B"), getTokenWithTypeAny("!"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("B"), getTokenQueue("!")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("B"), getTokenQueue(".")).isEmpty());
    }

    @Test
    void testSingleNonterminalWithDepthOne() {
        GrammarProduction target = getProductionRule(getNonterminal("C"), getNonterminal("X"));

        assertEquals(target, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("C"), getTokenQueue("?"))
                .orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("C"), getTokenQueue("!")).isEmpty());
    }

    @Test
    void testNonterminalOnlyChoiceWithDepthOne() {
        GrammarProduction targetA = getProductionRule(getNonterminal("D"), getNonterminal("X"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("D"), getTokenQueue("?"))
                .orElse(null));

        GrammarProduction targetB = getProductionRule(getNonterminal("D"), getNonterminal("Y"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("D"), getTokenQueue("!"))
                .orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("D"), getTokenQueue(".")).isEmpty());
    }

    @Test
    void testSingleNonterminalWithEpsilon() {
        GrammarProduction target = getProductionRule(getNonterminal("E"), getNonterminal("X"));

        assertEquals(target, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("E"), getTokenQueue("?")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("E"), getTokenQueue("!"))
                .orElseThrow().isEpsilonProduction());
    }

    @Test
    void testTerminalOnlyChainChoice() {
        GrammarProduction targetA = getProductionRule(
                getNonterminal("F"),
                getTokenWithTypeAny("?"), getTokenWithTypeAny("?"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("F"), getTokenQueue("?", "?")).orElse(null));

        GrammarProduction targetB = getProductionRule(
                getNonterminal("F"),
                getTokenWithTypeAny("?"), getTokenWithTypeAny("!"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("F"), getTokenQueue("?", "!")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("F"), getTokenQueue("?", ".")).isEmpty());

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("F"), getTokenQueue(".")).isEmpty());
    }

    @Test
    void testTerminalThenNonterminalChoice() {
        GrammarProduction targetA = getProductionRule(
                getNonterminal("G"),
                getTokenWithTypeAny("?"), getNonterminal("X"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("G"), getTokenQueue("?", "?")).orElse(null));

        GrammarProduction targetB = getProductionRule(
                getNonterminal("G"),
                getTokenWithTypeAny("?"), getNonterminal("Y"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("G"), getTokenQueue("?", "!")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("G"), getTokenQueue("?", ".")).isEmpty());
    }

    @Test
    void testNonterminalThenTerminalChoice() {
        GrammarProduction targetA = getProductionRule(
                getNonterminal("H"),
                getNonterminal("Z"), getTokenWithTypeAny("?"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("H"), getTokenQueue(".", "?")).orElse(null));

        GrammarProduction targetB = getProductionRule(
                getNonterminal("H"),
                getNonterminal("Z"), getTokenWithTypeAny("!"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("H"), getTokenQueue(".", "!")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("H"), getTokenQueue(".", ".")).isEmpty());
    }
    @Test
    void testNonterminalThenNonterminalChoice(){
        GrammarProduction targetA = getProductionRule(
                getNonterminal("I"),
                getNonterminal("Z"), getNonterminal("X"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("I"), getTokenQueue(".", "?")).orElse(null));

        GrammarProduction targetB = getProductionRule(
                getNonterminal("I"),
                getNonterminal("Z"), getNonterminal("Y"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("I"), getTokenQueue(".", "!")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("I"), getTokenQueue(".", ".")).isEmpty());
    }

    @Test
    void testNonterminalThenMixedChoice(){
        GrammarProduction targetA = getProductionRule(
                getNonterminal("J"),
                getNonterminal("Z"), getNonterminal("X"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("J"), getTokenQueue(".", "?")).orElse(null));

        GrammarProduction targetB = getProductionRule(
                getNonterminal("J"),
                getNonterminal("Z"), getTokenWithTypeAny("!"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("J"), getTokenQueue(".", "!")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("J"), getTokenQueue(".", ".")).isEmpty());
    }

    @Test
    void testMixedThenNonterminalChoice(){
        GrammarProduction targetA = getProductionRule(
                getNonterminal("K"),
                getNonterminal("Z"), getNonterminal("X"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("K"), getTokenQueue(".", "?")).orElse(null));

        GrammarProduction targetB = getProductionRule(
                getNonterminal("K"),
                getTokenWithTypeAny("."), getNonterminal("Y"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("K"), getTokenQueue(".", "!")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("K"), getTokenQueue(".", ".")).isEmpty());
    }

    @Test
    void testMixedThenMixedChoice(){
        GrammarProduction targetA = getProductionRule(
                getNonterminal("L"),
                getNonterminal("Z"), getTokenWithTypeAny("?"));
        assertEquals(targetA, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("L"), getTokenQueue(".", "?")).orElse(null));

        GrammarProduction targetB = getProductionRule(
                getNonterminal("L"),
                getTokenWithTypeAny("."), getNonterminal("Y"));
        assertEquals(targetB, ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("L"), getTokenQueue(".", "!")).orElse(null));

        assertTrue(ProductionRuleCandidateSelector
                .determineCandidate(getNonterminal("L"), getTokenQueue(".", ".")).isEmpty());
    }

    private GrammarNonterminal getNonterminal(String identifier) {
        return GrammarUtility.getNonterminal(g, identifier);
    }

}
