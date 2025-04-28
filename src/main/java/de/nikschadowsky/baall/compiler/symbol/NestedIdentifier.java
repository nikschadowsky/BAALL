package de.nikschadowsky.baall.compiler.symbol;


import java.util.List;

/**
 * @since 27.04.2025
 */
public record NestedIdentifier(List<String> memberSelects) implements SymbolTableIdentifier {

    public static NestedIdentifier of(String first, String second) {
        return new NestedIdentifier(List.of(first, second));
    }

}
