package de.nikschadowsky.compiler.grammar;

import de.nikschadowsky.compiler._utility.GrammarUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File created on 25.01.2024
 */
class GrammarNonterminalTest {

    GrammarNonterminal nonterminal;

    @BeforeEach
    void setup() {
        nonterminal = new GrammarNonterminal("START");
    }

    @Test
    void testAddAnnotations() {
        Set<GrammarNonterminalAnnotation> annotationSet1 = Set.of(
                new GrammarNonterminalAnnotation("Annotation1"),
                new GrammarNonterminalAnnotation("Annotation2")
        );
        Set<GrammarNonterminalAnnotation> annotationSet2 = Set.of(
                new GrammarNonterminalAnnotation("Annotation3"),
                new GrammarNonterminalAnnotation("Annotation4")
        );

        assertTrue(nonterminal.addAnnotations(annotationSet1));
        assertFalse(nonterminal.addAnnotations(annotationSet2));

        assertEquals(annotationSet1, nonterminal.getAnnotations());
    }


    @Test
    void testHasAnnotations() {
        Set<GrammarNonterminalAnnotation> annotationSet = Set.of(
                new GrammarNonterminalAnnotation("Annotation1"),
                new GrammarNonterminalAnnotation("Annotation2")
        );
        nonterminal.addAnnotations(annotationSet);

        assertTrue(nonterminal.hasAnnotation("Annotation1"));
        assertTrue(nonterminal.hasAnnotation("Annotation2"));
        assertFalse(nonterminal.hasAnnotation("Annotation3"));
    }

    @Test
    void testAddProductionRules() {
        Set<GrammarProduction> ruleSet1 = Set.of(new GrammarProduction(
                0,
                nonterminal,
                GrammarUtility.getTokenWithTypeAny("a"),
                GrammarUtility.getTokenWithTypeAny("b")
        ));
        Set<GrammarProduction> ruleSet2 = Set.of(new GrammarProduction(
                0,
                nonterminal,
                GrammarUtility.getTokenWithTypeAny("c"),
                GrammarUtility.getTokenWithTypeAny("d")
        ));

        assertTrue(nonterminal.addProductionRules(ruleSet1));
        assertFalse(nonterminal.addProductionRules(ruleSet2));

        assertEquals(ruleSet1, nonterminal.getProductionRules());
    }
}
