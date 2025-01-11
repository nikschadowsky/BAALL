package de.nikschadowsky.baall.compiler.tokenizer;

import java.util.NoSuchElementException;

/**
 * @since 21.10.2023
 */
public class NoTokenTypeFoundException extends NoSuchElementException {

    public NoTokenTypeFoundException(String msg) {
        super(msg);
    }

}
