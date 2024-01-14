package de.nikschadowsky.baall.compiler.grammar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Set;

public class Grammar {


    private final GrammarNonterminal start;

    private final Set<GrammarNonterminal> nonterminals;


    public Grammar(
            @NotNull(value = "Start Symbol cannot be NULL", exception = GrammarSyntaxException.class)
            GrammarNonterminal start,
            @NotNull(value = "Set cannot be NULL", exception = GrammarSyntaxException.class)
            Set<GrammarNonterminal> nonterminals
    ) {

        this.start = start;
        this.nonterminals = nonterminals;

    }

    public GrammarNonterminal getStart() {
        return start;
    }

    public @Unmodifiable Set<GrammarNonterminal> getAllNonterminals() {
        return Collections.unmodifiableSet(nonterminals);
    }
}
