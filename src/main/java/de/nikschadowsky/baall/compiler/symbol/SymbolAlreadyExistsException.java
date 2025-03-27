package de.nikschadowsky.baall.compiler.symbol;


import de.nikschadowsky.baall.compiler.output.error.CompileException;

/**
 * @since 26.03.2025
 */
public class SymbolAlreadyExistsException extends CompileException {

    public SymbolAlreadyExistsException(String message) {
        super(message);
    }

}
