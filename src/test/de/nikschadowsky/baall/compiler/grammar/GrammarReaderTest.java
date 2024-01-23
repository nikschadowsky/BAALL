package de.nikschadowsky.baall.compiler.grammar;

import de.nikschadowsky.baall.compiler._utility.GrammarUtility;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class GrammarReaderTest {


    private GrammarReader reader;

    private final Logger logger = Logger.getAnonymousLogger();

    @Test
    void testCreateGrammar() {
        Grammar g = new GrammarReader("test_resources/GrammarReaderTestFile.txt").generateGrammar();

        assertTrue(g.getStart().getIdentifier().equalsIgnoreCase("start"));

        Set<String> testNonterminals = Set.of("START", "A", "B", "END");

        assertTrue(g.getAllNonterminals().stream().map(elem -> testNonterminals.contains(elem.getIdentifier())).reduce(true, (a, b) -> a && b));

        assertEquals(4, g.getAllNonterminals().size());

        assertTrue(GrammarUtility.getNonterminal(g, "START").getAnnotations().contains(new GrammarNonterminalAnnotation("StartAnnotation")));
        assertTrue(GrammarUtility.getNonterminal(g, "END").getAnnotations().contains(new GrammarNonterminalAnnotation("EndAnnotation")));
        assertTrue(GrammarUtility.getNonterminal(g, "END").getAnnotations().contains(new GrammarNonterminalAnnotation("AdditionalAnnotation")));
        assertEquals(2, GrammarUtility.getNonterminal(g, "END").getAnnotations().size());

        String derivationRepresentation = g.getStart().getProductionRules().toString();

        String productionA = "A B \"T\" \"|\"";
        String productionB = "ε";

        logger.info(derivationRepresentation);

        assertTrue(derivationRepresentation.contains(productionA) && derivationRepresentation.contains(productionB));
    }

    @Test
    void testCreateGrammarSyntaxError() {
        Exception e = assertThrows(GrammarSyntaxException.class, () -> new GrammarReader("test_resources/GrammarReaderTestSyntaxError.txt").generateGrammar());

        String expected = "Missing symbols";

        logger.warning(e.getMessage());

        assertTrue(e.getMessage().contains(expected));
    }

    @Test
    void testCreateGrammarEpsilonError() {
        Exception e = assertThrows(GrammarSyntaxException.class, () -> new GrammarReader("test_resources/GrammarReaderTestEpsilonError.txt").generateGrammar());

        String expected = "Meta symbols cannot be used as identifiers for nonterminals!";

        logger.warning(e.getMessage());

        assertTrue(e.getMessage().contains(expected));
    }
}