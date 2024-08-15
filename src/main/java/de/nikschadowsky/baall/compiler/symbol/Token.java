package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.lexer.tokenizer.TokenType;

import java.util.Objects;

public record Token(TokenType type, String value, int line, int index) {

    @Override
    public String toString() {
        return "Token{type=%s, value='%s', at:%s:%s}".formatted(type, value, line, index);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token token)
            return type().equals(token.type()) && value().equals(token.value());
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type(), value());
    }

}
