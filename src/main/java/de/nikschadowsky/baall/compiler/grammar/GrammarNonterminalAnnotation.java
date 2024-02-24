package de.nikschadowsky.baall.compiler.grammar;

import java.util.Objects;

/**
 * File created on 21.01.2024
 */
public record GrammarNonterminalAnnotation(String value) {

    @Override
    public String toString() {
        return "@" + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrammarNonterminalAnnotation annotation) {
            return value.equals(annotation.value);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
