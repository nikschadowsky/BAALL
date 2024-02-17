package de.nikschadowsky.baall.compiler.grammar;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

import java.util.Objects;
import java.util.stream.IntStream;

public class GrammarProduction {

    private final GrammarSymbol[] sententialForm;

    private final int productionRuleIdentifier;

    public GrammarProduction(int productionRuleIdentifier, GrammarSymbol... symbols) {
        this.productionRuleIdentifier = productionRuleIdentifier;
        this.sententialForm = symbols;
    }

    public int getProductionRuleIdentifier() {
        return productionRuleIdentifier;
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

        StringBuilder builder = new StringBuilder();

        builder.append(productionRuleIdentifier);
        builder.append(":");

        if (isEpsilonProduction()) {
            builder.append(" ε");
        }

        for (GrammarSymbol s : sententialForm) {
            builder.append(" ");

            if (s instanceof GrammarNonterminal) {
                builder.append(((GrammarNonterminal) s).getIdentifier());
            } else if (s instanceof Token) {
                builder.append("\"");
                builder.append(((Token) s).value());
                builder.append("\"");
            }
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrammarProduction gp) {
            Objects.requireNonNull(gp.getSententialForm());

            if (getSentenceLength() != gp.getSentenceLength()) return false;

            return IntStream.range(0, getSentenceLength())
                                .allMatch(i -> sententialForm[i].symbolEquals(
                                        gp.getSententialForm()[i]));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionRuleIdentifier);
    }
}
