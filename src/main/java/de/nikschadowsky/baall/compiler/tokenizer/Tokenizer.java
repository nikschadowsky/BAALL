package de.nikschadowsky.baall.compiler.tokenizer;

import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler.Step;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.util.RegexFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer extends Step<String, List<Token>> {

    private String preprocessedCode;

    public Tokenizer(String preprocessedCode) {
        super(null);
        this.preprocessedCode = preprocessedCode;
    }

    @Override
    public List<Token> executeStep(String s, CompileInformation compileInformation) {
        return List.of();
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
            // fixme line number and index information!
            tokens.add(new Token(type, m.group(), 0 ,0));
            builder.delete(0, m.end());
            lastCheckedIndex += m.end();

            preprocessedCode = builder.toString();
        }

        return tokens;

    }

}
