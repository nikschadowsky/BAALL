package main.de.nikschadowsky.baall.compiler.grammar;

public interface GrammarSymbol {

    /**
     * Used to differentiate between Terminal Symbols and Nonterminal Symbols
     * @return isTerminal
     */
    boolean isTerminal();

}
