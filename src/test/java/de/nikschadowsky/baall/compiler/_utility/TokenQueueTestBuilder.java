package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.symbol.TokenQueueId;
import de.nikschadowsky.baall.compiler.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 13.08.2024
 */
public class TokenQueueTestBuilder {

    private final List<Token> tokens = new ArrayList<>();

    public TokenQueueTestBuilder() {
    }

    public TokenQueueTestBuilder any(String value) {
        tokens.add(new Token(TokenType.ANY, value, 1, tokens.size()));
        return this;
    }

    public TokenQueueTestBuilder string(String value) {
        tokens.add(new Token(TokenType.STRING, value, 1, tokens.size()));
        return this;
    }

    public TokenQueueTestBuilder number(String value) {
        tokens.add(new Token(TokenType.NUMBER, value, 1, tokens.size()));
        return this;
    }

    public TokenQueueTestBuilder bool(String value) {
        tokens.add(new Token(TokenType.BOOLEAN, value, 1, tokens.size()));
        return this;
    }

    public TokenQueueTestBuilder keyword(String value) {
        tokens.add(new Token(TokenType.KEYWORD, value, 1, tokens.size()));
        return this;
    }

    public TokenQueueTestBuilder identifier(String value) {
        tokens.add(new Token(TokenType.IDENTIFIER, value, 1, tokens.size()));
        return this;
    }

    public TokenQueueTestBuilder operator(String value) {
        tokens.add(new Token(TokenType.OPERATOR, value, 1, tokens.size()));
        return this;
    }

    public TokenQueueTestBuilder separator(String value) {
        tokens.add(new Token(TokenType.SEPARATOR, value, 1, tokens.size()));
        return this;
    }

    public TokenQueue build() {
        return new TokenQueue(TokenQueueId.of("root"), tokens);
    }

}
