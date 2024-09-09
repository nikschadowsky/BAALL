package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.lexer.tokenizer.TokenType;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import org.assertj.core.api.Assertions;

/**
 * @since 09.09.2024
 */
public class TokenQueueAssertion extends BaseAssertion<TokenQueueAssertion, TokenQueue> {
    protected TokenQueueAssertion(TokenQueue actual) {
        super(actual, TokenQueueAssertion.class);
    }

    public TokenQueueAssertion isAtEnd() {
        return truthinessAssert(
                (queue) -> "Tokenqueue still contains unconsumed elements; # remaining elements: " + (queue.getSize() - queue.getPointer()),
                TokenQueue::hasReachedEndOfFile
        );
    }

    public TokenQueueAssertion hasNextTokenMatch(String value, TokenType tokenType) {
        baseAssert("token value", queue -> getSafeNextToken(queue).value(), value);
        return baseAssert("token type", queue -> getSafeNextToken(queue).type(), tokenType);
    }

    public TokenQueueAssertion hasNextTokenValueMatch(String value) {
        return baseAssert("token value", queue -> getSafeNextToken(queue).value(), value);
    }

    private Token getSafeNextToken(TokenQueue queue) {
        Token nextToken = queue.peek();
        Assertions.assertThat(nextToken).isNotNull();
        return nextToken;
    }
}
