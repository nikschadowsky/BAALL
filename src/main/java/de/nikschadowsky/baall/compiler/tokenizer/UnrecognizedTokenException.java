package de.nikschadowsky.baall.compiler.tokenizer;

import de.nikschadowsky.baall.compiler.lexer.LexerRow;
import de.nikschadowsky.baall.compiler.output.error.CompileException;

public class UnrecognizedTokenException extends CompileException {

    public UnrecognizedTokenException(LexerRow row, int index) {
        super("Unrecognized token at index " + index + ": " + row.rowIndex());
    }

    @Deprecated
    public UnrecognizedTokenException(int index) {
        super("Unrecognized token at index " + index);
    }
}
