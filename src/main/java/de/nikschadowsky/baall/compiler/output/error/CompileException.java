package de.nikschadowsky.baall.compiler.output.error;


/**
 * @since 12.01.2025
 */
public class CompileException extends Exception {

    public CompileException(final Diagnostic diagnostic) {
        super(diagnostic.getFormatted());
    }

    public CompileException(final String message) {
        super(message);
    }

    public CompileException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
