package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.syntax.analysis.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * File created on 14.04.2024
 */
public class TokenQueue {

    private final List<Token> queue;

    private int pointer;

    private TokenQueueBranch branch;

    private boolean branchValid;

    public TokenQueue(List<Token> queue) {
        this(queue, 0);
    }

    private TokenQueue(List<Token> queue, int initialPosition) {
        this.queue = new ArrayList<>(queue);
        this.pointer = initialPosition;
    }

    public Token poll() {
        checkEOF();
        return queue.get(pointer++);
    }

    public Token peek() {
        checkEOF();
        return queue.get(pointer);
    }

    public List<Token> getAllTokens() {
        return Collections.unmodifiableList(queue);
    }

    /**
     * Checks if end of file was reached and if so throws an {@link EndOfFileException}.
     */
    private void checkEOF() {
        if (queue.isEmpty()) {
            throw new EndOfFileException();
        }
    }

    /**
     * Skips to and consumes the next token in queue that matches the passed symbol
     *
     * @param skipped
     */
    public void skipOver(Parser.TerminalSymbol skipped) {
        while (!skipped.symbolMatches(peek())) {
            poll();
        }
        // skip over matching token
        poll();
    }

    public TokenQueueBranch branchOff() {
        branchValid = true;
        branch = new TokenQueueBranch(queue, pointer);
        return branch;
    }

    /**
     * Points to the next element that is returned by calling {@link #peek()}.
     *
     * @return the index of the next element returned by {@link #peek()}
     */
    public int getPointer() {
        return pointer;
    }

    public boolean hasReachedEndOfFile() {
        return pointer == queue.size() - 1;
    }

    public void mergeBranch() {
        if (!branchValid) {
            System.err.println("Cannot advance queue because the branch is not valid!");
            return;
        }
        pointer = branch.getPointer();
        branch = null;
        branchValid = false;
    }

    public static class TokenQueueBranch extends TokenQueue {

        private TokenQueueBranch(List<Token> queue, int initialPosition) {
            super(queue, initialPosition);
        }
    }

}
