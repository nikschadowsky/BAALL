package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * File created on 26.12.2023
 */
public class ProductionRuleCandidateSelector {

    private ProductionRuleCandidateSelector() {

    }

    public static Optional<GrammarProduction> determineCandidate1(GrammarNonterminal symbol, Queue<Token> tokenQueue) {

        tokenQueue = new LinkedList<>(tokenQueue); // defensive copy

        ParserStack stack;

        Set<GrammarProduction> remainingProductionRules = symbol.getProductionRules();

        Iterator<GrammarProduction> remainingProductionRulesIterator = remainingProductionRules.iterator();

        boolean hasEpsilonRule = remainingProductionRules.stream().anyMatch(GrammarProduction::isEpsilonProduction);

        // Function<Integer, Boolean> f = size -> hasEpsilonRule ? size > 2 : size > 1;

        int index = 0;

        do {

            Token nextToken = tokenQueue.poll();

            while (remainingProductionRulesIterator.hasNext()) {

                GrammarProduction gp = remainingProductionRulesIterator.next();

                if (gp.getSentenceLength() > index) {
                    // it's sufficient to not check whether the symbol at index is terminal, since the token queue
                    // always gives a token and nonterminal != token f.a. token.

                    GrammarSymbol current = gp.getSententialForm()[index];
                    GrammarSymbol candidate;


                    if ((current.isTerminal() && !gp.getSententialForm()[index].symbolMatches(nextToken))
                            || (!current.isTerminal() && determineCandidate((GrammarNonterminal) current, tokenQueue).isEmpty())) {
                        remainingProductionRulesIterator.remove();
                    }
                }

            }

            index++;
        } while (!tokenQueue.isEmpty() && remainingProductionRules.size() > 1);

        /*while (tokenIterator.hasNext() && remainingProductionRules.size() > 1) {

            int index = tokenIterator.nextIndex();
            Token nextToken = tokenIterator.next();

            while (remainingProductionRulesIterator.hasNext()) {
                GrammarProduction rule = remainingProductionRulesIterator.next();

                if (rule.getSentenceLength() > index) {

                    if (rule.getSententialForm()[index].isTerminal())
                        if (!rule.getSententialForm()[index].symbolMatches(nextToken)) {

                            remainingProductionRules.remove(rule);
                        } else {

                        }

                }// when the index is larger than the length of a rule's sentential form,

            }

        }*/


        return remainingProductionRules.stream().findAny();
    }

    public static Optional<GrammarProduction> determineCandidate(GrammarNonterminal lss, Queue<Token> tokenQueue){
        return
    }

    private static Optional<ProductionRuleIntermediate> determineIntermediateCandidate(GrammarNonterminal lss, Queue<Token> tokenQueue) {

        tokenQueue = new LinkedList<>(tokenQueue);

        int index = 0;

        Set<GrammarProduction> rulesToCheck = lss.getProductionRules().stream().filter(gp -> !gp.isEpsilonProduction()).collect(Collectors.toSet());

        Optional<GrammarProduction> epsilonRule = lss.getProductionRules().stream().filter(GrammarProduction::isEpsilonProduction).findFirst();

        Set<GrammarNonterminal> deepCheck = new HashSet<>();


        // do at least one check.
        do {
            Iterator<GrammarProduction> rulesToCheckIterator = rulesToCheck.iterator();



            while (rulesToCheckIterator.hasNext()) {
                GrammarProduction rule = rulesToCheckIterator.next();

                if (rule.getSentenceLength() > index) {

                    GrammarSymbol symbol = rule.getSententialForm()[index];

                    if (symbol.isTerminal() && !symbol.symbolMatches(tokenQueue.peek())) {
                        rulesToCheckIterator.remove();
                    } else if (!symbol.isTerminal()) {
                        // check production of this nonterminal

                        deepCheck.add((GrammarNonterminal) symbol);
                    }

                }
            }

            for (GrammarNonterminal n : deepCheck) {

            }

            Queue<Token> finalTokenQueue = tokenQueue;
            Set<GrammarNonterminal> collect = deepCheck.stream()
                    .map(n -> new ProductionRuleIntermediate(n, determineCandidate(n, finalTokenQueue)))
                    .filter(ProductionRuleIntermediate::isEmpty).map(ProductionRuleIntermediate::lss)
                    .collect(Collectors.toSet());

            rulesToCheck.forEach(rule -> {
                if(!rule.getSententialForm()[index].isTerminal() && collect.contains(rule.getSententialForm()[index])){

                }
            });


        } while (!tokenQueue.isEmpty() && rulesToCheck.size() > 1);

        if (rulesToCheck.isEmpty()) {
          //  return epsilonRule;
        }

        //return rulesToCheck.stream().findFirst();
        return null;
    }

    private record ProductionRuleIntermediate(GrammarNonterminal lss, Optional<GrammarProduction> candidate) {
        boolean isEmpty(){
            return candidate.isEmpty();
        }
    }

}
