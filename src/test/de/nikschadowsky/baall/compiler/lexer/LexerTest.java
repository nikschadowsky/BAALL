package de.nikschadowsky.baall.compiler.lexer;

import de.nikschadowsky.baall.compiler.util.FileLoader;
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
    void testPreprocessing() {
        assertEquals(FileLoader.loadFileContent("test/LexerTestExpected.txt"), lexer.getPreprocessedCode());
    }

}
