package de.nikschadowsky.baall.compiler.grammar;

public class GrammarSyntaxException extends RuntimeException {

    public GrammarSyntaxException(String additionalInformation, String reason) {
        super("Invalid syntax! %s%nReason: %s".formatted(additionalInformation, reason));
    }

    public GrammarSyntaxException(String reason) {
        this("", reason);
    }

}
