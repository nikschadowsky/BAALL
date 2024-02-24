package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler._utility.ClassUtility;
import de.nikschadowsky.baall.compiler._utility.GrammarUtility;
import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.grammar.generation.GrammarReader;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.util.FileLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Queue;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File created on 13.01.2024
 */
class ProductionRuleCandidateSelectorPRIntermediateTest {

    static Grammar testGrammar;

    @BeforeAll
    static void init() {
        testGrammar = GrammarReader.getInstance()
                                   .generateGrammar(
                                           FileLoader.getPathFromClasspath("ProductionRuleCandidateSelectorStepTestGrammar.grammar"));
    }

    @Test
    void testStepTerminal() {
        GrammarNonterminal symbol = GrammarUtility.getNonterminal(testGrammar, "A");

        ProductionRuleCandidateSelector.PRIntermediate intermediate =
                createTestIntermediate(GrammarUtility.getProductionRule(symbol, GrammarUtility.getTokenWithTypeAny("a")), GrammarUtility.getTokenQueue("a"));

        assertTrue(intermediate.step());

        intermediate = createTestIntermediate(GrammarUtility.getProductionRule(symbol, GrammarUtility.getTokenWithTypeAny("a")), GrammarUtility.getTokenQueue("b"));

        assertFalse(intermediate.step());
    }

    @Test
    void testStepNonterminalOnly() throws NoSuchFieldException, IllegalAccessException {

        GrammarNonterminal symbol = GrammarUtility.getNonterminal(testGrammar, "B");

        GrammarNonterminal symbolForMock = GrammarUtility.getNonterminal(testGrammar, "A");

        try (MockedStatic<ProductionRuleCandidateSelector> mockedStatic = Mockito.mockStatic(
                ProductionRuleCandidateSelector.class)) {
            mockedStatic.when(() -> ProductionRuleCandidateSelector
                                .determineCandidate(Mockito.eq(symbolForMock), Mockito.any()))
                        .thenReturn(Optional.of(GrammarUtility.getProductionRule(symbolForMock, GrammarUtility.getTokenWithTypeAny("a"))));


            ProductionRuleCandidateSelector.PRIntermediate intermediate =
                    createTestIntermediate(
                            GrammarUtility.getProductionRule(symbol, GrammarUtility.getNonterminal(testGrammar, "A"),
                                                             GrammarUtility.getNonterminal(testGrammar, "A")
                            ), GrammarUtility.getTokenQueue("a", "b"));

            // stack contains 'A A', queue contains 'a b'
            assertTrue(intermediate.step());
            ParserStack watchedParserStack = ClassUtility.getFieldValue(ParserStack.class, intermediate, "stack");

            assertEquals(1, watchedParserStack.size());

            // stack contains 'A', queue contains 'b'
            assertFalse(intermediate.step());
            assertTrue(watchedParserStack.isEmpty());
        }
    }

    ProductionRuleCandidateSelector.PRIntermediate createTestIntermediate(GrammarProduction rule, Queue<Token> tokenQueue) {

        try {
            return ClassUtility.getInstance(
                    ProductionRuleCandidateSelector.PRIntermediate.class,
                    new Class[]{GrammarProduction.class, Queue.class},
                    rule,
                    tokenQueue
            );
        } catch (Exception e) {
            Logger.getAnonymousLogger().severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
