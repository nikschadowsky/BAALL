package de.nikschadowsky.baall.compiler.tokenizer;

import de.nikschadowsky.baall.compiler._utility.TestCompileInformation;
import de.nikschadowsky.baall.compiler.lexer.LexerRow;
import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;

/**
 * @since 12.01.2025
 */
class BetterTokenizerStepTest {

    private List<LexerRow> input;

    private BetterTokenizerStep step;

    @BeforeEach
    void setUp() {
        input = List.of(
                new LexerRow("1234 _identifier true struct;", 1),
                new LexerRow("struct(123,\"String\",true).test[];", 4)
        );

        step = new BetterTokenizerStep(null);
    }

    @Test
    void executeStep() throws CompileException {
        TokenQueue queue = step.executeStep(input, new TestCompileInformation());

        assertThat(queue).hasNextToken("1234", TokenType.NUMBER)
                         .hasNextToken("_identifier", TokenType.IDENTIFIER)
                         .hasNextToken("true", TokenType.BOOLEAN)
                         .hasNextToken("struct", TokenType.KEYWORD)
                         .hasNextToken(";", TokenType.SEPARATOR)
                         .hasNextToken("struct", TokenType.KEYWORD)
                         .hasNextToken("(", TokenType.SEPARATOR)
                         .hasNextToken("123", TokenType.NUMBER)
                         .hasNextToken(",", TokenType.SEPARATOR)
                         .hasNextToken("\"String\"", TokenType.STRING)
                         .hasNextToken(",", TokenType.SEPARATOR)
                         .hasNextToken("true", TokenType.BOOLEAN)
                         .hasNextToken(")", TokenType.SEPARATOR)
                         .hasNextToken(".", TokenType.SEPARATOR)
                         .hasNextToken("test", TokenType.IDENTIFIER)
                         .hasNextToken("[", TokenType.SEPARATOR)
                         .hasNextToken("]", TokenType.SEPARATOR)
                         .hasNextToken(";", TokenType.SEPARATOR)
                         .isAtEnd();

    }
}