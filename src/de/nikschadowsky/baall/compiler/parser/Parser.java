package de.nikschadowsky.baall.compiler.parser;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;

import java.util.List;

public class Parser {

    private List<Token> tokens;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

}
