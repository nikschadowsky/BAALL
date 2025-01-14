package de.nikschadowsky.baall.compiler.tokenizer;

import de.nikschadowsky.baall.compiler._utility.TestCompileInformation;
import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.symbol.Token;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTest {

    private static final String TEST_INPUT = """
            true false
            "String"
            := / % @ :: +=
            0xFFFF,0b10101,
            1234.1234 .123 123
            identifier
            
            
            
            struct
            string
            stringIdentifier""";

    @Test
    void testTokenizer() throws CompileException {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.executeStep(TEST_INPUT, new TestCompileInformation());

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
                TokenType.IDENTIFIER
        );

        assertThat(tokens).map(Token::type).isEqualTo(types);
    }

}