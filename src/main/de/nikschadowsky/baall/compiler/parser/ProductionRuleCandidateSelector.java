package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;

public class ProductionRuleCandidateSelector {

    public ProductionRuleCandidateSelector() {

    }

    public Optional<GrammarProduction> determineCandidate(GrammarNonterminal symbol, ListIterator<Token> tokenIterator) {
        ParserStack stack;

        Set<GrammarProduction> remainingProductionRules = symbol.getProductionRules();

        Iterator<GrammarProduction> remainingProductionRulesIterator = remainingProductionRules.iterator();

        while (tokenIterator.hasNext() && remainingProductionRules.size() > 1) {

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

        }


        return remainingProductionRules.parallelStream().findAny();
    }


}
