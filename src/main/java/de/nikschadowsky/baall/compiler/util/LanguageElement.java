package de.nikschadowsky.baall.compiler.util;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;

/**
 * @since 14.08.2024
 */
public record LanguageElement(String representation, TokenType type, String matchingValue) {

    @Override
    public String toString() {
        return "LanguageElement{representation='%s', type=%s, matchingValue='%s'}".formatted(
                representation,
                type,
                matchingValue
        );
    }

    public boolean matches(Token token) {
        if (token == null) {
            return false;
        }
        if (type != token.type()) {
            return false;
        }
        if (type.hasExactTokenMatching()) {
            return matchingValue.equals(token.value());
        }
        return true;
    }
}
