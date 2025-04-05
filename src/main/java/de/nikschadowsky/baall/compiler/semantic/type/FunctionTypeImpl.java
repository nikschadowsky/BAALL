package de.nikschadowsky.baall.compiler.semantic.type;

import java.util.Collections;
import java.util.List;

record FunctionTypeImpl(BaallType returnType, List<BaallType> parameterTypes) implements FunctionType {

    public FunctionTypeImpl(BaallType returnType, BaallType... types) {
        this(returnType, types == null ? List.of() : List.of(types));
    }

    @Override
    public List<BaallType> getParameterTypes() {
        return Collections.unmodifiableList(parameterTypes);
    }
}