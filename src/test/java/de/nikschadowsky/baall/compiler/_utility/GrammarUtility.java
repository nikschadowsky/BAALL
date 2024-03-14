package de.nikschadowsky.baall.compiler._utility;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.lexer.tokens.TokenType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * File created on 13.01.2024
 */
public class GrammarUtility {


    public static Queue<Token> getTokenQueue(String... tokenValues) {
        return Arrays.stream(tokenValues)
                     .map(GrammarUtility::getTokenWithTypeAny)
                     .collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<Token> getTokenList(String... tokenValues) {
        return new ArrayList<>(getTokenQueue(tokenValues));
    }

    public static Token getTokenWithTypeAny(String value) {
        return new Token(TokenType.ANY, value);
    }


}
