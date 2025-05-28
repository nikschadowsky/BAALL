package de.nikschadowsky.baall.compiler.semantic.type;

record UserDefinedTypeImpl(TypeReference reference, boolean isNoneSafe) implements UserDefinedType {

    @Override
    public boolean isKnown() {
        return !isUnknown();
    }

    @Override
    public boolean isUnknown() {
        return reference.getKind() == TypeReference.Kind.UNKNOWN;
    }
}
