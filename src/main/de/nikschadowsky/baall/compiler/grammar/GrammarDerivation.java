package main.de.nikschadowsky.baall.compiler.grammar;

import main.de.nikschadowsky.baall.compiler.lexer.tokens.Token;

import java.util.List;

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

    public boolean matches(List<GrammarSymbol> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (!tokens.get(i).equalsSymbol(derivation[i])) return false;
        }

        return true;
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
