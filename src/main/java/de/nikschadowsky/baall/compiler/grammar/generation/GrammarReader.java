package de.nikschadowsky.baall.compiler.grammar.generation;

import de.nikschadowsky.baall.compiler.grammar.*;
import de.nikschadowsky.baall.compiler.lexer.tokens.NoTokenTypeFoundException;
import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.lexer.tokens.TokenType;
import de.nikschadowsky.baall.compiler.util.ArrayUtility;
import de.nikschadowsky.baall.compiler.util.FileLoader;
import de.nikschadowsky.baall.compiler.util.LambdaUtility;
import de.nikschadowsky.baall.compiler.util.RegexFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The GrammarReader class is a singleton class used for generating a {@link Grammar} object from a file. A grammar file
 * consists of production rules, separated by a linefeed. A {@link GrammarProduction} always consists of a
 * {@link GrammarNonterminal} identifier followed by an arrow operator '->' and a series of rules separated by a pipe
 * '|' consisting of identifiers for {@link GrammarSymbol GrammarSymbols} separated by a space. These symbols can either
 * be {@link GrammarNonterminal GrammarNonterminals}, {@link Token Tokens} surrounded by "double quotes" or meta
 * symbols. Meta symbols always begin with an underscore ('_'), and may be of the following list of recognized meta
 * symbols:
 * <ul>
 *     <li>
 *         _EPSILON - signaling an empty {@link GrammarProduction production rule} often referred to as an 'epsilon rule'. Cannot be used with any other symbol in the same production and may only be used once on a {@link GrammarNonterminal}
 *     </li>
 *     <li>
 *         _STRING - signaling a variable chain of characters enclosed in quotation marks. It acts as a terminal symbol.
 *     </li>
 *     <li>
 *         _NUMBER - signaling a hex, binary or decimal number. It acts as a terminal symbol.
 *     </li>
 *     <Li>
 *         _BOOLEAN - signaling a boolean value. It acts as a terminal symbol.
 *     </Li>
 * </ul>
 * The first nonterminal read by this reader will be used as the entry point or start symbol of this grammar.
 */
public class GrammarReader {

    private static GrammarReader instance;

    private GrammarReader() {
    }

    /**
     * Get the instance of this singleton.
     *
     * @return singleton instance
     */
    public static GrammarReader getInstance() {
        if (instance == null) instance = new GrammarReader();
        return instance;
    }


    /**
     * Generate a grammar from a grammar definition specified by a path.
     *
     * @param path path of file
     * @return a new grammar object representing the specified grammar definition
     * @see GrammarReader
     */
    public @NotNull Grammar generateGrammar(@NotNull Path path) {
        GrammarFileContent content = createFileContentContainer(path);

        validateGrammarFileContent(content);

        Set<GrammarNonterminal> nonterminalSet = addAllNonterminal(content);
        Set<GrammarProduction> ruleSet = new HashSet<>(addProductionRules(nonterminalSet, content));
        enhanceNonterminalsWithAnnotations(nonterminalSet, content);

        GrammarNonterminal startSymbol = determineStartSymbol(nonterminalSet);

        return new Grammar(startSymbol, nonterminalSet, ruleSet);
    }

    private @NotNull GrammarFileContent createFileContentContainer(@NotNull Path path) {
        if (!FileLoader.getFileExtension(path).equals("grammar")) {
            System.out.println("Grammar files should use the '.grammar' extension!");
        }
        String preprocessed = preprocess(FileLoader.getFileContent(path));
        String[] lines = preprocessed.split(RegexFactory.NEWLINE_REGEX);
        String[][] tokens = splitLines(lines);

        return new GrammarFileContent(path, preprocessed, lines, tokens);
    }

    private @NotNull String preprocess(String content) {
        // remove comments
        content = content.replaceAll("#.*($|\\v)", "");
        // replace multiple line breaks
        content = content.replaceAll(RegexFactory.NEWLINE_REGEX, "\n");
        // replace multiple regular spaces
        content = content.replaceAll(" +", " ").trim();
        return content;
    }

    private @NotNull String[][] splitLines(String @NotNull [] lines) {
        String[][] tokens = new String[lines.length][];

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = lines[i].split(RegexFactory.GRAMMAR_LINE_TOKENIZING_REGEX);

            int index = i;
            Arrays.setAll(tokens[index], j -> tokens[index][j].trim());
        }

