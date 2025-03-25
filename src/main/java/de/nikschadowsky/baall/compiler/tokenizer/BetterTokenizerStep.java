package de.nikschadowsky.baall.compiler.tokenizer;


import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler.Step;
import de.nikschadowsky.baall.compiler.StepOptions;
import de.nikschadowsky.baall.compiler.lexer.LexerRow;
import de.nikschadowsky.baall.compiler.output.error.CompileException;
import de.nikschadowsky.baall.compiler.output.error.Diagnostic;
import de.nikschadowsky.baall.compiler.symbol.LineInformation;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TokenQueue;
import de.nikschadowsky.baall.compiler.symbol.TokenQueueId;
import de.nikschadowsky.baall.compiler.util.RegexFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 11.01.2025
 */
public class BetterTokenizerStep extends Step<List<LexerRow>, TokenQueue, Diagnostic> {

    private static final Map<Pattern, TokenType> PATTERN_MAP = new LinkedHashMap<>();

    static {
        PATTERN_MAP.put(Pattern.compile("^(" + RegexFactory.STRING_PRIMITIVE_REGEX + ")"), TokenType.STRING);
        PATTERN_MAP.put(Pattern.compile("^(" + RegexFactory.BOOLEAN_PRIMITIVE_REGEX + ")"), TokenType.BOOLEAN);
        PATTERN_MAP.put(Pattern.compile("^(" + RegexFactory.NUMBER_PRIMITIVE_REGEX + ")"), TokenType.NUMBER);
        PATTERN_MAP.put(Pattern.compile("^(" + RegexFactory.KEYWORD_REGEX + ")"), TokenType.KEYWORD);
        PATTERN_MAP.put(Pattern.compile("^(" + RegexFactory.OPERATOR_REGEX + ")"), TokenType.OPERATOR);
        PATTERN_MAP.put(Pattern.compile("^(" + RegexFactory.SEPARATOR_REGEX + ")"), TokenType.SEPARATOR);
        PATTERN_MAP.put(Pattern.compile("^(" + RegexFactory.IDENTIFIER_REGEX + ")"), TokenType.IDENTIFIER);
    }


    public BetterTokenizerStep(@Nullable StepOptions options) {
        super(options);
    }

    @Override
    public TokenQueue executeStep(List<LexerRow> lexerRows, CompileInformation compileInformation) throws CompileException {
        List<Token> tokens = new ArrayList<>();

        for (LexerRow lexerRow : lexerRows) {
            tokens.addAll(tokenize(lexerRow));
        }

        return new TokenQueue(TokenQueueId.of("BASE"), tokens);
    }

    private List<Token> tokenize(LexerRow row) throws UnrecognizedTokenException {
        Matcher m = null;
        TokenType type = null;
        List<Token> tokens = new ArrayList<>();

        String content = row.content();

        int currentTokenIndex = 0;
        while (!content.isBlank()) {
            boolean found = false;

            for (Map.Entry<Pattern, TokenType> entry : PATTERN_MAP.entrySet()) {
                m = entry.getKey().matcher(content);

                if (m.find()) {
                    found = true;
                    type = entry.getValue();
                    break;
                }
            }

            if (!found) {
                throw new UnrecognizedTokenException(row, currentTokenIndex);
            }

            String group = m.group();
            tokens.add(new Token(type, group, new LineInformation(row.rowIndex(), currentTokenIndex++)));

            if (content.length() > group.length()) {
                content = content.substring(group.length()).stripLeading();
            } else {
                content = "";
            }
        }

        return tokens;
    }

}
