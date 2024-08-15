package de.nikschadowsky.baall.compiler.util;

import de.nikschadowsky.baall.compiler.lexer.tokenizer.TokenType;
import de.nikschadowsky.baall.compiler.symbol.Token;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;

/**
 * @since 14.08.2024
 */
class LanguageElementTest {

    @Test
    void matches() {
        LanguageElement elementWithExactMatching = new LanguageElement("representation", TokenType.SEPARATOR, "value");

        assertThat(elementWithExactMatching).matches(new Token(TokenType.SEPARATOR, "value", 0, 0))
                                            .doesNotMatch(new Token(TokenType.SEPARATOR, "not value", 0, 0))
                                            .doesNotMatch(new Token(TokenType.KEYWORD, "value", 0, 0));

        LanguageElement elementWithoutExactMatching = new LanguageElement("representation", TokenType.STRING, "value");

        assertThat(elementWithoutExactMatching).matches(new Token(TokenType.STRING, "any", 0, 0))
                                               .matches(new Token(TokenType.STRING, "value", 0, 0))
                                               .doesNotMatch(new Token(TokenType.KEYWORD, "value", 0, 0));
    }
}