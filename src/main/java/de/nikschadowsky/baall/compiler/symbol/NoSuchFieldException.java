package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.output.error.CompileException;

public class NoSuchFieldException extends RuntimeException {

    public NoSuchFieldException(String message) {
        super(message);
    }

}
