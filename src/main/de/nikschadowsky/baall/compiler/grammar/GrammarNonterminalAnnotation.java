package de.nikschadowsky.baall.compiler.grammar;

/**
 * File created on 21.01.2024
 */
public record GrammarNonterminalAnnotation(String value) {

    @Override
    public String toString() {
        return "[GrammarNonterminalAnnotation]@" + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrammarNonterminalAnnotation annotation) {
            return value.equalsIgnoreCase(annotation.value);
        }

        return false;
    }
}
