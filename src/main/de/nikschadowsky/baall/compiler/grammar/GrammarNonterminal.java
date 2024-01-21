package de.nikschadowsky.baall.compiler.grammar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Set;

public class GrammarNonterminal implements GrammarSymbol {

    private Set<GrammarProduction> productionRules;

    private final String identifier;

    private boolean isProductionRulesSet = false;

    public GrammarNonterminal(@NotNull String identifier) {
        this.identifier = identifier;
    }

    /**
     * Sets this nonterminal's production rules
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

    /**
     * @return an immutable set of this nonterminal's production rules
     */
    public @Unmodifiable Set<GrammarProduction> getProductionRules() {
        return Collections.unmodifiableSet(productionRules);
    }

    @Override
    public String toString() {
        return "GrammarNonterminal { " + getIdentifier() + ", " + getProductionRules() + "}";
    }

    @Override
    public String getFormatted() {
        return getIdentifier();
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    /**
     * Compares this GrammarNonterminal to a GrammarSymbol. If s is an instance of GrammarNonterminal their identifiers
     * are compared. False otherwise.
     *
     * @param s GrammarSymbol to compare to
     * @return if symbols match
     */
    @Override
    public boolean symbolMatches(GrammarSymbol s) {
        if (s instanceof GrammarNonterminal g) {
            return getIdentifier().equals(g.getIdentifier());
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrammarNonterminal g) {
            boolean rulesMatch;

            if (productionRules == null) {
                rulesMatch = g.productionRules == null;
            } else {
                rulesMatch = productionRules.equals(g.productionRules);
            }
            return getIdentifier().equals(g.getIdentifier()) && rulesMatch;
        }
        return false;
    }
}
