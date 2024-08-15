package de.nikschadowsky.baall.compiler.util;

import de.nikschadowsky.baall.compiler.lexer.tokenizer.TokenType;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SyntaxSet {
    public static final @Unmodifiable Set<LanguageElement> PRIMITIVES = getPrimitives();
    public static final @Unmodifiable Set<LanguageElement> KEYWORDS = getKeywords();
    public static final @Unmodifiable Set<LanguageElement> SIMPLE_TYPES = getSimpleTypes();
    public static final @Unmodifiable Set<LanguageElement> OPERATORS = getOperators();
    public static final @Unmodifiable Set<LanguageElement> SEPARATORS = getSeparators();

    public static final @Unmodifiable Map<String, LanguageElement> LANGUAGE_ELEMENTS = getAllLanguageElements();

    private static Set<LanguageElement> getPrimitives() {
        Set<LanguageElement> primitives = new LinkedHashSet<>();

        primitives.add(new LanguageElement("number_primitive", TokenType.STRING, "_NUMBER"));
        primitives.add(new LanguageElement("string_primitive", TokenType.NUMBER, "_NUMBER"));
        // primitives.add(new LanguageElement("char_primitive", TokenType.STRING, "_CHARACTER"));
        primitives.add(new LanguageElement("boolean_primitive", TokenType.BOOLEAN, "_BOOLEAN"));

        System.out.println("Primitives: " + primitives + "\n Number of elements: " + primitives.size());
        return Collections.unmodifiableSet(primitives);
    }

    private static Set<LanguageElement> getKeywords() {
        Set<LanguageElement> keywords = new LinkedHashSet<>();

        keywords.add(new LanguageElement("use", TokenType.KEYWORD, "use"));

        keywords.add(new LanguageElement("none", TokenType.KEYWORD, "none"));

        keywords.add(new LanguageElement("while", TokenType.KEYWORD, "while"));
        keywords.add(new LanguageElement("for", TokenType.KEYWORD, "for"));

        keywords.add(new LanguageElement("return", TokenType.KEYWORD, "return"));
        keywords.add(new LanguageElement("continue", TokenType.KEYWORD, "continue"));
        keywords.add(new LanguageElement("break", TokenType.KEYWORD, "break"));

        keywords.add(new LanguageElement("raise", TokenType.KEYWORD, "raise"));

        keywords.add(new LanguageElement("try", TokenType.KEYWORD, "try"));
        keywords.add(new LanguageElement("intercept", TokenType.KEYWORD, "intercept"));
        keywords.add(new LanguageElement("ensure", TokenType.KEYWORD, "ensure"));

        keywords.add(new LanguageElement("export", TokenType.KEYWORD, "export"));

        System.out.println("Keywords: " + keywords + "\n Number of elements: " + keywords.size());
        return Collections.unmodifiableSet(keywords);
    }

    private static Set<LanguageElement> getSimpleTypes() {
        Set<LanguageElement> simpleTypes = new LinkedHashSet<>();
        // TODO Undecided, just reserve it for now
        simpleTypes.add(new LanguageElement("char", TokenType.KEYWORD, "char"));
        simpleTypes.add(new LanguageElement("number", TokenType.KEYWORD, "number"));
        simpleTypes.add(new LanguageElement("string", TokenType.KEYWORD, "string"));
        simpleTypes.add(new LanguageElement("boolean", TokenType.KEYWORD, "boolean"));
        simpleTypes.add(new LanguageElement("struct", TokenType.KEYWORD, "struct"));
        simpleTypes.add(new LanguageElement("function", TokenType.KEYWORD, "function"));
        simpleTypes.add(new LanguageElement("exception", TokenType.KEYWORD, "exception"));

        System.out.println("Simple types: " + simpleTypes + "\n Number of elements: " + simpleTypes.size());
        return Collections.unmodifiableSet(simpleTypes);
    }

    private static Set<LanguageElement> getOperators() {
        Set<LanguageElement> operators = new LinkedHashSet<>();
        operators.add(new LanguageElement("=", TokenType.OPERATOR, "="));
        operators.add(new LanguageElement(":=", TokenType.OPERATOR, ":="));

        operators.add(new LanguageElement("+", TokenType.OPERATOR, "+"));
        operators.add(new LanguageElement("-", TokenType.OPERATOR, "-"));
        operators.add(new LanguageElement("*", TokenType.OPERATOR, "*"));
        operators.add(new LanguageElement("/", TokenType.OPERATOR, "/"));
        operators.add(new LanguageElement("%", TokenType.OPERATOR, "%"));

        operators.add(new LanguageElement("+=", TokenType.OPERATOR, "+="));
        operators.add(new LanguageElement("-=", TokenType.OPERATOR, "-="));
        operators.add(new LanguageElement("*=", TokenType.OPERATOR, "*="));
        operators.add(new LanguageElement("/=", TokenType.OPERATOR, "/="));
        operators.add(new LanguageElement("%=", TokenType.OPERATOR, "%="));

        operators.add(new LanguageElement("++", TokenType.OPERATOR, "++"));
        operators.add(new LanguageElement("--", TokenType.OPERATOR, "--"));

        operators.add(new LanguageElement("&", TokenType.OPERATOR, "&"));
        operators.add(new LanguageElement("|", TokenType.OPERATOR, "|"));
        operators.add(new LanguageElement("^", TokenType.OPERATOR, "^"));
        operators.add(new LanguageElement("!", TokenType.OPERATOR, "!"));
        operators.add(new LanguageElement(">>", TokenType.OPERATOR, ">>"));
        operators.add(new LanguageElement("<<", TokenType.OPERATOR, "<<"));

        operators.add(new LanguageElement("&=", TokenType.OPERATOR, "&="));
        operators.add(new LanguageElement("|=", TokenType.OPERATOR, "|="));
        operators.add(new LanguageElement("^=", TokenType.OPERATOR, "^="));

        operators.add(new LanguageElement("<", TokenType.OPERATOR, "<"));
        operators.add(new LanguageElement(">", TokenType.OPERATOR, ">"));
        operators.add(new LanguageElement("<=", TokenType.OPERATOR, "<="));
        operators.add(new LanguageElement(">=", TokenType.OPERATOR, ">="));
        operators.add(new LanguageElement("<>", TokenType.OPERATOR, "<>"));
        operators.add(new LanguageElement("==", TokenType.OPERATOR, "=="));
        operators.add(new LanguageElement("&&", TokenType.OPERATOR, "&&"));
        operators.add(new LanguageElement("||", TokenType.OPERATOR, "||"));

        operators.add(new LanguageElement("?", TokenType.OPERATOR, "?"));

        System.out.println("Operators: " + operators + "\n Number of elements: " + operators.size());
        return Collections.unmodifiableSet(operators);
    }

    private static Set<LanguageElement> getSeparators() {
        Set<LanguageElement> separators = new LinkedHashSet<>();

        separators.add(new LanguageElement("{", TokenType.SEPARATOR, "{"));
        separators.add(new LanguageElement("}", TokenType.SEPARATOR, "}"));
        separators.add(new LanguageElement("(", TokenType.SEPARATOR, "("));
        separators.add(new LanguageElement(")", TokenType.SEPARATOR, ")"));
        separators.add(new LanguageElement(";", TokenType.SEPARATOR, ";"));
        separators.add(new LanguageElement(",", TokenType.SEPARATOR, ","));
        separators.add(new LanguageElement("::", TokenType.SEPARATOR, "::"));
        separators.add(new LanguageElement(":", TokenType.SEPARATOR, ":"));
        separators.add(new LanguageElement("@", TokenType.SEPARATOR, "@"));
        separators.add(new LanguageElement("[", TokenType.SEPARATOR, "["));
        separators.add(new LanguageElement("]", TokenType.SEPARATOR, "]"));
        separators.add(new LanguageElement(".", TokenType.SEPARATOR, "."));

        System.out.println("Separators: " + separators + "\n Number of elements: " + separators.size());
        return Collections.unmodifiableSet(separators);
    }

    private static Map<String, LanguageElement> getAllLanguageElements() {
        Set<LanguageElement> languageElements = new LinkedHashSet<>();
        languageElements.addAll(PRIMITIVES);
        languageElements.addAll(KEYWORDS);
        languageElements.addAll(OPERATORS);
        languageElements.addAll(SEPARATORS);
        // todo find a place to put this
        languageElements.add(new LanguageElement("identifier_primitive", TokenType.IDENTIFIER, "_IDENTIFIER"));

        return Collections.unmodifiableMap(languageElements.stream()
                                                           .collect(Collectors.toMap(
                                                                   LanguageElement::representation,
                                                                   e -> e
                                                           )));
    }

}
