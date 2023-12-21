package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;

import java.util.Stack;

public class ParserStack extends Stack<GrammarSymbol> {

    /**
     * Constructs a new ParserStack with an empty Stack.
     */
    public ParserStack(GrammarSymbol initial) {
        push(initial);
    }

    /**
     * Pushes an Array of GrammarSymbols in reversed order onto the stack e.g. adding 'abc' results in 'a' being on top
     * of the stack, followed by 'b' and finally 'c'.
     *
     * @param symbols Array of symbols to be pushed
     */
    public void pushSeries(GrammarSymbol[] symbols) {

        for (int i = symbols.length; i > 0; i--) {
            push(symbols[i - 1]);
        }

    }


}
