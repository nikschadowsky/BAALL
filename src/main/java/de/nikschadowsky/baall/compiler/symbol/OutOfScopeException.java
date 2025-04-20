package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.output.error.CompileException;

public class OutOfScopeException extends CompileException {
    public OutOfScopeException(String message) {
        super(message);
    }
}
