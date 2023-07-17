package de.nikschadowsky.baall.compiler.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;

public class RegexFactory {

    public static final String END_OF_TOKEN_REGEX = "(?=\\W|$)";
    public static final String BOOLEAN_PRIMITIVE_REGEX = "(true|false)" + END_OF_TOKEN_REGEX;
    public static final String STRING_PRIMITIVE_REGEX = "\".*?(?<!\\)\"";
    public static final String NUMBER_PRIMITIVE_REGEX = "(0((b[01]+)|(x[0-9A-Fa-f]+))|(\\d*\\.)?\\d+)" + END_OF_TOKEN_REGEX;
    public static final String SINGLE_LINE_COMMENT_REGEX = "(//.*\\R)";
    public static final String BLOCK_COMMENT_REGEX = "(/\\*(.|\\s)*?\\*/)";
    public static final String WHITESPACE_REGEX = "\\s+";

    /**
     * Generate a Regular Expression from the defined Operators in {@link SyntaxSet}.
     * Ordered by length descending, so remove need to peek ahead.
     *
     * @return Regular Expression String for Operators
     */

    public static String generateOperatorRegex() {

        String removeLastOrRegex = "(?<=\\))\\|\\)"; // ...)|) -> ...))

        return (SyntaxSet.OPERATORS.stream().sorted(Comparator.comparingInt(String::length).reversed()) // longest OP first, so there is no reason to look ahead
                .reduce("(", (akk, op) -> akk
                        + "(" +
                        regexifySymbols(op)
                        + ")|")
                + ")").replaceAll(removeLastOrRegex, ")");
    }

    /**
     * Generate a Regular Expression from the defined Keywords in {@link SyntaxSet}.
     *
     * @return Regular Expression String for Keywords
     */
    public static String generateKeywordRegex() {
        return (SyntaxSet.KEYWORDS.stream().reduce("(", (akk, op) -> akk + op + "|") + ")").replaceAll("\\|\\)", ")");
    }

    /**
     * Generate a Regular Expression from the defined Separators in {@link SyntaxSet}.
     * Ordered by length descending, so remove need to peek ahead.
     *
     * @return Regular Expression String for Separators
     */
    public static String generateSeparatorRegex() {
        String removeLastOrRegex = "(?<=\\))\\|\\)"; // ...)|) -> ...))

        return (SyntaxSet.SEPARATORS.stream().sorted(Comparator.comparingInt(String::length).reversed()) // longest OP first, so there is no reason to look ahead
                .reduce("(", (akk, sep) -> akk
                        + "(" +
                        regexifySymbols(sep)
                        + ")|")
                + ")").replaceAll(removeLastOrRegex, ")");
    }

    /**
     * Since Operators consist of symbols, some may be interpreted by regular expressions as non literals.
     * To prevent this, every symbol gets escaped, to interpret it as a literal.
     *
     * @param op not empty and not null String of symbols
     * @return Operator as regex literal
     */

    public static String regexifySymbols(@NotNull String op) {
        return Arrays.stream(op.split("")).map(s -> "\\" + s).reduce("", String::concat);
    }
}
