package de.nikschadowsky.baall.compiler.lexer.tokens;

/**
 * File created on 21.10.2023
 */
public class NoTokenTypeFoundException extends RuntimeException {

    public NoTokenTypeFoundException(String msg) {
        super(msg);
    }

}
