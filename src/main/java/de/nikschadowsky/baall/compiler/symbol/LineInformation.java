package de.nikschadowsky.baall.compiler.symbol;


/**
 * @since 25.03.2025
 */
public record LineInformation(int line, int column) {

    @Override
    public String toString() {
        return "%s:%s".formatted(line, column);
    }
}
