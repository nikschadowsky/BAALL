package de.nikschadowsky.baall.compiler.syntax.error;

import de.nikschadowsky.baall.compiler.output.error.Diagnostic;
import de.nikschadowsky.baall.compiler.symbol.Token;

/**
 * @since 07.04.2024
 */
public class SyntaxDiagnostic extends Diagnostic {

    private final String message;
    private final Token token;


    public SyntaxDiagnostic(Token token, String message) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String getFormatted() {
        return "%s: %s".formatted(message, token);
    }
}
