package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

import java.util.*;

/**
 * File created on 26.12.2023
 */
public class ProductionRuleCandidateSelector {

    private ProductionRuleCandidateSelector() {

    }

    public static Optional<GrammarProduction> determineCandidate(GrammarNonterminal symbol, Queue<Token> tokenQueue) {

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


}
