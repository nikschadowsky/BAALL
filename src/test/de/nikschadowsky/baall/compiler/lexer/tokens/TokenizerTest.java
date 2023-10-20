package de.nikschadowsky.baall.compiler.lexer.tokens;

import de.nikschadowsky.baall.compiler.lexer.Lexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class TokenizerTest {

    private String fileContent;


    @BeforeEach
    void setUp() {
        final String path = "test/TokenizerTestFile.txt";

        Lexer lexer = new Lexer(path);

        fileContent = lexer.getPreprocessedCode();

    }

    @Test
    void testTokenizer() {

        Tokenizer tokenizer = new Tokenizer(fileContent);
        List<Token> tokens = tokenizer.run();

        assertIterableEquals(Arrays.asList(
                TokenType.BOOLEAN, TokenType.BOOLEAN,
                TokenType.STRING,
                TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
                TokenType.SEPARATOR, TokenType.SEPARATOR, TokenType.OPERATOR,

                TokenType.NUMBER,TokenType.SEPARATOR, TokenType.NUMBER, TokenType.SEPARATOR,
                TokenType.NUMBER, TokenType.NUMBER, TokenType.NUMBER,
                TokenType.IDENTIFIER,
                TokenType.KEYWORD,
                TokenType.KEYWORD

        ), tokens.stream().map(Token::getType).toList());

        System.out.println(tokens);
    }

}