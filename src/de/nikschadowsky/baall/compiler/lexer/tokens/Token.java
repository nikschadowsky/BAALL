package de.nikschadowsky.baall.compiler.lexer.tokens;

public class Token {

    private final TokenType type;

    private final String value;


    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;

    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }
}
