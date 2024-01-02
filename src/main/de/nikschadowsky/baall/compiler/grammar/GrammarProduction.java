package de.nikschadowsky.baall.compiler.grammar;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

public class GrammarProduction {

    private final GrammarSymbol[] sententialForm;

    private final GrammarNonterminal leftSideSymbol;

    private final int productionRuleIdentifier;

    public GrammarProduction(int productionRuleIdentifier, GrammarNonterminal leftSideSymbol, GrammarSymbol... symbols) {
        this.productionRuleIdentifier = productionRuleIdentifier;
        this.leftSideSymbol = leftSideSymbol;
        this.sententialForm = symbols;
    }


    public int getProductionRuleIdentifier() {
        return productionRuleIdentifier;
    }

    public GrammarNonterminal getLeftSideSymbol() {
        return leftSideSymbol;
    }

    public GrammarSymbol[] getSententialForm() {
        return sententialForm;
    }

    public int getSentenceLength() {
        return sententialForm.length;
    }

    public boolean isEpsilonProduction() {
        return getSentenceLength() == 0;
    }

    @Override
    public String toString() {

        boolean empty = true;
        StringBuilder builder = new StringBuilder();

        builder.append(productionRuleIdentifier);
        builder.append(": ");

        builder.append(leftSideSymbol.getIdentifier());
        builder.append(" ->");

        if (getSentenceLength() == 0) {
            builder.append(" ε");
        }

        for (GrammarSymbol s : sententialForm) {
            builder.append(" ");

            if (s instanceof GrammarNonterminal) {
                builder.append(((GrammarNonterminal) s).getIdentifier());
            } else if (s instanceof Token) {
                builder.append("\"");
                builder.append(((Token) s).getValue());
                builder.append("\"");
            }
        }

        return builder.toString();
    }
}
