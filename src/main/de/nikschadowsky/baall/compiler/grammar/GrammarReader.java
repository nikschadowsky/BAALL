package de.nikschadowsky.baall.compiler.grammar;

import de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import de.nikschadowsky.baall.compiler.lexer.tokens.TokenType;
import de.nikschadowsky.baall.compiler.util.ArrayUtility;
import de.nikschadowsky.baall.compiler.util.FileLoader;

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
        String preprocessed = preprocess(FileLoader.loadFileContent(path));

        Set<GrammarNonterminal> nonterminalSet = new HashSet<>();
        Set<GrammarProduction> ruleSet = new HashSet<>();

        String[][] tokens = splitContent(preprocessed);

        GrammarNonterminal startSymbol = addAllNonterminal(nonterminalSet, lines);

        addProductionRules(nonterminalSet, ruleSet, lines);
        GrammarNonterminal startSymbol = addAllNonterminal(nonterminalSet, tokens);
        addProductionRules(nonterminalSet, ruleSet, tokens);
        enhanceNonterminalsWithAnnotations(nonterminalSet, tokens);

        return new Grammar(startSymbol, nonterminalSet, ruleSet);
    }

    private String preprocess(String content) {
        // remove comments
        content = content.replaceAll("#.*($|\\v)", "");
        // replace multiple line breaks
        content = content.replaceAll("\\v+", "\n");
        // replace multiple regular spaces
        content = content.replaceAll(" +", " ").trim();
        return content;
    }

    private static String[][] splitContent(String content) {
        String[] lines = content.split("\\v");
        String[][] tokens = new String[lines.length][];

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = lines[i].split("(?<!\\\\)@|(?<!\\\\)->");
        }

        return tokens;
    }


    private static final String tokenRegex = "[^\\\\]->";

    /**
     * Traverses lines to find all Nonterminals and adds them to the provided Set
     *
     * @param set    of Nonterminals
     * @param tokens
     * @return the First GrammarNonterminal mentioned in the input String
     */
    private GrammarNonterminal addAllNonterminal(Set<GrammarNonterminal> set, String[][] tokens) {
        GrammarNonterminal startSymbol = null;

        for (int i = 0; i < tokens.length; i++) {

            String[] tokenizedLine = tokens[i];

            if (tokens.length != 2) {
                throw new GrammarSyntaxException("Error while parsing line '" + i + "' in " + path + "! Invalid syntax!");
            }

            String identifier = tokenizedLine[0].toUpperCase();

            if (identifier.startsWith("_"))
                throw new GrammarSyntaxException("Error while parsing line " + i + " in " + path + "! Meta-Symbols cannot be identifiers for Nonterminals!");

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


    private void addProductionRules(Set<GrammarNonterminal> nonterminalSet, Set<GrammarProduction> ruleSet, String[][] tokens) {
        // uniquely identifies a Production Rule created by this Grammar Reader
        int productionRuleIdentifier = 0;

        for (int i = 0; i < tokens.length; i++) {
            String[] tokenizedLine = tokens[i];

            String[] tokens = line.split(tokenRegex);
            String identifier = tokenizedLine[0];
            String rightSide = tokenizedLine[1];

            String[] ruleStrings = rightSide.split("(?<!\\\\)\\|"); // any '|' without a '\' preceding

            GrammarNonterminal nonterminal = getNonterminalFromSet(nonterminalSet, identifier);

            // rules for the current nonterminal
            Set<GrammarProduction> productionRules = new HashSet<>();

            for (String s : ruleStrings) {
                List<GrammarSymbol> sententialForm = new ArrayList<>();

                boolean hasEpsilonProduction = false;

                for (String token : s.trim().split(" ")) {
                    token = token.trim();

                    if (token.equals("_EPSILON")) {
                        if (hasEpsilonProduction)
                            throw new GrammarSyntaxException("Cannot have more than one Epsilon Production Rule");
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
                            default ->
                                    throw new GrammarSyntaxException("Unrecognized Meta-Symbol " + token + " in line " + i + " in " + path + "!");
                        }

                    } else {
                        sententialForm.add(getNonterminalFromSet(nonterminalSet, token));
                    }
                }
                productionRules.add(new GrammarProduction(productionRuleIdentifier++, nonterminal, sententialForm.toArray(GrammarSymbol[]::new)));
            }
            if (!nonterminal.setProductionRules(productionRules)) {
                throw new GrammarSyntaxException("Production Rules of Nonterminal " + nonterminal.getIdentifier() + " can only be assigned once! ");
            }

            // add all production rules for this symbol
            ruleSet.addAll(productionRules);
        }
    }

    private void enhanceNonterminalsWithAnnotations(Set<GrammarNonterminal> nonterminalSet, String[][] tokens) {
        for (String[] token : tokens) {
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
}
