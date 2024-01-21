package de.nikschadowsky.baall.compiler.lexer.tokens;

import de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;

public class Token implements GrammarSymbol {

    private final TokenType type;

    private final String value;


    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;

    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type.getDescription() +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public String getFormatted() {
        return "'" + getValue() + "'";
    }

    /**
     * Compares Token to Symbol. If s is an instance of Token they are compared accordingly. False otherwise. If any of
     * both Tokens are of TokenType.ANY, value is compared. If both s and this instance are of either type
     * TokenType.STRING, TokenType.NUMBER or TokenType.BOOLEAN return true. Else type and value are compared.
     *
     * @param s GrammarSymbol to compare to
     * @return if symbols match
     */
    @Override
    public boolean symbolMatches(GrammarSymbol s) {
        if (s instanceof Token token) {

            // if one of the token is type ANY, compare literal value
            if (getType().equals(TokenType.ANY) || token.getType().equals(TokenType.ANY)) {
                return value.equals(token.getValue());
            }

            if ((getType().equals(TokenType.STRING) && token.getType().equals(TokenType.STRING))
                    || (getType().equals(TokenType.BOOLEAN) && token.getType().equals(TokenType.BOOLEAN))
                    || (getType().equals(TokenType.NUMBER) && token.getType().equals(TokenType.NUMBER))) {
                return true;
            }

            // else the type must be equal too
            return getType().equals(token.getType()) && getValue().equals(token.getValue());

        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token token)
            return getType().equals(token.getType()) && getValue().equals(token.getValue());
        return false;
    }
}
