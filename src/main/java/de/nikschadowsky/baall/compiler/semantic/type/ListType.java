package de.nikschadowsky.baall.compiler.semantic.type;

public sealed interface ListType extends BaallType permits ListTypeImpl {

    BaallType elementType();

}
