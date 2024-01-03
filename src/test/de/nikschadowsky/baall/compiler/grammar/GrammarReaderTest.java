package de.nikschadowsky.baall.compiler.grammar;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GrammarReaderTest {


    private GrammarReader reader;

    @Test
    void testCreateGrammar() {
        Grammar g = new GrammarReader("test_resources/GrammarReaderTestFile.txt").generateGrammar();

        assertTrue(g.getStart().getIdentifier().equalsIgnoreCase("start"));

        Set<String> testNonterminals = Set.of("START", "A", "B", "END");

        assertTrue(g.getAllNonterminals().stream().map(elem -> testNonterminals.contains(elem.getIdentifier())).reduce(true, (a, b) -> a && b));

        assertEquals(4, g.getAllNonterminals().size());

        String derivationRepresentation = g.getStart().getProductionRules().toString();

        String derivA = "A B \"T\" \"|\"";
        String derivB = "ε";

        System.out.println(derivationRepresentation);

        assertTrue(derivationRepresentation.contains(derivA) && derivationRepresentation.contains(derivB));

    }

    @Test
    void testCreateGrammarSyntaxError() {

        Exception e = assertThrows(GrammarSyntaxException.class, () -> new GrammarReader("test_resources/GrammarReaderTestSyntaxError.txt").generateGrammar());

        String expected = "Invalid syntax!";

        e.printStackTrace();

        assertTrue(e.getMessage().contains(expected));
    }

    @Test
    void testCreateGrammarEpsilonError() {

        Exception e = assertThrows(GrammarSyntaxException.class, () -> new GrammarReader("test_resources/GrammarReaderTestEpsilonError.txt").generateGrammar());

        String expected = "Meta-Symbols cannot be identifiers for Nonterminals!";

        e.printStackTrace();

        assertTrue(e.getMessage().contains(expected));
    }
}