package main.de.nikschadowsky.baall.compiler.grammar;

import main.de.nikschadowsky.baall.compiler.lexer.tokens.Token;
import main.de.nikschadowsky.baall.compiler.lexer.tokens.TokenType;
import main.de.nikschadowsky.baall.compiler.util.FileLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrammarReader {

    private final String path;

    private final String content;

    /**
     * Creates a GrammarReader object to generate a {@link Grammar} from a file provided by a path.
     * Special Symbols in the File are:
     * 1. _EPSILON
     * 2. _STRING_PRIMITIVE, _BOOLEAN_PRIMITIVE, _NUMBER_PRIMITIVE
     * 3. "any string"
     * They are treated differently by the reader.
     * _EPSILON denotes an Epsilon as a transition of a formal grammar,
     * _*_PRIMITIVE denotes variable tokens
     * and "any string" denotes a hard token, assigning it its literal value.
     *
     * @param path of Grammar File
     */
    public GrammarReader(String path) {
        this.path = path;

        content = FileLoader.loadFileContent(path);
    }


    public Grammar generateGrammar() {

        Set<GrammarNonterminal> nonterminalSet = new HashSet<>();

        String preprocessed = preprocess(content);

        String[] lines = preprocessed.split("\\v");

        GrammarNonterminal first = addAllNonterminal(nonterminalSet, lines);

        addDerivations(nonterminalSet, lines);

        return new Grammar(first, nonterminalSet);

    }

    private boolean isNonterminalAlreadyInSet(Set<GrammarNonterminal> set, String e) {
        return set.stream().anyMatch(token -> token.getIdentifier().equals(e.toUpperCase()));
    }

    private GrammarNonterminal getNonterminalFromSet(Set<GrammarNonterminal> set, String e) {
        return set.stream().filter(token -> token.getIdentifier()
                        .equals(e.toUpperCase())).findAny()
                .orElseThrow(() -> new GrammarSyntaxException("Nonterminal " + e + " is not defined!"));
    }

    private String preprocess(String content) {
        // replace multiple line breaks
        content = content.replaceAll("\\v+", "\n");
        // replace multiple regular spaces
        content = content.replaceAll(" +", " ").trim();
        return content;
    }


    private static final String tokenRegex = "[^\\\\]->";

    /**
     * Traverses lines to find all Nonterminals and adds them to the provided Set
     *
     * @param set of Nonterminals
     * @param lines dissected code preprocessed lines
     * @return the First GrammarNonterminal mentioned in the input String
     */
    private GrammarNonterminal addAllNonterminal(Set<GrammarNonterminal> set, String[] lines) {
        GrammarNonterminal first = null;
        boolean firstSet = false;

        for (String line : lines) {

            String[] tokens = line.split(tokenRegex);

            if (tokens.length != 2) {
                throw new GrammarSyntaxException("Error while parsing line " + line + " in " + path + "! Invalid syntax!");
            }

            String identifier = tokens[0].toUpperCase();

            if (identifier.equalsIgnoreCase("_EPSILON"))
                throw new GrammarSyntaxException("Error while parsing line " + line + " in " + path + "! EPSILON cannot be an identifier for a Nonterminal!");

            if (!isNonterminalAlreadyInSet(set, identifier)) {

                GrammarNonterminal nonterminal = new GrammarNonterminal(identifier);

                if (!firstSet) {
                    first = nonterminal;
                    firstSet = true;
                }

                set.add(nonterminal);
            }

        }
        return first;
    }

    private void addDerivations(Set<GrammarNonterminal> set, String[] lines) {
        for (String line : lines) {

            String[] tokens = line.split(tokenRegex);

            String identifier = tokens[0];
            String rawDerivation = tokens[1];

            GrammarNonterminal nonterminal = getNonterminalFromSet(set, identifier);

            List<GrammarDerivation> allDerivationsList = new ArrayList<>();

            // allow for literal "|" using the form "\|" 
            // FIXME: 19.10.2023 why is this so complicated, just do '\|' maybe. having a backslash as a valid language token is weird and should be discouraged.
            for (String s : rawDerivation.split("(?<!\\\\)\\|")) {

                List<GrammarSymbol> derivation = new ArrayList<>();

                for (String token : s.trim().split(" ")) {
                    token = token.trim();

                    // meta symbol
                    if(token.startWith('_')){
                        if(token.equals("_EPSILON"))
                            // epsilon character
                            break;

                        else {
                            // find possible match in tokentype enum
                        }
                    }


                    if (token.equals("_EPSILON")) {
                        // Epsilon
                        break;
                    } else if (token.matches("^\".*\"$")) {
                        String tokenValue = token.substring(1, token.length() - 1).replaceAll("\\\\|", "|");

                        derivation.add(new Token(TokenType.ANY, tokenValue));
                    } else if (token.charAt(0) == '_') {
                        switch (token) {
                            case "_STRING_PRIMITIVE" -> derivation.add(new Token(TokenType.STRING, ""));
                            case "_NUMBER_PRIMITIVE" -> derivation.add(new Token(TokenType.NUMBER, ""));
                            case "_BOOLEAN_PRIMITIVE" -> derivation.add(new Token(TokenType.BOOLEAN, ""));
                            default ->
                                    throw new GrammarSyntaxException("Unrecognized Meta-Symbol " + token + " in line " + line + "!");
                        }

                    } else {
                        derivation.add(getNonterminalFromSet(set, token));
                    }
                }


                allDerivationsList.add(new GrammarDerivation(derivation.toArray(GrammarSymbol[]::new)));

            }
            if (!nonterminal.setDerivationList(allDerivationsList)) {
                throw new GrammarSyntaxException("Derivation of Nonterminal " + nonterminal.getIdentifier() + " can only be assigned once! ");
            }
        }
    }

}
