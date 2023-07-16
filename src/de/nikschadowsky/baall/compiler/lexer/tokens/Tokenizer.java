package de.nikschadowsky.baall.compiler.lexer.tokens;

import java.util.LinkedList;
import java.util.List;

public class Tokenizer {

    private static final String BOOLEAN_PRIMITIVE_REGEX = "(true|false)(?=[^a-zA-Z0-9]|$)";
    private static final String STRING_PRIMITIVE_REGEX = "\".*?(?<!\\)\"";

    public Tokenizer(){

    }

    public void run(String preprocessedCode){
        List<Token> tokens = new LinkedList<>();

        int i = 0;
        while(i < preprocessedCode.length()){


            i++;
        }

    }

}
