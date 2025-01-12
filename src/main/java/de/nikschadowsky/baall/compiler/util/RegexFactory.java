package de.nikschadowsky.baall.compiler.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RegexFactory {

    public static final String SINGLE_LINE_COMMENT_REGEX = "(//.*\\R)";

    public static final String BLOCK_COMMENT_REGEX = "(/\\*(.|\\s)*?\\*/)";

    public static final String WHITESPACE_REGEX = "\\s+";

    public static final String NEWLINE_REGEX = "\\v+";

    public static final String END_OF_WORD_REGEX = "(?=\\W|$)";

    public static final String BOOLEAN_PRIMITIVE_REGEX = "(true|false)" + END_OF_WORD_REGEX;

    public static final String STRING_PRIMITIVE_REGEX = "\".*?(?<!\\\\)\"";

    public static final String NUMBER_PRIMITIVE_REGEX =
            "(0((b[01]+)|(x[0-9A-Fa-f]+))|(\\d*\\.)?\\d+)" + END_OF_WORD_REGEX;

    public static final String OPERATOR_REGEX = generateOperatorRegex();

    public static final String KEYWORD_REGEX = generateKeywordRegex();

    public static final String SEPARATOR_REGEX = generateSeparatorRegex();

    public static final String IDENTIFIER_REGEX = "_*[a-zA-Z]\\w*|_";

    public static final String GRAMMAR_LINE_TOKENIZING_REGEX = "(?<!\\\\)@|(?<!\\\\)->";


    /**
     * Generate a Regular Expression from the defined Keywords in {@link SyntaxSet}.
     *
     * @return Regular Expression String for Keywords
     */
    private static String generateKeywordRegex() {
        return SyntaxSet.KEYWORDS.stream()
                                 .map(LanguageElement::representation)
                                 .collect(Collectors.joining(")|(", "(", ")"))
                                 .replaceAll("\\|\\)", ")") + END_OF_WORD_REGEX;
    }

    /**
     * Generate a Regular Expression from the defined Operators in {@link SyntaxSet}. Ordered by length descending, so
     * remove need to peek ahead.
     *
     * @return Regular Expression String for Operators
     */

    private static String generateOperatorRegex() {


        return SyntaxSet.OPERATORS.stream()
                                  .sorted(Comparator.<LanguageElement>comparingInt(le -> le.representation().length())
                                                    .reversed())
                                  .map(LanguageElement::representation)
                                  .map(RegexFactory::regexifySymbols)
                                  .collect(Collectors.joining(")|(", "(", ")"));
    }

    /**
     * Generate a Regular Expression from the defined Separators in {@link SyntaxSet}. Ordered by length descending, so
     * remove need to peek ahead.
     *
     * @return Regular Expression String for Separators
     */
    private static String generateSeparatorRegex() {
        return SyntaxSet.SEPARATORS.stream()
                                   .sorted(Comparator.<LanguageElement>comparingInt(le -> le.representation().length())
                                                     .reversed())
                                   .map(LanguageElement::representation)
                                   .map(RegexFactory::regexifySymbols)
                                   .map(s -> "(" + s + ")")
                                   .collect(Collectors.joining("|"));
    }

    /**
     * Escaped every non-alphanumeric character within the passed string. Alphanumeric characters remain as is.
     *
     * @param op nonnull String of symbols
     * @return passed string properly escaped for regex
     */

    public static String regexifySymbols(@NotNull String op) {
        return Arrays.stream(op.split("")).map(s -> {
            if (Character.isLetterOrDigit(s.charAt(0))) {
                // do not escape alphanumeric characters
                return s;
            }
            // escape everything else
            return "\\" + s;
        }).reduce("", String::concat);
    }
}
