package de.nikschadowsky.baall.compiler.grammar;

public interface GrammarSymbol {

    /**
     * Used to differentiate between Terminal Symbols and Nonterminal Symbols
     *
     * @return isTerminal
     */
    boolean isTerminal();

    /**
     * Compares Symbols if they are of the same type (e.g. Token or GrammarNonterminal). Else false.
     *
     * @return if symbols match
     */
    boolean symbolMatches(GrammarSymbol s);

    boolean symbolEquals(GrammarSymbol s);

    /**
     * @return a formatted representation of this symbol
     */
    String getFormatted();

}
