package de.nikschadowsky.baall.compiler.semantic.type;

import org.jetbrains.annotations.NotNull;

public sealed interface PrimitiveType extends SimpleType permits PrimitiveTypeImpl {

    @NotNull Kind getKind();

    enum Kind {
        NUMBER, STRING, BOOLEAN, STRUCT, EXCEPTION
    }

}
