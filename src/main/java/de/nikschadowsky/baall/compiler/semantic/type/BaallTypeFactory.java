package de.nikschadowsky.baall.compiler.semantic.type;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;

import java.util.List;

/**
 * @since 27.03.2025
 */
public class BaallTypeFactory {

    public static BaallTypeFactory create() {
        return new BaallTypeFactory();
    }

    private BaallTypeFactory() {
    }

    public PrimitiveType createPrimitiveType(PrimitiveTypeNode.Kind kind) {
        return switch (kind) {
            case NUMBER -> PrimitiveTypeImpl.NUMBER_TYPE;
            case STRING -> PrimitiveTypeImpl.STRING_TYPE;
            case BOOLEAN -> PrimitiveTypeImpl.BOOLEAN_TYPE;
            case STRUCT -> PrimitiveTypeImpl.STRUCT_TYPE;
            case EXCEPTION -> PrimitiveTypeImpl.EXCEPTION_TYPE;
        };
    }

    public UserDefinedType createUserDefinedType(TypeReference typeReference) {
        return new UserDefinedTypeImpl(typeReference);
    }

    public FunctionType createFunctionType(BaallType returnType, List<BaallType> parameterTypes) {
        return new FunctionTypeImpl();
    }

    public ListType createListType(BaallType elementType) {
        return new ListTypeImpl(elementType);
    }

}
