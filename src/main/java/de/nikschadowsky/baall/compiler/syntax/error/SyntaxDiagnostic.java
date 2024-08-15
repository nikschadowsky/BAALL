package de.nikschadowsky.baall.compiler.syntax.error;

import de.nikschadowsky.baall.compiler.symbol.Token;

/**
 * File created on 07.04.2024
 */
public class SyntaxDiagnostic {

    private final int index;
    private final String message;
    private final Token token;

    public SyntaxDiagnostic(String message) {
        this(null, -1, message);
    }

    public SyntaxDiagnostic(int index, String message) {
        this(null, index, message);
    }

    public SyntaxDiagnostic(Token token, String message) {
        this(token, -1, message);
    }

    /**
     * Constructor to initialize all fields.
     */
    private SyntaxDiagnostic(Token token, int index, String message) {
        this.index = index;
        this.message = message;
        this.token = token;
    }

    /**
     * @return index, or {@code -1} if no index was set.
     */
    public int getIndex() {
        return index;
    }

    public String getMessage() {
        return message;
    }

    public Token getToken() {
        return token;
    }
}
