package de.nikschadowsky.baall.compiler.syntax.analysis.result;

import de.nikschadowsky.baall.compiler.symbol.TokenQueueId;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * @since 11.07.2024
 */
public class ParseResult<T> {

    private final @Nullable T parseResult;
    private final @Nullable SyntaxDiagnostic diagnostic;
    private final @NotNull TokenQueueId tokenQueueId;

    public static <T> ParseResult<T> successfulParse(T parseResult, TokenQueueId tokenQueueId) {
        return new ParseResult<>(parseResult, null, tokenQueueId);
    }

    public static <T> ParseResult<T> unsuccessfulParse(SyntaxDiagnostic diagnostic, TokenQueueId tokenQueueId) {
        return new ParseResult<>(null, diagnostic, tokenQueueId);
    }

    protected ParseResult(@Nullable T parseResult, @Nullable SyntaxDiagnostic diagnostic, @NotNull TokenQueueId tokenQueueId) {
        this.parseResult = parseResult;
        this.diagnostic = diagnostic;
        this.tokenQueueId = tokenQueueId;
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

    public @NotNull TokenQueueId getTokenQueueId() {
        return tokenQueueId;
    }

    public boolean isUnsuccessful() {
        return parseResult == null;
    }

    public boolean isSuccessful() {
        return !isUnsuccessful();
    }
}