        return tokens;
    }

    private void validateGrammarFileContent(@NotNull GrammarFileContent content) {
        if (content.lineCount() == 0) {
            throw new GrammarSyntaxException("File does not contain grammar definition!");
        }

        for (int l = 0; l < content.lineCount(); l++) {
            if (content.tokens[l].length < 2) {
                throw new GrammarSyntaxException(createLineString(l, content.lines()[l]), "Missing symbols!");
            }

            for (int i = 0; i < content.tokens()[l].length; i++) {
                if (content.tokens()[l][i].isEmpty()) {
                    throw new GrammarSyntaxException(createLineString(l, content.lines()[l]), "Missing symbols!");
                }
            }
        }

    }


    private @NotNull Set<GrammarNonterminal> addAllNonterminal(@NotNull GrammarFileContent content) {
        Set<GrammarNonterminal> nonterminalSet = new HashSet<>();

        for (int l = 0; l < content.lineCount(); l++) {

            String[] tokens = content.tokens()[l];

            String identifier = tokens[0].toUpperCase();

            if (identifier.startsWith("_")) {
                throw new GrammarSyntaxException(
                        createLineString(l, content.lines()[l]),
                        "Meta symbols cannot be used as identifiers for nonterminals!"
                );
            }

            if (!isNonterminalAlreadyInSet(nonterminalSet, identifier)) {
                nonterminalSet.add(new GrammarNonterminal(identifier));
            }
        }
        return nonterminalSet;
    }

    private @NotNull Set<GrammarProduction> addProductionRules(@NotNull Set<GrammarNonterminal> nonterminalSet, @NotNull GrammarFileContent content) {
        Set<GrammarProduction> ruleSet = new HashSet<>();

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
                        if (hasEpsilonProduction) {
                            throw new GrammarSyntaxException(
                                    createLineString(l, content.lines()[l]),
                                    "Cannot have more than one Epsilon Production Rule for each Nonterminal"
                            );
                        }
                        // Epsilon
                        hasEpsilonProduction = true;
                    } else if (token.charAt(0) == '_') {
                        TokenType type;
                        try {
                            type = TokenType.getTokenTypeForDescription(token.substring(1));
                        } catch (NoTokenTypeFoundException exception) {
                            throw new GrammarSyntaxException(
                                    createLineString(l, content.lines()[l]),
                                    "Unrecognized meta symbol '" + token + "'!",
                                    exception
                            );
                        }

                        if (!type.hasExactTokenMatching()) {
                            sententialForm.add(new Token(type, ""));
                        } else {
                            throw new GrammarSyntaxException(
                                    createLineString(l, content.lines()[l]),
                                    "Token type '" + type + "' cannot be used as a meta symbol!"
                            );
                        }

                    } else if (token.matches("^\".*\"$")) {
                        // remove quotation and replace escaped pipe with regular pipe
                        String tokenValue = token.substring(1, token.length() - 1).replaceAll("\\\\\\|", "|");

                        sententialForm.add(new Token(TokenType.ANY, tokenValue));
                    } else {
                        sententialForm.add(getNonterminalFromSet(nonterminalSet, token));
                    }
                }
                productionRules.add(new GrammarProduction(
                        GrammarProductionUIDGenerator.getInstance().nextID(),
                        sententialForm.toArray(GrammarSymbol[]::new)
                ));
            }
            if (!nonterminal.addProductionRules(productionRules)) {
                throw new GrammarSyntaxException(
                        createLineString(
                                l,
                                content.lines()[l]
                        ),
                        "Production rules for '" + nonterminal.getFormatted() + "' can only be assigned once! "
                );
            }

            // add all production rules for this symbol
            ruleSet.addAll(productionRules);
        }

        return ruleSet;
    }

    private GrammarNonterminal determineStartSymbol(@NotNull Set<GrammarNonterminal> nonterminalSet) {
        Set<GrammarNonterminal> startCandidates = nonterminalSet.stream()
                                                                .filter(n -> n.hasAnnotation("Start"))
                                                                .collect(Collectors.toSet());
        if (startCandidates.size() > 1)
            throw new GrammarSyntaxException("Multiple nonterminals annotated with @Start!");

        return startCandidates.stream()
                              .findAny()
                              .orElseThrow(LambdaUtility.createSupplier(new GrammarSyntaxException(
                                      "No nonterminal annotated with @Start!"
                              )));
    }

    private void enhanceNonterminalsWithAnnotations(@NotNull Set<GrammarNonterminal> nonterminalSet, @NotNull GrammarFileContent content) {
        for (String[] token : content.tokens()) {
            Set<GrammarNonterminalAnnotation> annotations =
                    Set.of(Arrays.stream(ArrayUtility.subarray(token, 2, -1))
                                 .map(GrammarNonterminalAnnotation::new)
                                 .toArray(GrammarNonterminalAnnotation[]::new));

            getNonterminalFromSet(nonterminalSet, token[0]).addAnnotations(annotations);
        }
    }


    private boolean isNonterminalAlreadyInSet(@NotNull Set<GrammarNonterminal> set, @NotNull String e) {
        return set.stream().anyMatch(token -> token.getIdentifier().equals(e.toUpperCase()));
    }

    private @NotNull GrammarNonterminal getNonterminalFromSet(@NotNull Set<GrammarNonterminal> set, @NotNull String e) {
        return set.stream()
                  .filter(token -> token.getIdentifier().equals(e.toUpperCase()))
                  .findAny()
                  .orElseThrow(() -> new GrammarSyntaxException("Nonterminal " + e + " is not defined!"));
    }

    private static String createLineString(int lineNumber, String line) {
        return "Exception in line %s: %s".formatted(lineNumber, line);
    }

    private record GrammarFileContent(@NotNull Path path,
                                      @NotNull String preprocessed,
                                      @NotNull String[] lines,
                                      @NotNull String[][] tokens) {


        public int lineCount() {
            return lines.length;
        }
    }
}
