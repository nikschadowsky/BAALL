package main.de.nikschadowsky.baall.compiler.lexer.tokens;

import main.de.nikschadowsky.baall.compiler.grammar.GrammarSymbol;

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
    public boolean equalsSymbol(GrammarSymbol s) {
        return false;
    }
}
