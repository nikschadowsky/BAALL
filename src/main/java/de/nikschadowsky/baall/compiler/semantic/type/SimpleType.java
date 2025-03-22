package de.nikschadowsky.baall.compiler.semantic.type;

public sealed interface SimpleType extends BaallType permits PrimitiveType, UserDefinedType {
}
