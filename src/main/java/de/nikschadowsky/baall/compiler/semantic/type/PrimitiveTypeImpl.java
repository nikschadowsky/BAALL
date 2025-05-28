package de.nikschadowsky.baall.compiler.semantic.type;

import org.jetbrains.annotations.NotNull;

enum PrimitiveTypeImpl implements PrimitiveType {

    NUMBER_TYPE(Kind.NUMBER),
    STRING_TYPE(Kind.STRING),
    BOOLEAN_TYPE(Kind.BOOLEAN),
    EXCEPTION_TYPE(Kind.EXCEPTION),
    STRUCT_TYPE(Kind.STRUCT);

    private final Kind kind;

    PrimitiveTypeImpl(Kind kind) {
        this.kind = kind;
    }

    @Override
    public @NotNull Kind getKind() {
        return kind;
    }

    @Override
    public boolean isNoneSafe() {
        return true;
    }
}
