package de.nikschadowsky.baall.compiler.grammar;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.lexer.tokens.TokenType;
import de.nikschadowsky.baall.compiler.util.ArrayUtility;
import de.nikschadowsky.baall.compiler.util.FileLoader;
import de.nikschadowsky.baall.compiler.util.RegexFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GrammarReader {
    private final String path;

    /**
     * Generates a {@link Grammar} from a file provided by a path. Special Symbols in the File are: 1. _EPSILON 2.
     * _STRING_PRIMITIVE, _BOOLEAN_PRIMITIVE, _NUMBER_PRIMITIVE 3. "any string" They are treated differently by the
     * reader. _EPSILON denotes an Epsilon as a transition of a formal grammar, _*_PRIMITIVE denotes variable tokens and
     * "any string" denotes a hard token, assigning it its literal value.
     *
     * @param path of Grammar File
     */
    public GrammarReader(String path) {
        this.path = path;
    }


    public Grammar generateGrammar() {
        GrammarFileContent content = createFileContentContainer(path);

        validateGrammarFileContent(content);

        Set<GrammarNonterminal> nonterminalSet = new HashSet<>();
        Set<GrammarProduction> ruleSet = new HashSet<>();

        GrammarNonterminal startSymbol = addAllNonterminal(nonterminalSet, content);
        addProductionRules(nonterminalSet, ruleSet, content);
        enhanceNonterminalsWithAnnotations(nonterminalSet, content);

        return new Grammar(startSymbol, nonterminalSet, ruleSet);
    }

    private GrammarFileContent createFileContentContainer(String path) {
        String preprocessed = preprocess(FileLoader.loadFileContent(path));
        String[] lines = preprocessed.split(RegexFactory.NEWLINE_REGEX);
        String[][] tokens = splitLines(lines);

        return new GrammarFileContent(
                path,
                preprocessed,
                lines,
                tokens
        );
    }

    private String preprocess(String content) {
        // remove comments
        content = content.replaceAll("#.*($|\\v)", "");
        // replace multiple line breaks
        content = content.replaceAll(RegexFactory.NEWLINE_REGEX, "\n");
        // replace multiple regular spaces
        content = content.replaceAll(" +", " ").trim();
        return content;
    }

    private static final String TOKENIZING_REGEX = "(?<!\\\\)@|(?<!\\\\)->";

    private String[][] splitLines(String[] lines) {
        String[][] tokens = new String[lines.length][];

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = lines[i].split(TOKENIZING_REGEX);

            int index = i;
            Arrays.setAll(tokens[index], j -> tokens[index][j].trim());
        }

        return tokens;
    }

    private void validateGrammarFileContent(GrammarFileContent content) {
        for (int l = 0; l < content.lineCount(); l++) {
            String exceptionMessage = createExceptionMessage(l, content.lines()[l], "Missing symbols");

            if (content.tokens[l].length < 2) {
                throw new GrammarSyntaxException(exceptionMessage);
            }

            for (int i = 0; i < content.tokens()[l].length; i++) {
                if (content.tokens()[l][i].isEmpty()) {
                    throw new GrammarSyntaxException(exceptionMessage);
                }
            }
        }

    }


    private GrammarNonterminal addAllNonterminal(Set<GrammarNonterminal> set, GrammarFileContent content) {
        GrammarNonterminal startSymbol = null;

        for (int l = 0; l < content.lineCount(); l++) {

            String[] tokens = content.tokens()[l];

            String identifier = tokens[0].toUpperCase();

            if (identifier.startsWith("_"))
                throw new GrammarSyntaxException(createExceptionMessage(l, content.lines()[l], "Meta symbols cannot be used as identifiers for nonterminals!"));

            if (!isNonterminalAlreadyInSet(set, identifier)) {
                GrammarNonterminal nonterminal = new GrammarNonterminal(identifier);

                if (startSymbol == null) {
                    startSymbol = nonterminal;
                }

                set.add(nonterminal);
            }
        }
        return startSymbol;
    }


    private void addProductionRules(Set<GrammarNonterminal> nonterminalSet, Set<GrammarProduction> ruleSet, GrammarFileContent content) {
        // uniquely identifies a Production Rule created by this Grammar Reader
        int productionRuleIdentifier = 0;

        for (int l = 0; l < content.lineCount(); l++) {
            String identifier = content.tokens()[l][0];
            String rules = content.tokens()[l][1];

            GrammarNonterminal nonterminal = getNonterminalFromSet(nonterminalSet, identifier);
            // rules for the current nonterminal
            Set<GrammarProduction> productionRules = new HashSet<>();

            String[] ruleStrings = rules.split("(?<!\\\\)\\|"); // any '|' without a '\' preceding

            for (String singleRule : ruleStrings) {
                List<GrammarSymbol> sententialForm = new ArrayList<>();

                boolean hasEpsilonProduction = false;

                for (String token : singleRule.trim().split(" ")) {
                    token = token.trim();

                    if (token.equals("_EPSILON")) {
                        if (hasEpsilonProduction)
                            throw new GrammarSyntaxException(createExceptionMessage(
                                    l,
                                    content.lines()[l],
                                    "Cannot have more than one Epsilon Production Rule for each Nonterminal"
                            ));
                        // Epsilon
                        hasEpsilonProduction = true;
                    } else if (token.matches("^\".*\"$")) {
                        // remove quotation and replace escaped pipe with regular pipe
                        String tokenValue = token.substring(1, token.length() - 1).replaceAll("\\\\\\|", "|");

                        sententialForm.add(new Token(TokenType.ANY, tokenValue));
                    } else if (token.charAt(0) == '_') {
                        switch (token) {
                            case "_STRING_PRIMITIVE" -> sententialForm.add(new Token(TokenType.STRING, ""));
                            case "_NUMBER_PRIMITIVE" -> sententialForm.add(new Token(TokenType.NUMBER, ""));
                            case "_BOOLEAN_PRIMITIVE" -> sententialForm.add(new Token(TokenType.BOOLEAN, ""));
                            default -> throw new GrammarSyntaxException(createExceptionMessage(
                                    l,
                                    content.lines()[l],
                                    "Unrecognized Meta-Symbol '" + token + "'!"));
                        }
                    } else {
                        sententialForm.add(getNonterminalFromSet(nonterminalSet, token));
                    }
                }
                productionRules.add(new GrammarProduction(productionRuleIdentifier++, nonterminal, sententialForm.toArray(GrammarSymbol[]::new)));
            }
            if (!nonterminal.setProductionRules(productionRules)) {
                throw new GrammarSyntaxException(createExceptionMessage(
                        l,
                        content.lines()[l],
                        "Production rules for '" + nonterminal.getFormatted() + "' can only be assigned once! "));
            }

            // add all production rules for this symbol
            ruleSet.addAll(productionRules);
        }
    }

    private void enhanceNonterminalsWithAnnotations(Set<GrammarNonterminal> nonterminalSet, GrammarFileContent content) {
        for (String[] token : content.tokens()) {
            Set<GrammarNonterminalAnnotation> annotations =
                    Set.of(Arrays
                            .stream(ArrayUtility.subarray(token, 2, -1))
                            .map(GrammarNonterminalAnnotation::new)
                            .toArray(GrammarNonterminalAnnotation[]::new));

            getNonterminalFromSet(nonterminalSet, token[0]).setAnnotations(annotations);
        }
    }


    private boolean isNonterminalAlreadyInSet(Set<GrammarNonterminal> set, String e) {
        return set.stream()
                .anyMatch(token -> token.getIdentifier().equals(e.toUpperCase()));
    }

    private GrammarNonterminal getNonterminalFromSet(Set<GrammarNonterminal> set, String e) {
        return set.stream()
                .filter(token -> token.getIdentifier().equals(e.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new GrammarSyntaxException("Nonterminal " + e + " is not defined!"));
    }

    private static String createExceptionMessage(int lineNumber, String line, String reason) {
        return "Invalid syntax in line %s: %s%nReason: %s".formatted(lineNumber, line, reason);
    }

    private record GrammarFileContent(
            @NotNull String path,
            @NotNull String preprocessed,
            @NotNull String[] lines,
            @NotNull String[][] tokens) {

        public int lineCount() {
            return lines.length;
        }
    }
}
