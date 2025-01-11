package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.lexer.LexerRow;

/**
 * @since 03.10.2024
 */
public class LexerRowAssertion extends BaseAssertion<LexerRowAssertion, LexerRow> {

    protected LexerRowAssertion(LexerRow actual) {
        super(actual, LexerRowAssertion.class);
    }

    public LexerRowAssertion hasRowIndex(int expected) {
        baseAssert("rowIndex", LexerRow::rowIndex, expected);
        return this;
    }

    public LexerRowAssertion hasContentContaining(String expected) {
        truthinessAssert(
                row -> "Expected content%n'%s'%nto contain%n'%s'".formatted(row.content(), expected),
                row -> row.content().contains(expected)
        );
        return this;
    }

    public LexerRowAssertion hasContent(String expected) {
        baseAssert("content", LexerRow::content, expected);
        return this;
    }
}
