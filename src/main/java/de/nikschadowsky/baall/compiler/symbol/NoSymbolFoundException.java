package de.nikschadowsky.baall.compiler.symbol;


import de.nikschadowsky.baall.compiler.output.error.CompileException;

/**
 * @since 26.03.2025
 */
public class NoSymbolFoundException extends CompileException {

    public NoSymbolFoundException(String message) {
        super(message);
    }
}
