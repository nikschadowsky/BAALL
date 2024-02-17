package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.ConcreteSyntaxTree;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeInternalNode;
import de.nikschadowsky.baall.compiler.syntaxtree.cst.node.ConcreteSyntaxTreeLeafNode;
import de.nikschadowsky.baall.compiler.util.CollectionUtility;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * File created on 21.07.2023
 */
public class Parser {

    public ConcreteSyntaxTree parse(Grammar grammar, List<Token> tokens) {

        return generateConcreteSyntaxTree(grammar, tokens, determineAppliedRules(grammar, tokens));
    }

    private List<Integer> determineAppliedRules(Grammar grammar, List<Token> tokens) {

        List<Integer> appliedProductionRules = new LinkedList<>();
        Queue<Token> tokenQueue = new LinkedList<>(tokens);
        ParserStack stack = new ParserStack(grammar.getStart());

        while (!stack.empty() && !tokenQueue.isEmpty()) {

            GrammarSymbol currentSymbol = stack.pop();

            if (currentSymbol.isTerminal()) {
                Token currentToken = tokenQueue.remove();

                if (!currentSymbol.symbolMatches(currentToken)) {
                    throw new SyntaxException(String.format(
                            "Expected: %s; Got %s",
                            currentSymbol,
                            CollectionUtility.getNextThreeQueueSymbols(
                                    tokenQueue)
                    ));
                }
            } else {
                GrammarNonterminal leftSideSymbol = (GrammarNonterminal) currentSymbol;

                GrammarProduction production =
                        ProductionRuleCandidateSelector.determineCandidate(leftSideSymbol, tokenQueue)
                                                       .orElseThrow(
                                                               () -> new SyntaxException(
                                                                       String.format(
                                                                               "Expected: %s; Got %s",
                                                                               leftSideSymbol.getFormatted(),
                                                                               tokenQueue
                                                                       )));

                stack.pushSeries(production.getSententialForm());
                appliedProductionRules.add(production.getProductionRuleIdentifier());
            }
        }
        // assert that all symbols were used
        if (!stack.isEmpty()) throw new SyntaxException("Missing symbol: %s".formatted(stack.pop()));
        if (!tokenQueue.isEmpty())
            throw new SyntaxException("Got more than expected: %s".formatted(CollectionUtility.getNextThreeQueueSymbols(
                    tokenQueue)));

        return appliedProductionRules;
    }

    private ConcreteSyntaxTree generateConcreteSyntaxTree(Grammar grammar, List<Token> tokens, List<Integer> appliedRules) {
        Queue<Token> tokenQueue = new LinkedList<>(tokens);
        Queue<Integer> rules = new LinkedList<>(appliedRules);

        final GrammarNonterminal rootNonterminal = grammar.getStart();

        ConcreteSyntaxTreeInternalNode root = createInternalNodeAndFill(grammar, rootNonterminal, rules, tokenQueue, 0);

        return new ConcreteSyntaxTree(root);
    }

    private ConcreteSyntaxTreeInternalNode createInternalNodeAndFill(
            Grammar grammar,
            GrammarNonterminal nonterminal,
            Queue<Integer> appliedRules,
            Queue<Token> tokenQueue,
            int depth
    ) {
        ConcreteSyntaxTreeInternalNode node = new ConcreteSyntaxTreeInternalNode(nonterminal, depth);

        GrammarProduction rule = grammar.getRuleQualifiedById(appliedRules.remove());

        for (GrammarSymbol symbol : rule.getSententialForm()) {
            if (symbol.isTerminal()) {
                node.addChild(new ConcreteSyntaxTreeLeafNode(tokenQueue.remove(), depth + 1));
            } else {
                node.addChild(createInternalNodeAndFill(
                        grammar,
                        (GrammarNonterminal) symbol,
                        appliedRules,
                        tokenQueue,
                        depth + 1
                ));
            }
        }

        node.setUnmodifiable();
        return node;
    }

}

