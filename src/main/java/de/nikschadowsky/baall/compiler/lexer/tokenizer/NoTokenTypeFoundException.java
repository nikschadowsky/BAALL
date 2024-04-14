package de.nikschadowsky.baall.compiler.lexer.tokenizer;

import java.util.NoSuchElementException;

/**
 * File created on 21.10.2023
 */
public class NoTokenTypeFoundException extends NoSuchElementException {

    public NoTokenTypeFoundException(String msg) {
        super(msg);
    }

}
