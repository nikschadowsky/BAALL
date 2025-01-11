package de.nikschadowsky.baall.compiler.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RegexFactoryTest {

    @Test
    void generateRegexesTest() {
        SyntaxSet.OPERATORS.stream()
                           .map(LanguageElement::representation)
                           .map(RegexFactory::regexifySymbols)
                           .forEach(op -> {
                               if (!RegexFactory.OPERATOR_REGEX.contains("(" + op + ")")) {
                                   fail("Operator not in Regex: " + op + "\n" + RegexFactory.OPERATOR_REGEX);
                               }
                           });

        SyntaxSet.KEYWORDS.stream()
                          .map(LanguageElement::representation)
                          .forEach(keyword -> {
                              if (!RegexFactory.KEYWORD_REGEX.contains("(" + keyword + ")")) {
                                  fail("Keyword not in Regex: " + keyword + "\n" + RegexFactory.KEYWORD_REGEX);
                              }
                          });

        SyntaxSet.SEPARATORS.stream()
                            .map(LanguageElement::representation)
                            .map(RegexFactory::regexifySymbols)
                            .forEach(separator -> {
                                if (!(RegexFactory.SEPARATOR_REGEX.contains("(" + separator + ")"))) {
                                    fail("Separator not in Regex: " + separator + "\n" + RegexFactory.SEPARATOR_REGEX);
                                }
                            });
    }

    @Test
    void regexifyOperator() {
        assertEquals("\\<\\>", RegexFactory.regexifySymbols("<>"));
        assertEquals("\\<", RegexFactory.regexifySymbols("<"));
        assertEquals("\\+", RegexFactory.regexifySymbols("+"));
        assertEquals("\\|\\=", RegexFactory.regexifySymbols("|="));
        assertEquals("abc\\.\\|\\=def", RegexFactory.regexifySymbols("abc.|=def"));
    }

}