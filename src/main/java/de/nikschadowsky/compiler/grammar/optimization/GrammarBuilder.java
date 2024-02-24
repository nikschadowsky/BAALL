package de.nikschadowsky.compiler.grammar.optimization;

import de.nikschadowsky.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.compiler.grammar.GrammarProduction;
import de.nikschadowsky.compiler.grammar.GrammarSymbol;
import de.nikschadowsky.compiler.grammar.generation.GrammarProductionUIDGenerator;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * File created on 01.02.2024
 */
public class GrammarBuilder {

    private GrammarBuilder() {
    }

    public static class GrammarNonterminalBuilder {

        private final GrammarNonterminal nonterminal;
        private final ProductionRuleBuilder ruleBuilder;
        private final boolean isStartNonterminalBuilder;

        public GrammarNonterminalBuilder(
                GrammarNonterminal oldNonterminal,
                @Unmodifiable Map<GrammarNonterminal, GrammarNonterminal> nonterminalTranslationMap
        ) {
            this.nonterminal = nonterminalTranslationMap.get(oldNonterminal);
            this.nonterminal.addAnnotations(oldNonterminal.getAnnotations());
            this.ruleBuilder =
                    new ProductionRuleBuilder(oldNonterminal.getProductionRules(), nonterminalTranslationMap);
            this.isStartNonterminalBuilder = nonterminal.hasAnnotation("Start");
        }

        public ProductionRuleBuilder getRuleBuilder() {
            return ruleBuilder;
        }

        public boolean isStartNonterminalBuilder(){
            return isStartNonterminalBuilder;
        }

        public GrammarNonterminal build() {
            nonterminal.addProductionRules(ruleBuilder.build());
            return nonterminal;
        }
    }

    public static class ProductionRuleBuilder {

        private final List<List<GrammarSymbol>> sententialForms;

        public ProductionRuleBuilder(Set<GrammarProduction> originalRules, Map<GrammarNonterminal, GrammarNonterminal> nonterminalTranslationMap) {
            sententialForms = originalRules.stream()
                                           .map(rule -> translateProductionRule(rule, nonterminalTranslationMap))
                                           .collect(Collectors.toList());
        }

        private List<GrammarSymbol> translateProductionRule(GrammarProduction rule, Map<GrammarNonterminal, GrammarNonterminal> translationMap) {
            return Arrays.stream(rule.getSententialForm())
                         .map(symbol -> {
                             if (symbol.isTerminal()) return symbol;
                             return translationMap.get((GrammarNonterminal) symbol);
                         })
                         .collect(Collectors.toList());
        }

        public boolean containsNonterminal(GrammarNonterminal target) {
            return sententialForms.stream().anyMatch(form -> form.contains(target));
        }

        public boolean hasAllTerminalProductions() {
            return sententialForms.stream().allMatch(sentence -> sentence.stream().allMatch(GrammarSymbol::isTerminal));
        }

        public @Unmodifiable Set<List<GrammarSymbol>> getSententialForms() {
            return sententialForms.stream().collect(Collectors.toUnmodifiableSet());
        }

        public void replace(GrammarNonterminal old, Set<List<GrammarSymbol>> replacements) {
            // if the set is empty, this would result in every rule containing old being deleted!
            if (replacements.isEmpty()) throw new IllegalArgumentException("Replacement set cannot be empty!");

            ListIterator<List<GrammarSymbol>> ruleIterator = sententialForms.listIterator();

            while (ruleIterator.hasNext()) {
                List<GrammarSymbol> oldSequence = ruleIterator.next();

                if (oldSequence.contains(old)) {
                    ruleIterator.remove();
                    replacements.forEach(newSymbolSequence -> {
                        List<GrammarSymbol> newSequence = new LinkedList<>(oldSequence);

                        int oldIndex = newSequence.indexOf(old);
                        newSequence.remove(old);
                        newSequence.addAll(oldIndex, newSymbolSequence);

                        ruleIterator.add(newSequence);
                    });

                }
            }
        }

        protected Set<GrammarProduction> build() {
            return sententialForms.stream()
                                  .map(list -> new GrammarProduction(
                                          GrammarProductionUIDGenerator.getInstance().nextID(),
                                          list.toArray(GrammarSymbol[]::new)
                                  ))
                                  .collect(Collectors.toSet());

        }

    }
}
