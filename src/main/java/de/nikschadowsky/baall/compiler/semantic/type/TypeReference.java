package de.nikschadowsky.baall.compiler.semantic.type;


/**
 * @since 30.03.2025
 */
public interface TypeReference {

    Kind getKind();

    enum Kind {
        STRUCT, EXCEPTION
    }

}
