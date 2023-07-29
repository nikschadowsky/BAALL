package main.de.nikschadowsky.baall.compiler.parser;

import main.de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import org.w3c.dom.Node;

import java.util.List;

public class Parser {

    private List<Token> tokens;

    private Node rootNodeAST;

    public Parser(List<Token> tokens){
        this.tokens = tokens;

    }

    private void parse(){

    }

}

