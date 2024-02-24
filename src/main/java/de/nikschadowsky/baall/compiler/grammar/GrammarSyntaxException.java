package de.nikschadowsky.baall.compiler.grammar;

public class GrammarSyntaxException extends RuntimeException {

    public GrammarSyntaxException(String additionalInformation, String reason) {
        super("Invalid grammar syntax! %s%nReason: %s".formatted(additionalInformation, reason));
    }

    public GrammarSyntaxException(String reason) {
        this("", reason);
    }

    public GrammarSyntaxException(String additionalInformation, String reason, Throwable cause) {
        super("Invalid grammar syntax! %s%nReason: %s".formatted(additionalInformation, reason), cause);
    }

    public GrammarSyntaxException(String reason, Throwable cause) {
        this("", reason, cause);
    }
}
