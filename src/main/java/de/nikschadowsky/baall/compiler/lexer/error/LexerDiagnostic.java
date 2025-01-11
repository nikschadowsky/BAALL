package de.nikschadowsky.baall.compiler.lexer.error;


import de.nikschadowsky.baall.compiler.lexer.LexerRow;
import de.nikschadowsky.baall.compiler.output.error.Diagnostic;

/**
 * @since 03.01.2025
 */
public class LexerDiagnostic extends Diagnostic {

    private final String message;

    private final LexerRow row;

    public LexerDiagnostic(String message, LexerRow row) {
        this.message = message;
        this.row = row;
    }

    @Override
    public String getFormatted() {
        return "";
    }
}
