package de.nikschadowsky.compiler.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegexFactoryTest {

    @Test
    void generateRegexesTest() {

        String opRegex = RegexFactory.OPERATOR_REGEX;
        System.out.println(opRegex);


        for (String op : SyntaxSet.OPERATORS) {

            String regexifiedOp = RegexFactory.regexifySymbols(op);

            if (!(opRegex.contains("|(" + regexifiedOp + (")")) || opRegex.contains("(" + regexifiedOp + (")|")))) {
                System.err.println(opRegex);
                fail("Operator not in Regex");
            }
        }


        String keywordRegex = RegexFactory.KEYWORD_REGEX;
        System.out.println(keywordRegex);

        for (String keyword : SyntaxSet.KEYWORDS) {
            if (!(keywordRegex.contains(keyword + "|") || (keywordRegex.contains("|" + keyword)))) {
                System.err.println(keywordRegex);
                fail("Keyword not in Regex");
            }

        }

        String separatorRegex = RegexFactory.SEPARATOR_REGEX;
        System.out.println(separatorRegex);

        for (String separator : SyntaxSet.SEPARATORS) {

            String regexifiedSeparator = RegexFactory.regexifySymbols(separator);

            if (!(separatorRegex.contains("|(" + regexifiedSeparator + (")")) || separatorRegex.contains("(" + regexifiedSeparator + (")|")))) {
                System.err.println(separatorRegex);
                fail("Separator not in Regex");
            }
        }
        assertTrue(true);

    }


    @Test
    void regexifyOperator() {

        assertEquals("\\<\\>", RegexFactory.regexifySymbols("<>"));
        assertEquals("\\<", RegexFactory.regexifySymbols("<"));
        assertEquals("\\+", RegexFactory.regexifySymbols("+"));
        assertEquals("\\|\\=", RegexFactory.regexifySymbols("|="));
    }

}