package de.nikschadowsky.baall.compiler.grammar.optimization;

import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.generation.GrammarReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * File created on 14.02.2024
 */
class GrammarMinimizerTest {

    @Test
    void minimizeGrammar() {
        Grammar g = GrammarReader.getInstance().generateGrammar("test_resources/GrammarMinimizerTest.grammar");

        GrammarMinimizer minimizer = new GrammarMinimizer();

        Grammar minimized = minimizer.minimizeGrammar(g);

        Grammar expected = GrammarReader.getInstance().generateGrammar("test_resources/GrammarMinimizerExpectedGrammar.txt");

        assertEquals(expected, minimized);
    }
}