package de.nikschadowsky.compiler.lexer.tokens;

import de.nikschadowsky.compiler.lexer.Lexer;
import de.nikschadowsky.compiler.util.FileLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class TokenizerTest {

    private String fileContent;


    @BeforeEach
    void setUp() {
        final String path = "TokenizerTestFile.txt";

        Lexer lexer = new Lexer(FileLoader.getPathFromClasspath(path));

        fileContent = lexer.getPreprocessedCode();
    }

    @Test
    void testTokenizer() {

        Tokenizer tokenizer = new Tokenizer(fileContent);
        List<Token> tokens = tokenizer.run();


        List<TokenType> types = Arrays.asList(
                TokenType.BOOLEAN, TokenType.BOOLEAN,
                TokenType.STRING,
                TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
                TokenType.SEPARATOR, TokenType.SEPARATOR, TokenType.OPERATOR,

                TokenType.NUMBER, TokenType.SEPARATOR, TokenType.NUMBER, TokenType.SEPARATOR,
                TokenType.NUMBER, TokenType.NUMBER, TokenType.NUMBER,
                TokenType.IDENTIFIER,
                TokenType.KEYWORD,
                TokenType.KEYWORD,
                TokenType.IDENTIFIER);


        assertEquals(types.size(), tokens.size());

        assertIterableEquals(types, tokens.stream().map(Token::type).toList());

        System.out.println(tokens);
    }

}