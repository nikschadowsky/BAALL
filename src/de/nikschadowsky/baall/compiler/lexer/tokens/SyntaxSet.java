package de.nikschadowsky.baall.compiler.lexer.tokens;

import java.util.HashSet;
import java.util.Set;

public class SyntaxSet {
    public static final Set<String> KEYWORDS = new HashSet<>();
    public static final Set<String> OPERATORS= new HashSet<>();
    public static final Set<String> SEPARATORS= new HashSet<>();


    static {
        // Keywords
        addKeywords();

        // Operators
        addOperators();

    }private static void addKeywords(){
        KEYWORDS.add("use");
        KEYWORDS.add("num");
        KEYWORDS.add("string");
        KEYWORDS.add("char"); // TODO Undecided, just reserve it for now
        KEYWORDS.add("struct");
        KEYWORDS.add("while");
        KEYWORDS.add("for");
        KEYWORDS.add("export");
        KEYWORDS.add("return");

    }

    private static void addOperators() {
        OPERATORS.add("=");
        OPERATORS.add(":=");

        OPERATORS.add("+");
        OPERATORS.add("-");
        OPERATORS.add("*");
        OPERATORS.add("/");
        OPERATORS.add("%");

        OPERATORS.add("+=");
        OPERATORS.add("-=");
        OPERATORS.add("*=");
        OPERATORS.add("/=");
        OPERATORS.add("%=");

        OPERATORS.add("++");
        OPERATORS.add("--");

        OPERATORS.add("&");
        OPERATORS.add("|");
        OPERATORS.add("^");
        OPERATORS.add("!");
        OPERATORS.add(">>");
        OPERATORS.add("<<");

        OPERATORS.add("&=");
        OPERATORS.add("|=");
        OPERATORS.add("^=");

        OPERATORS.add("<");
        OPERATORS.add(">");
        OPERATORS.add("<=");
        OPERATORS.add(">=");
        OPERATORS.add("<>");
        OPERATORS.add("==");
        OPERATORS.add("&&");
        OPERATORS.add("||");

        OPERATORS.add("?");
    }

    private static void setSeparators(){

        SEPARATORS.add("{");
        SEPARATORS.add("}");
        SEPARATORS.add("(");
        SEPARATORS.add(")");
        SEPARATORS.add(";");
        SEPARATORS.add(",");
        SEPARATORS.add("::");
        SEPARATORS.add("@");
        SEPARATORS.add("[");
        SEPARATORS.add("]");
        SEPARATORS.add(".");
    }

}
