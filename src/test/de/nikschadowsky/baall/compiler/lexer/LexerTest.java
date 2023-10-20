package de.nikschadowsky.baall.compiler.lexer;

import de.nikschadowsky.baall.compiler.util.FileLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {

    private Lexer lexer;

    private static final String TEST_PATH = "test_resources/LexerTestFile.txt";

    @BeforeEach
    void setUp() {

        lexer = new Lexer(TEST_PATH);
    }

    @Test
    void testPreprocessing() {
        assertEquals(FileLoader.loadFileContent("test_resources/LexerTestExpected.txt"), lexer.getPreprocessedCode());
    }

}
