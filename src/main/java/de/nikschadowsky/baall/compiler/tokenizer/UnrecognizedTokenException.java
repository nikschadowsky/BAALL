package de.nikschadowsky.baall.compiler.tokenizer;

public class UnrecognizedTokenException extends RuntimeException {

    public UnrecognizedTokenException(int index) {
        super("A Token at Index " + index + " was not recognized as a valid Token!");
    }
}
