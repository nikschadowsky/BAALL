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

    @Override
    public boolean isTerminal() {
        return false;
    }

    /**
     * Compares GrammarNonterminal to Symbol. If s is an instance of GrammarNonterminal they are compared accordingly. False otherwise.
     * Compares the identifiers of both Nonterminals.
     * @param s GrammarSymbol to compare to
     * @return if symbols match
     */
    @Override
    public boolean symbolMatches(GrammarSymbol s) {

        // if we have
        if(s instanceof GrammarNonterminal n){
            return getIdentifier().equals(n.getIdentifier());
        }

        return false;
    }
}
