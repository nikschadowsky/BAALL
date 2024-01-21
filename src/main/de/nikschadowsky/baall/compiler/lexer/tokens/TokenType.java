package de.nikschadowsky.baall.compiler.lexer.tokens;

import java.util.Arrays;

public enum TokenType {

    STRING("String"),
    NUMBER("Number"),
    BOOLEAN("Boolean"),
    KEYWORD("Keyword"),
    IDENTIFIER("Identifier"),
    OPERATOR("Operator"),
    SEPARATOR("Separator"),
    ANY("Any");

    private final String description;

    TokenType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Finds a TokenType to the provided description String. It matches case-insensitive.
     *
     * @param description description of this type
     * @return the type associated to this description
     * @throws NoTokenTypeFoundException if there is no type having this description
     */
    public static TokenType getTokenTypeForDescription(String description) throws NoTokenTypeFoundException {
        return Arrays.stream(values())
                .filter(entry -> entry.getDescription().equalsIgnoreCase(description))
                .findAny()
                .orElseThrow(() -> new NoTokenTypeFoundException("No Token found matching '" + description + "'!"));


    }
}
