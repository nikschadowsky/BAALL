package de.nikschadowsky.baall.compiler.lexer.test;

import de.nikschadowsky.baall.compiler.util.FileLoader;
import de.nikschadowsky.baall.compiler.lexer.Lexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {

    private Lexer lexer;

    private static final String TEST_PATH = "test/LexerTestFile.txt";

    @BeforeEach
    void setUp() {

        lexer = new Lexer(TEST_PATH);
    }

    @Test
    void testLexerSteps() {


        assertEquals(FileLoader.loadFileContent(TEST_PATH), lexer.readContent().getContent());

        assertEquals(FileLoader.loadFileContent("test/RemoveComments.txt"), lexer.removeComments().getContent());

        assertEquals(FileLoader.loadFileContent("test/RemoveWhitespaces.txt"), lexer.replaceAllWhitespace().getContent());
    }

}
