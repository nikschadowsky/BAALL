package de.nikschadowsky.baall.compiler.semantic.type;


/**
 * @since 30.03.2025
 */
public interface TypeReference {

    Kind getKind();

    enum Kind {
        /**
         * Constant for a type reference to a struct
         */
        STRUCT,
        /**
         * Constant for a type reference to an exception
         */
        EXCEPTION,
        /**
         * Constant for an unknown type reference
         */
        UNKNOWN
    }

    /**
     * Constructs a new type reference to an unknown type.
     *
     * @param reason the reason why this type is unknown
     * @return a new unknown type reference
     */
    static TypeReference unknown(String reason) {
        return new UnknownTypeReference(reason);
    }

}
