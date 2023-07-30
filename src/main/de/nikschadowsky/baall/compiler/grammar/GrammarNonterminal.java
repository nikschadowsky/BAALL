package main.de.nikschadowsky.baall.compiler.grammar;

import java.util.List;

public class GrammarNonterminal implements GrammarSymbol {

    private List<GrammarDerivation> derivationList;

    private final String identifier;

    private boolean isDerivationSet = false;

    public GrammarNonterminal(String identifier) {
        this.identifier = identifier;
    }

    public boolean setDerivationList(List<GrammarDerivation> derivations) {

        if (!isDerivationSet) {
            this.derivationList = derivations;
            isDerivationSet = true;
            return true;
        }

        return false;
    }

    @Override
    public final boolean equalsSymbol(GrammarSymbol s) {
        return getClass().equals(s.getClass());
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<GrammarDerivation> getDerivationList() {
        return derivationList;
    }

    @Override
    public String toString() {
        return "GrammarNonterminal { " + getIdentifier() + ", " + getDerivationList() + "}";
    }
}
