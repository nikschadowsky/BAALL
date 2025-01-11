package de.nikschadowsky.baall.compiler.tokenizer;

import de.nikschadowsky.baall.compiler.symbol.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

        /*LexerStep lexerStep = new LexerStep(FileLoader.getPathFromClasspath(path));

        fileContent = lexerStep.getPreprocessedCode();*/
    }

    @Disabled
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