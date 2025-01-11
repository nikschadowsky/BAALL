package de.nikschadowsky.baall.compiler.lexer;


/**
 * @since 30.09.2024
 */
public record LexerRow(String content, int rowIndex) {

    public int length() {
        return content.length();
    }

    public boolean contains(String sequence) {
        return content.contains(sequence);
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

}
