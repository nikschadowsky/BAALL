package de.nikschadowsky.baall.compiler.semantic.type;

import java.util.List;

public sealed interface FunctionType extends BaallType permits FunctionTypeImpl {

    List<BaallType> getParameterTypes();

}
