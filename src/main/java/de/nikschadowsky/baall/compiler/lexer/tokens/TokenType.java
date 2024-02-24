package de.nikschadowsky.baall.compiler.lexer.tokens;

import de.nikschadowsky.baall.compiler.util.LambdaUtility;

import java.util.Arrays;

public enum TokenType {

    STRING("String", false),
    NUMBER("Number", false),
    BOOLEAN("Boolean", false),
    KEYWORD("Keyword", true),
    IDENTIFIER("Identifier", false),
    OPERATOR("Operator", true),
    SEPARATOR("Separator", true),
    ANY("Any", true);

    private final String description;

    private final boolean hasExactTokenMatching;

    TokenType(String description, boolean hasExactTokenMatching) {
        this.description = description;
        this.hasExactTokenMatching = hasExactTokenMatching;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasExactTokenMatching() {
        return hasExactTokenMatching;
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
                     .orElseThrow(LambdaUtility.createSupplier(new NoTokenTypeFoundException(
                             "No Token found matching description '" + description + "'!")));
    }
}
