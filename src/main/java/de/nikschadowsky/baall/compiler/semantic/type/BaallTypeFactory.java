package de.nikschadowsky.baall.compiler.semantic.type;


/**
 * @since 27.03.2025
 */
public class BaallTypeFactory {

    public static BaallTypeFactory create() {
        return new BaallTypeFactory();
    }

    private BaallTypeFactory() {
    }

    public PrimitiveType createPrimitiveType(PrimitiveType.Kind kind) {
        return switch (kind) {
            case NUMBER -> PrimitiveTypeImpl.NUMBER_TYPE;
            case STRING -> PrimitiveTypeImpl.STRING_TYPE;
            case BOOLEAN -> PrimitiveTypeImpl.BOOLEAN_TYPE;
            case STRUCT -> PrimitiveTypeImpl.STRUCT_TYPE;
            case EXCEPTION -> PrimitiveTypeImpl.EXCEPTION_TYPE;
        };
    }

    public UserDefinedType createUserDefinedType() {
        return new UserDefinedTypeImpl();
    }

    public FunctionType createFunctionType() {
        return new FunctionTypeImpl();
    }

    public ListType createListType() {
        return new ListTypeImpl();
    }

}
