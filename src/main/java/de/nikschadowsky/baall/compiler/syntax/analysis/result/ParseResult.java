package de.nikschadowsky.baall.compiler.syntax.analysis.result;

import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * File created on 11.07.2024
 */
public class ParseResult<T> {

    private final @Nullable T parseResult;
    private final @Nullable SyntaxDiagnostic diagnostic;

    public static <T> ParseResult<T> successfulParse(T parseResult) {
        return new ParseResult<>(parseResult, null);
    }

    public static <T> ParseResult<T> unsuccessfulParse(SyntaxDiagnostic diagnostic) {
        return new ParseResult<>(null, diagnostic);
    }

    protected ParseResult(@Nullable T parseResult, @Nullable SyntaxDiagnostic diagnostic) {
        this.parseResult = parseResult;
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
