package de.nikschadowsky.baall.compiler.util;

import org.junit.platform.commons.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SyntaxSet {
    public static final Set<String> KEYWORDS = new HashSet<>();
    public static final Set<String> OPERATORS = new HashSet<>();
    public static final Set<String> SEPARATORS = new HashSet<>();


    static {
        // Keywords
        addKeywords();

        // Operators
        addOperators();

        // Separators
        addSeparators();
    }

    private static void addKeywords() {
        KEYWORDS.add("use");
        KEYWORDS.add("num");
        KEYWORDS.add("string");
        KEYWORDS.add("char"); // TODO Undecided, just reserve it for now
        KEYWORDS.add("struct");
        KEYWORDS.add("while");
        KEYWORDS.add("for");
        KEYWORDS.add("export");
        KEYWORDS.add("return");

        System.out.println("Keywords: "  + KEYWORDS.size() + ", " + KEYWORDS);
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

        System.out.println("Operators: " + OPERATORS.size() + ", " + OPERATORS);
        }

    private static void addSeparators() {

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

        System.out.println("Separators: " + SEPARATORS.size() + ", " + SEPARATORS);
    }

}
