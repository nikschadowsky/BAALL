package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Parser {

    private List<Token> tokens;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;

    }

    private void parse(List<Token> tokens, Grammar grammar) {

        List<Integer> derivationIndices = new LinkedList<>();

        PriorityQueue<Token> tokenQueue = new PriorityQueue<>(tokens);

        ParserStack stack = new ParserStack(grammar.getStart());

        int tokenIndex;

        while (!stack.empty() && !tokenQueue.isEmpty()) {

            if (stack.peek().isTerminal()) {

                if (!stack.pop().symbolMatches(tokenQueue.poll())) {
                    throw new SyntaxException();
                }

            } else {

            }


        }

    }

}

