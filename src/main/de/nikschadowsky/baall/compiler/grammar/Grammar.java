package de.nikschadowsky.baall.compiler.grammar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

public class Grammar {


    private final GrammarNonterminal start;

    private final Set<GrammarNonterminal> nonterminals;

    private final Set<GrammarProduction> grammarProductions;

    public Grammar(
            @NotNull(value = "Start symbol cannot be NULL", exception = GrammarSyntaxException.class)
            GrammarNonterminal start,
            @NotNull(value = "Nonterminal set cannot be NULL", exception = GrammarSyntaxException.class)
            Set<GrammarNonterminal> nonterminals,
            @NotNull(value = "Production rule set cannot be NULL", exception = GrammarSyntaxException.class)
            Set<GrammarProduction> grammarProductions
    ) {
        this.start = start;
        this.nonterminals = nonterminals;
        this.grammarProductions = grammarProductions;
    }

    public @NotNull GrammarNonterminal getStart() {
        return start;
    }

    public @Unmodifiable Set<GrammarNonterminal> getAllNonterminals() {
        return Collections.unmodifiableSet(nonterminals);
    }

    public @NotNull GrammarProduction getRuleQualifiedById(int identifier) {
        return grammarProductions.stream()
                .filter(rule -> rule.getProductionRuleIdentifier() == identifier)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("No production rule with id %s found in this grammar!".formatted(identifier)));
    }
}
