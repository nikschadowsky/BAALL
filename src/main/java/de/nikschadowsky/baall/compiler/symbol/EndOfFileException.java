package de.nikschadowsky.baall.compiler.symbol;

/**
 * @since 14.04.2024
 */
public class EndOfFileException extends RuntimeException {
    public EndOfFileException() {
        super("A symbol was requested but the end of file reached!");
    }
}