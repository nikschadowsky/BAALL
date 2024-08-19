package de.nikschadowsky.baall.compiler.syntaxtree.util;

import de.nikschadowsky.baall.compiler.symbol.Token;

/**
 * @since 14.04.2024
 */
public record NodeDiagnostic(String message, Token source, ReportingLevel reportingLevel) {

    @Override
    public String toString() {
        String sourceString = source != null ? "@" + source : "";

        return "NodeDiagnostic@%s: %s".formatted(sourceString, message);
    }

    public enum ReportingLevel {
        INFO, WARN, ERROR
    }
}
