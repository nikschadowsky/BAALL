package de.nikschadowsky.baall.compiler.symbol;


/**
 * @since 27.04.2025
 */
public record StringIdentifier(String identifier) implements SymbolTableIdentifier {

    public static StringIdentifier of(String identifier) {
        return new StringIdentifier(identifier);
    }

}
