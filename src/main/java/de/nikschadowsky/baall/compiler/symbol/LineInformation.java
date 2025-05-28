package de.nikschadowsky.baall.compiler.symbol;


import org.jetbrains.annotations.NotNull;

/**
 * @since 25.03.2025
 */
public record LineInformation(int line, int column) implements Comparable<LineInformation>{

    @Override
    public String toString() {
        return "%s:%s".formatted(line, column);
    }

    @Override
    public int compareTo(@NotNull LineInformation other) {
        int lineCompare = Integer.compare(line, other.line);
        return lineCompare != 0 ? lineCompare : Integer.compare(column, other.column);
    }
}
