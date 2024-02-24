package de.nikschadowsky.compiler.lexer.tokens;

import de.nikschadowsky.compiler.grammar.GrammarSymbol;

import java.util.Objects;

public record Token(TokenType type, String value) implements GrammarSymbol {

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
        return "'" + value() + "'";
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
            if (type().equals(TokenType.ANY) || token.type().equals(TokenType.ANY)) {
                return value.equals(token.value());
            }

            if (!type().hasExactTokenMatching() && type().equals(token.type())) {
                return true;
            }

            // else the type must be equal too
            return type().equals(token.type()) && value().equals(token.value());

        }

        return false;
    }

    @Override
    public boolean symbolEquals(GrammarSymbol s) {
        return equals(s);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token token)
            return type().equals(token.type()) && value().equals(token.value());
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type(), value());
    }
}
