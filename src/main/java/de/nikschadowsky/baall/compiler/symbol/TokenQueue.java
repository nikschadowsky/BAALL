package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.util.LanguageElement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 14.04.2024
 */
public class TokenQueue {

    private final TokenQueueId id;

    private final List<Token> queue;
    private int pointer;

    private final List<TokenQueueBranch> branches = new ArrayList<>();

    private boolean branchValid;

    public TokenQueue(TokenQueueId id, List<Token> queue) {
        this(id, queue, 0);
    }

    private TokenQueue(TokenQueueId id, List<Token> queue, int initialPosition) {
        this.id = id;
        this.queue = new ArrayList<>(queue);
        this.pointer = initialPosition;
    }

    public @Nullable Token poll() {
        if (hasReachedEndOfFile()) {
            return null;
        }
        return queue.get(pointer++);
    }

    public @Nullable Token peek() {
        if (hasReachedEndOfFile()) {
            return null;
        }
        return queue.get(pointer);
    }

    public @Nullable Token replace(Token newToken) {
        if (hasReachedEndOfFile()) {
            return null;
        }
        return queue.set(pointer, newToken);
    }

    public List<Token> getAllTokens() {
        return Collections.unmodifiableList(queue);
    }

    /**
     * Checks if end of file was reached and if so throws an {@link EndOfFileException}.
     */
    private void checkEOF() {
        if (hasReachedEndOfFile()) {
            throw new EndOfFileException();
        }
    }

    public void skipTo(LanguageElement skippedTo) {
        while (!skippedTo.matches(peek()) && !hasReachedEndOfFile()) {
            poll();
        }
    }

    /**
     * Skips to and consumes the next token in queue that matches the passed symbol
     *
     * @param skipped the first queue element that matches LanguageElement via {@link LanguageElement#matches(Token)}
     *                gets skipped.
     */
    public void skipOver(LanguageElement skipped) {
        skipTo(skipped);
        // skip over matching token
        poll();
    }

    public TokenQueueBranch branchOff() {
        TokenQueueBranch branch =
                new TokenQueueBranch(TokenQueueId.of(id.getId() + "$" + branches.size()), queue, pointer);
        branches.add(branch);
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

    public int getSize() {
        return queue.size();
    }

    public TokenQueueId getId() {
        return id;
    }

    public boolean hasReachedEndOfFile() {
        return pointer >= queue.size();
    }

    public void mergeBranch(TokenQueueId branchId) {
        TokenQueueBranch branch = branches.stream()
                                          .filter(b -> b.getId().equals(branchId))
                                          .filter(TokenQueueBranch::isValid)
                                          .findAny()
                                          .orElseThrow(() -> new IllegalArgumentException(
                                                  "No valid branch with the id '%s' is registered on this queue!".formatted(
                                                          branchId)));
        pointer = branch.getPointer();
        branch.invalidate();
    }

    public static class TokenQueueBranch extends TokenQueue {

        private boolean isValid = true;

        private TokenQueueBranch(TokenQueueId id, List<Token> queue, int initialPosition) {
            super(id, queue, initialPosition);
        }

        public boolean isValid() {
            return isValid;
        }

        public void invalidate() {
            isValid = false;
        }
    }
}
