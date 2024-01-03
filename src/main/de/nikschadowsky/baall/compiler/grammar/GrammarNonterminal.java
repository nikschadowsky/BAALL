package de.nikschadowsky.baall.compiler.grammar;

import java.util.Set;

public class GrammarNonterminal implements GrammarSymbol {

    private Set<GrammarProduction> productionRules;

    private final String identifier;

    private boolean isProductionRulesSet = false;

    public GrammarNonterminal(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Sets this Nonterminal's Production rules
     *
     * @param productionRules production rules of this Nonterminal
     * @return whether this operation was successful
     */
    public boolean setProductionRules(Set<GrammarProduction> productionRules) {

        if (!isProductionRulesSet) {
            this.productionRules = productionRules;
            isProductionRulesSet = true;
            return true;
        }

        return false;
    }


    public String getIdentifier() {
        return identifier;
    }

    public Set<GrammarProduction> getProductionRules() {
        return productionRules;
    }

    @Override
    public String toString() {
        return "GrammarNonterminal { " + getIdentifier() + ", " + getProductionRules() + "}";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    /**
     * Compares GrammarNonterminal to Symbol. If s is an instance of GrammarNonterminal their identifiers are compared.
     * False otherwise.
     *
     * @param s GrammarSymbol to compare to
     * @return if symbols match
     */
    @Override
    public boolean symbolMatches(GrammarSymbol s) {
        return equals(s);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrammarNonterminal g) {
            return identifier.equals(g.getIdentifier());
        }
        return false;
    }
}
