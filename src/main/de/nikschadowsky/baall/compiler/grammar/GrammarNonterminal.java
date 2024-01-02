package de.nikschadowsky.baall.compiler.grammar;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
     * Compares GrammarNonterminal to Symbol. If s is an instance of GrammarNonterminal they are compared accordingly.
     * False otherwise. Compares the identifiers of both Nonterminals.
     *
     * @param s GrammarSymbol to compare to
     * @return if symbols match
     */
    @Override
    public boolean symbolMatches(@NotNull GrammarSymbol s) {

        // if we have
        if (s instanceof GrammarNonterminal n) {
            return getIdentifier().equals(n.getIdentifier());
        }

        return false;
    }
}
