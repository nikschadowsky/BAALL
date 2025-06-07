package de.nikschadowsky.baall.compiler.symbol;


import org.jetbrains.annotations.NotNull;

/**
 * Record holding information about an element's location in the source code.
 *
 * @since 25.03.2025
 */
public record LineInformation(int line, int column) implements Comparable<LineInformation> {

    public static final LineInformation UNKNOWN = new LineInformation(-1, -1);

    @Override
    public @NotNull String toString() {
        if (UNKNOWN.equals(this)) {
            return "unknown location";
        }
        return "%s:%s".formatted(line, column);
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive
     * integer. An unknown location is always less than any other location except for itself.
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     */
    @Override
    public int compareTo(@NotNull LineInformation other) {
        int lineCompare = Integer.compare(line, other.line);
        return lineCompare != 0 ? lineCompare : Integer.compare(column, other.column);
    }
}
