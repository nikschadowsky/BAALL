package de.nikschadowsky.baall.compiler.syntax.analysis.result;

import de.nikschadowsky.baall.compiler.symbol.TokenQueueId;
import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import org.jetbrains.annotations.Nullable;

/**
 * @since 07.08.2024
 */
// todo maybe move partial into super class?
public class PartialParseResult<T> extends ParseResult<T> {

    private final boolean partial;

    public static <T> PartialParseResult<T> successfulParse(T result, TokenQueueId tokenQueueId) {
        return new PartialParseResult<>(result, false, null, tokenQueueId);
    }

    public static <T> PartialParseResult<T> partialParse(T result, SyntaxDiagnostic diagnostic, TokenQueueId tokenQueueId) {
        return new PartialParseResult<>(result, true, diagnostic, tokenQueueId);
    }

    public static <T> PartialParseResult<T> unsuccessfulParse(SyntaxDiagnostic diagnostic, TokenQueueId tokenQueueId) {
        return new PartialParseResult<>(null, false, diagnostic, tokenQueueId);
    }

    private PartialParseResult(@Nullable T parseResult, boolean partial, @Nullable SyntaxDiagnostic diagnostic, TokenQueueId tokenQueueId) {
        super(parseResult, diagnostic, tokenQueueId);
        this.partial = partial;
    }


    public boolean isPartial() {
        return partial;
    }

    @Override
    public boolean isSuccessful() {
        return super.isSuccessful() && !partial;
    }

    @Override
    public boolean isUnsuccessful() {
        return super.isUnsuccessful() && !partial;
    }

}
