package de.nikschadowsky.baall.compiler.grammar;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

public class GrammarDerivation {

    private final GrammarSymbol[] derivation;

    public GrammarDerivation(GrammarSymbol... symbols) {
        this.derivation = symbols;
    }

    public GrammarSymbol[] getDerivation() {
        return derivation;
    }

    public int getDerivationSymbolCount() {
        return derivation.length;
    }


    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder("[");

        boolean empty = true;

        for (GrammarSymbol s : derivation) {
            if (s instanceof GrammarNonterminal) {
                builder.append(((GrammarNonterminal) s).getIdentifier());
            } else if (s instanceof Token) {
                builder.append("\"");
                builder.append(((Token) s).getValue());
                builder.append("\"");
            }

            builder.append(" ");

            empty = false;
        }
        if (!empty) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");

        return builder.toString();
    }
}
