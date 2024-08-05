package de.nikschadowsky.baall.compiler.syntax.analysis;

import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * File created on 11.07.2024
 */
class ParseResult<T> {

    private final @Nullable T parseResult;
    private final @Nullable SyntaxDiagnostic diagnostic;
    private final boolean complete;

    public static <T> ParseResult<T> successfulParse(T parseResult) {
        return new ParseResult<>(parseResult, true, null);
    }

    public static <T> ParseResult<T> unsuccessfulParse(SyntaxDiagnostic diagnostic) {
        return new ParseResult<>(null, false, diagnostic);
    }

    public static <T> ParseResult<T> incompleteParse(T parseResult, SyntaxDiagnostic diagnostic) {
        return new ParseResult<>(parseResult, false, diagnostic);
    }

    private ParseResult(@Nullable T parseResult, boolean complete, @Nullable SyntaxDiagnostic diagnostic) {
        this.parseResult = parseResult;
        this.complete = complete;
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
        return !complete && parseResult == null;
    }

    public boolean isSuccessful() {
        return complete;
    }

    public boolean isIncomplete() {
        return !complete && parseResult != null;
    }
}
