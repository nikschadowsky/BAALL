package de.nikschadowsky.baall.compiler.symbol;


import de.nikschadowsky.baall.compiler.output.error.CompileException;

/**
 * @since 30.03.2025
 */
public class TypeAlreadyExistsException extends CompileException {
    public TypeAlreadyExistsException(String message) {
        super(message);
    }
}
