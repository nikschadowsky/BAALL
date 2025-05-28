package de.nikschadowsky.baall.compiler.semantic.type;

record ListTypeImpl(BaallType elementType) implements ListType {

    @Override
    public boolean isNoneSafe() {
        return true;
    }

}
