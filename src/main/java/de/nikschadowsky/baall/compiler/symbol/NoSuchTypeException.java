package de.nikschadowsky.baall.compiler.symbol;

public class NoSuchTypeException extends RuntimeException {
    public NoSuchTypeException(String message) {
        super(message);
    }
}
