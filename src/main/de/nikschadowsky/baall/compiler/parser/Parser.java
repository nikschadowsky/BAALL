package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.abstractsyntaxtree.SyntaxTree;
import de.nikschadowsky.baall.compiler.abstractsyntaxtree.node.SyntaxTreeInternalNode;
import de.nikschadowsky.baall.compiler.abstractsyntaxtree.node.SyntaxTreeLeafNode;
import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * File created on 21.07.2023
 */
public class Parser {

    public SyntaxTree parse(Grammar grammar, List<Token> tokens) {

        return generateSyntaxTree(grammar, tokens, parse1(grammar, tokens));
    }

    private List<Integer> parse1(Grammar grammar, List<Token> tokens) {

        List<Integer> appliedProductionRules = new LinkedList<>();
        Queue<Token> tokenQueue = new LinkedList<>(tokens);
        ParserStack stack = new ParserStack(grammar.getStart());

        while (!stack.empty() && !tokenQueue.isEmpty()) {

            GrammarSymbol currentSymbol = stack.pop();

            if (currentSymbol.isTerminal()) {
                Token currentToken = tokenQueue.remove();

                if (!currentSymbol.symbolMatches(currentToken)) {
                    throw new SyntaxException(String.format("Expected: %s; Got %s", currentSymbol, currentToken.getValue()));
                }
            } else {
                GrammarNonterminal leftSideSymbol = (GrammarNonterminal) currentSymbol;

                GrammarProduction production =
                        ProductionRuleCandidateSelector.determineCandidate(leftSideSymbol, tokenQueue).orElseThrow(() ->
                                new SyntaxException(
                                        String.format(
                                                "Expected: %s; Got %s",
                                                leftSideSymbol,
                                                "idk haven't determined yet how to handle these syntax exceptions")));

                stack.pushSeries(production.getSententialForm());
                appliedProductionRules.add(production.getProductionRuleIdentifier());
            }
        }
        // assert that all symbols were used
        if (!stack.isEmpty()) throw new SyntaxException("Missing symbol: %s".formatted(stack.pop()));
        if (!tokenQueue.isEmpty()) throw new SyntaxException("Got more than expected: %s".formatted(tokenQueue.poll()));

        return appliedProductionRules;
    }

    private SyntaxTree generateSyntaxTree(Grammar grammar, List<Token> tokens, List<Integer> appliedRules) {
        Queue<Token> tokenQueue = new LinkedList<>(tokens);
        Queue<Integer> rules = new LinkedList<>(appliedRules);

        final GrammarNonterminal rootNonterminal = grammar.getStart();

        SyntaxTreeInternalNode root = createInternalNodeAndFill(grammar, rootNonterminal, rules, tokenQueue, 0);

        return new SyntaxTree(root);
    }

    private SyntaxTreeInternalNode createInternalNodeAndFill(
            Grammar grammar,
            GrammarNonterminal nonterminal,
            Queue<Integer> appliedRules,
            Queue<Token> tokenQueue,
            int depth
    ) {
        SyntaxTreeInternalNode node = new SyntaxTreeInternalNode(nonterminal, depth);

        GrammarProduction rule = grammar.getRuleQualifiedById(appliedRules.remove());

        for (GrammarSymbol symbol : rule.getSententialForm()) {
            if (symbol.isTerminal()) {
                node.addChild(new SyntaxTreeLeafNode(tokenQueue.remove(), depth + 1));
            } else {
                node.addChild(createInternalNodeAndFill(grammar, (GrammarNonterminal) symbol, appliedRules, tokenQueue, depth + 1));
            }
        }

        node.setUnmodifiable();
        return node;
    }

}

