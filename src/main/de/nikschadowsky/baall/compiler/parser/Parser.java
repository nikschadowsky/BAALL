package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.lexer.tokens.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Parser {

    private List<Token> tokens;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;

    }

    private void parse(List<Token> tokens, Grammar grammar) {

        List<Integer> appliedProductionRules = new LinkedList<>();

        Queue<Token> tokenQueue = new LinkedList<>(tokens);

        ParserStack stack = new ParserStack(grammar.getStart());

        int tokenIndex;

        while (!stack.empty() && !tokenQueue.isEmpty()) {

            GrammarSymbol currentSymbol = stack.pop();

            if (currentSymbol.isTerminal()) {

                Token currentToken = tokenQueue.remove();

                if (!currentSymbol.symbolMatches(currentToken)) {
                    throw new SyntaxException(String.format("Expected: %s; Got %s",currentSymbol,currentToken.getValue()));
                }

            } else {
                GrammarNonterminal leftSideSymbol = (GrammarNonterminal) currentSymbol;

                new ProductionRuleCandidateSelector().determineCandidate(tokenQueue.iterator());
            }


        }

    }

}

