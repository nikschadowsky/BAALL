package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * File created on 26.12.2023
 */
public class ProductionRuleCandidateSelector {

    private ProductionRuleCandidateSelector() {
    }

    /**
     * Determine a production rule candidate for a given nonterminal. Returns an optional of type
     * {@link GrammarProduction}.
     * <p>
     * If there is only one production rule, then this method immediately return it. If there is an ε-rule
     * (epsilon-rule) and there is another matching rule, this method returns an optional of the matching rule and
     * ignores the ε-rule. If there are multiple candidates, but they all get rolled out after evaluating, this method
     * returns the epsilon rule or an empty optional if there is no ε-rule.
     *
     * @param lss        the grammar nonterminal of which to determine a candidate
     * @param tokenQueue the queue of tokens to evaluate the candidate
     * @return either
     * <ul>
     *     <li>empty optional if there is no candidate (not even an ε-rule)</li>
     *     <li>optional of an production rule containing either an ε-rule or a regular rule
     *     depending on whichever matches the queue</li>
     * </ul>
     */
    public static Optional<GrammarProduction> determineCandidate(GrammarNonterminal lss, Queue<Token> tokenQueue) {
        tokenQueue = new LinkedList<>(tokenQueue);

        Set<GrammarProduction> rulesUnfiltered = lss.getProductionRules();

        Queue<Token> finalTokenQueue = tokenQueue;

        Set<PRIntermediate> rulesEpsilonFiltered =
                rulesUnfiltered
                        .stream()
                        .filter(r -> !r.isEpsilonProduction())
                        .map(r -> new PRIntermediate(r, finalTokenQueue))
                        .collect(Collectors.toSet());


        while (rulesEpsilonFiltered.size() > 1 && !tokenQueue.isEmpty()) {
            rulesEpsilonFiltered.removeIf(intermediate -> !intermediate.step());
        }

        // tokenQueue was empty, and we have an ambiguous result -> no candidate
        if (rulesEpsilonFiltered.size() > 1) {
            return Optional.empty();
        }
        // get the determined candidate, or if there is none get an optional epsilon rule or none all together.
        return rulesEpsilonFiltered
                .stream()
                .map(PRIntermediate::getRule)
                .findAny()
                .or(() -> rulesUnfiltered.stream().filter(GrammarProduction::isEpsilonProduction).findAny());
    }

    /**
     * Class for containing the state of the candidate selector for each production rule
     */
    protected static class PRIntermediate {

        private final ParserStack stack;

        private final Queue<Token> tokenQueue;

        private final GrammarProduction rule;

        private PRIntermediate(@NotNull GrammarProduction rule, @NotNull Queue<Token> tokenQueue) {
            this.rule = rule;
            this.tokenQueue = new LinkedList<>(tokenQueue); // defensive copy
            // init stack
            stack = new ParserStack();
            stack.pushSeries(rule.getSententialForm());
        }

        public GrammarProduction getRule() {
            return rule;
        }

        /**
         * Performs a step in reducing the parser stack to check, whether this production is valid.
         *
         * @return if the top of the stack was derived successfully
         */
        public boolean step() {

            if (!stack.isEmpty() && !tokenQueue.isEmpty()) {
                GrammarSymbol symbol = stack.pop();

                if (symbol.isTerminal()) {
                    return symbol.symbolMatches(tokenQueue.poll());
                } else {
                    Optional<GrammarProduction> candidate = determineCandidate((GrammarNonterminal) symbol, tokenQueue);

                    if (candidate.isEmpty()) return false;

                    int stackSizeTarget = stack.size();
                    stack.pushSeries(candidate.get().getSententialForm());

                    // run stack down until only the initial state minus the stack top remains
                    while (stack.size() > stackSizeTarget) {
                        if (!step()) return false; // if step fails, then the outer step has to also report a fail
                    }

                    return true;
                }
            }

            return false;
        }
    }

}
