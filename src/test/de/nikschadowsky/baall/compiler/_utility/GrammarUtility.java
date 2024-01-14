package de.nikschadowsky.baall.compiler._utility;

import de.nikschadowsky.baall.compiler.grammar.Grammar;
import de.nikschadowsky.baall.compiler.grammar.GrammarNonterminal;
import de.nikschadowsky.baall.compiler.grammar.GrammarProduction;
import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.lexer.tokens.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * File created on 13.01.2024
 */
public class GrammarUtility {

    public static GrammarNonterminal getNonterminal(Grammar grammar, String id) {
        return grammar.getAllNonterminals()
                .stream()
                .filter(g -> g.getIdentifier().equals(id))
                .findFirst()
                .orElseThrow();
    }

    public static Queue<Token> getTokenQueue(String... tokenValues) {
        return Arrays.stream(tokenValues)
                .map(GrammarUtility::getTokenWithTypeAny)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static Token getTokenWithTypeAny(String value) {
        return new Token(TokenType.ANY, value);
    }

    public static GrammarProduction getProductionRule(@NotNull GrammarNonterminal lss, GrammarSymbol... prod) {
        return lss.getProductionRules()
                .stream()
                .filter(gp -> gp.equals(new GrammarProduction(-1, lss, prod)))
                .findAny()
                .orElse(null);
    }
}
