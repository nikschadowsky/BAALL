package de.nikschadowsky.baall.compiler.semantic.type;

import java.util.Collections;
import java.util.List;

record FunctionTypeImpl(BaallType returnType, List<BaallType> parameterTypes) implements FunctionType {

    public FunctionTypeImpl(BaallType returnType, BaallType... parameterTypes) {
        this(returnType, parameterTypes == null ? List.of() : List.of(parameterTypes));
    }

    @Override
    public List<BaallType> getParameterTypes() {
        return Collections.unmodifiableList(parameterTypes);
    }

    @Override
    public boolean isNoneSafe() {
        return true;
    }
}