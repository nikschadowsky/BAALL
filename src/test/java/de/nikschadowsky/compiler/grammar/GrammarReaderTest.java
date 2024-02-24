package de.nikschadowsky.compiler.grammar;

import de.nikschadowsky.compiler._utility.GrammarUtility;
import de.nikschadowsky.compiler.grammar.generation.GrammarReader;
import de.nikschadowsky.compiler.util.FileLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class GrammarReaderTest {

    private final Logger logger = Logger.getAnonymousLogger();

    @Test
    void testCreateGrammar() {
        Grammar g = GrammarReader.getInstance().generateGrammar(FileLoader.getPathFromClasspath("GrammarReaderTestFile.grammar"));

        assertTrue(g.getStart().getIdentifier().equalsIgnoreCase("start"));

        Set<String> testNonterminals = Set.of("START", "A", "B", "END");

        assertTrue(g.getAllNonterminals()
                    .stream()
                    .map(elem -> testNonterminals.contains(elem.getIdentifier()))
                    .reduce(true, (a, b) -> a && b));

        assertEquals(4, g.getAllNonterminals().size());

        Assertions.assertTrue(GrammarUtility.getNonterminal(g, "START")
                                            .getAnnotations()
                                            .contains(new GrammarNonterminalAnnotation("StartAnnotation")));
        assertTrue(GrammarUtility.getNonterminal(g, "END")
                                 .getAnnotations()
                                 .contains(new GrammarNonterminalAnnotation("EndAnnotation")));
        assertTrue(GrammarUtility.getNonterminal(g, "END")
                                 .getAnnotations()
                                 .contains(new GrammarNonterminalAnnotation("AdditionalAnnotation")));
        assertEquals(2, GrammarUtility.getNonterminal(g, "END").getAnnotations().size());

        String derivationRepresentation = g.getStart().getProductionRules().toString();

        String productionA = "A B \"T\" \"|\"";
        String productionB = "ε";

        logger.info(derivationRepresentation);

        assertTrue(derivationRepresentation.contains(productionA) && derivationRepresentation.contains(productionB));
    }

    @Test
    void testCreateGrammarWithStartNotInFirstLine() {
        Grammar g =
                GrammarReader.getInstance().generateGrammar(FileLoader.getPathFromClasspath("GrammarReaderStartInLastLineTestFile.grammar"));

        assertEquals(GrammarUtility.getNonterminal(g, "B"), g.getStart());
        assertTrue(GrammarUtility.getNonterminal(g, "START").getAnnotations().isEmpty());
    }

    @Test
    void testCreateGrammarWithMultipleAnnotations() {
        Grammar g =
                GrammarReader.getInstance()
                             .generateGrammar(FileLoader.getPathFromClasspath(
                                     "GrammarReaderNonterminalWithMultipleAnnotationsTestFile.grammar"));

        assertEquals(GrammarUtility.getNonterminal(g, "A"), g.getStart());
        assertEquals(4, g.getStart().getAnnotations().size());
    }

    @Test
    void testCreateGrammarSyntaxError() {
        Exception e = assertThrows(
                GrammarSyntaxException.class,
                () -> GrammarReader.getInstance().generateGrammar(FileLoader.getPathFromClasspath("GrammarReaderTestSyntaxError.grammar"))
        );

        String expected = "Missing symbols";

        logger.warning(e.getMessage());

        assertTrue(e.getMessage().contains(expected));
    }

    @Test
    void testCreateGrammarEpsilonError() {
        Exception e = assertThrows(
                GrammarSyntaxException.class,
                () -> GrammarReader.getInstance().generateGrammar(FileLoader.getPathFromClasspath("GrammarReaderTestEpsilonError.grammar"))
        );

        String expected = "Meta symbols cannot be used as identifiers for nonterminals!";

        logger.warning(e.getMessage());

        assertTrue(e.getMessage().contains(expected));
    }
}