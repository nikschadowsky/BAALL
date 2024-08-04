package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * File created on 11.07.2024
 *
 * TODO hold a queue mark as a field. when #isSuccessful then step queue to the mark
 */
class ParseResult<T> {

    private final @Nullable T parseResult;
    private final @Nullable SyntaxDiagnostic diagnostic;

    public static <T> ParseResult<T> successfulParse(T parseResult) {
        return new ParseResult<>(parseResult);
    }

    public static <T> ParseResult<T> unsuccessfulParse(SyntaxDiagnostic diagnostic) {
        return new ParseResult<>(diagnostic);
    }

    private ParseResult(@Nullable T parseResult) {
        this.parseResult = parseResult;
        this.diagnostic = null;
    }

    // constructor for EMPTY
    private ParseResult(@Nullable SyntaxDiagnostic diagnostic) {
        this.parseResult = null;
        this.diagnostic = diagnostic;
    }

    public T getParseResult() {
        if (isUnsuccessful()) {
            throw new NoSuchElementException("Cannot get node for a unsuccessful parse result!");
        }
        return parseResult;
    }

    public SyntaxDiagnostic getDiagnostic() {
        if (isSuccessful()) {
            throw new NoSuchElementException("Cannot get diagnostic for a successful parse result!");
        }
        return diagnostic;
    }

    public boolean isUnsuccessful() {
        return parseResult == null;
    }

    public boolean isSuccessful() {
        return !isUnsuccessful();
    }
}
