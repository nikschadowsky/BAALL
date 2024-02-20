package de.nikschadowsky.baall.compiler.grammar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GrammarNonterminal implements GrammarSymbol {

    private final Set<GrammarNonterminalAnnotation> annotations = new HashSet<>();
    private final Set<GrammarProduction> productionRules = new HashSet<>();

    private final String identifier;

    private boolean areProductionRulesSet = false;
    private boolean areAnnotationsSet = false;

    public GrammarNonterminal(@NotNull String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * Adds production rules to this nonterminal and sets them unmodifiable.
     *
     * @param newProductionRules production rules to add
     * @return whether this operation was successful
     */
    public boolean addProductionRules(Set<GrammarProduction> newProductionRules) {
        if (!areProductionRulesSet) {
            productionRules.addAll(newProductionRules);
            areProductionRulesSet = true;
            return true;
        }
        return false;
    }


    /**
     * @return an immutable set of this nonterminal's production rules
     */
    public @Unmodifiable Set<GrammarProduction> getProductionRules() {
        return Collections.unmodifiableSet(productionRules);
    }

    public boolean areProductionRulesSet() {
        return areProductionRulesSet;
    }

    /**
     * Adds annotations to this nonterminal and sets them unmodifiable.
     *
     * @param newAnnotations annotations to add
     * @return whether this operation was successful
     */
    public boolean addAnnotations(Set<GrammarNonterminalAnnotation> newAnnotations) {
        if (!areAnnotationsSet) {
            annotations.addAll(newAnnotations);
            areAnnotationsSet = true;
            return true;
        }
        return false;
    }

    public @Unmodifiable Set<GrammarNonterminalAnnotation> getAnnotations() {
        return Collections.unmodifiableSet(annotations);
    }

    public boolean hasAnnotation(@NotNull String value) {
        return annotations.contains(new GrammarNonterminalAnnotation(value));
    }

    public boolean areAnnotationsSet() {
        return areAnnotationsSet;
    }

    @Override
    public String toString() {
        return "%s -> %s %s"
                .formatted(
                        identifier,
                        String.join(
                                " | ",
                                productionRules.stream()
                                               .map(GrammarProduction::toString)
                                               .toList()),
                        String.join(
                                " ",
                                annotations.stream()
                                           .map(GrammarNonterminalAnnotation::toString)
                                           .toList()
                        )
                );
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
    public boolean symbolEquals(GrammarSymbol s) {
        if (s instanceof GrammarNonterminal other) {
            return getIdentifier().equals(other.getIdentifier()) && annotations.equals(other.annotations);
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrammarNonterminal g) {
            return getIdentifier().equals(g.getIdentifier()) && productionRules.equals(g.productionRules);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, productionRules);
    }
}
