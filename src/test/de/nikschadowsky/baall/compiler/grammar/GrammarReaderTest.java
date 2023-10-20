package de.nikschadowsky.baall.compiler.grammar;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GrammarReaderTest {


    private GrammarReader reader;

    @Test
    void testCreateGrammar(){
        reader = new GrammarReader("test/GrammarReaderTestFile.txt");

        Grammar g = reader.generateGrammar();

        assertTrue(g.getStart().getIdentifier().equalsIgnoreCase("start"));

        Set<String> testNonterminals = Set.of("START", "A", "B", "END");

        assertTrue(g.getAllNonterminals().stream().map(elem -> testNonterminals.contains(elem.getIdentifier())).reduce(true, (a, b) -> a && b));

        assertEquals(4, g.getAllNonterminals().size());

        String derivationRepresentation = g.getStart().getDerivationList().toString();

        String derivA = "[A B \"T\" \"\\|\"]";
        String derivB = "[]";

        System.out.println(derivationRepresentation);

        assertTrue(derivationRepresentation.contains(derivA) && derivationRepresentation.contains(derivB));

    }

    @Test
    void testCreateGrammarSyntaxError(){
        reader = new GrammarReader("test/GrammarReaderTestSyntaxError.txt");

        Exception e = assertThrows(GrammarSyntaxException.class, () -> reader.generateGrammar());

        String expected = "Invalid syntax!";

        e.printStackTrace();

        assertTrue(e.getMessage().contains(expected));
    }@Test
    void testCreateGrammarEpsilonError(){
        reader = new GrammarReader("test/GrammarReaderTestEpsilonError.txt");
    }

    @Test
    void testCreateGrammarEpsilonError() {

        Exception e = assertThrows(GrammarSyntaxException.class, () -> reader.generateGrammar());

        String expected = "EPSILON cannot be an identifier for a Nonterminal!";

        e.printStackTrace();

        assertTrue(e.getMessage().contains(expected));
    }
}