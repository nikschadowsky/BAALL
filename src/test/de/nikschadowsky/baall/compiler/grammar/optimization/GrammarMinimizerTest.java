package de.nikschadowsky.baall.compiler.grammar.optimization;

import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.generation.GrammarReader;
import de.nikschadowsky.baall.compiler.util.ArrayUtility;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * File created on 14.02.2024
 */
class GrammarMinimizerTest {

    @ParameterizedTest
    @MethodSource("parameterSupplier")
    void minimizeGrammar(int value) {
        Grammar g =
                GrammarReader.getInstance()
                             .generateGrammar("test_resources/minimizer/GrammarMinimizerTest" + value + ".grammar");

        GrammarMinimizer minimizer = new GrammarMinimizer();

        Grammar minimized = minimizer.minimizeGrammar(g);

        Grammar expected =
                GrammarReader.getInstance()
                             .generateGrammar("test_resources/minimizer/GrammarMinimizerExpectedGrammar" + value + ".grammar");

        assertEquals(expected, minimized);
    }

    static int[] parameterSupplier() {
        return ArrayUtility.rangeClosed(1, 5);
    }
}