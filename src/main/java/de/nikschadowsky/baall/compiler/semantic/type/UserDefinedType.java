package de.nikschadowsky.baall.compiler.semantic.type;

public sealed interface UserDefinedType extends SimpleType permits UserDefinedTypeImpl {

    TypeReference reference();

    boolean isKnown();

    boolean isUnknown();

}
