package de.nikschadowsky.baall.compiler.lexer.tokens;

public enum TokenType {

    STRING("String"),
    NUMBER("Number"),
    BOOLEAN("Boolean"),
    KEYWORD("Keyword"),
    IDENTIFIER("Identifier"),
    OPERATOR("Operator"),
    SEPARATOR("Separator"),
    ANY("Any");

    private String description;

    TokenType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
