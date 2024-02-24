package de.nikschadowsky.compiler.lexer.tokens;

import de.nikschadowsky.compiler.util.RegexFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private String preprocessedCode;

    public Tokenizer(String preprocessedCode) {
        this.preprocessedCode = preprocessedCode;
    }

    /**
     *
     */
    public List<Token> run() {
        List<Token> tokens = new LinkedList<>();

        String startOfStringRegex = "^";

        Pattern keywordPattern = Pattern.compile(startOfStringRegex + RegexFactory.KEYWORD_REGEX);
        Pattern operatorPattern = Pattern.compile(startOfStringRegex + RegexFactory.OPERATOR_REGEX);
        Pattern separatorPattern = Pattern.compile(startOfStringRegex + RegexFactory.SEPARATOR_REGEX);
        Pattern stringPattern = Pattern.compile(startOfStringRegex + RegexFactory.STRING_PRIMITIVE_REGEX);
        Pattern numberPattern = Pattern.compile(startOfStringRegex + RegexFactory.NUMBER_PRIMITIVE_REGEX);
        Pattern booleanPattern = Pattern.compile(startOfStringRegex + RegexFactory.BOOLEAN_PRIMITIVE_REGEX);
        Pattern identifierPattern = Pattern.compile(startOfStringRegex + RegexFactory.IDENTIFIER_REGEX);


        int lastCheckedIndex = 0;

        while (!preprocessedCode.isBlank()) {
            Matcher m;
            TokenType type;

            String stripped = preprocessedCode.stripLeading();

            StringBuilder builder = new StringBuilder(stripped);

            if ((m = stringPattern.matcher(stripped)).find()) {
                type = TokenType.STRING;
            } else if ((m = booleanPattern.matcher(stripped)).find()) {
                type = TokenType.BOOLEAN;
            } else if ((m = numberPattern.matcher(stripped)).find()) {
                type = TokenType.NUMBER;
            } else if ((m = keywordPattern.matcher(stripped)).find()) {
                type = TokenType.KEYWORD;
            } else if ((m = operatorPattern.matcher(stripped)).find()) {
                type = TokenType.OPERATOR;
            } else if ((m = separatorPattern.matcher(stripped)).find()) {
                type = TokenType.SEPARATOR;
            } else if ((m = identifierPattern.matcher(stripped)).find()) {
                type = TokenType.IDENTIFIER;
            } else {
                throw new UnrecognizedTokenException(lastCheckedIndex);
            }
            tokens.add(new Token(type, m.group()));
            builder.delete(0, m.end());
            lastCheckedIndex += m.end();

            preprocessedCode = builder.toString();
        }

        return tokens;

    }

}
