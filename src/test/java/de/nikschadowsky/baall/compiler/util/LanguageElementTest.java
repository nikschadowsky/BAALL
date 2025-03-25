package de.nikschadowsky.baall.compiler.util;

import de.nikschadowsky.baall.compiler.symbol.LineInformation;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;

/**
 * @since 14.08.2024
 */
class LanguageElementTest {

    private static final LineInformation LINE_INFORMATION = new LineInformation(0, 0);

    @Test
    void matches() {
        LanguageElement elementWithExactMatching = new LanguageElement("representation", TokenType.SEPARATOR, "value");

        assertThat(elementWithExactMatching).matches(new Token(TokenType.SEPARATOR, "value", LINE_INFORMATION))
                                            .doesNotMatch(new Token(TokenType.SEPARATOR, "not value", LINE_INFORMATION))
                                            .doesNotMatch(new Token(TokenType.KEYWORD, "value", LINE_INFORMATION));

        LanguageElement elementWithoutExactMatching = new LanguageElement("representation", TokenType.STRING, "value");

        assertThat(elementWithoutExactMatching).matches(new Token(TokenType.STRING, "any", LINE_INFORMATION))
                                               .matches(new Token(TokenType.STRING, "value", LINE_INFORMATION))
                                               .doesNotMatch(new Token(TokenType.KEYWORD, "value", LINE_INFORMATION));
    }
}