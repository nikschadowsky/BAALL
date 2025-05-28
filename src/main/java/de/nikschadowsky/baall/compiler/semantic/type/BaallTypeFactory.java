package de.nikschadowsky.baall.compiler.semantic.type;


import de.nikschadowsky.baall.compiler.symbol.UnresolvedSymbolException;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.*;

import java.util.List;
import java.util.Optional;

/**
 * @since 27.03.2025
 */
public class BaallTypeFactory {

    private final TypeTable typeTable;

    public BaallTypeFactory(TypeTable typeTable) {
        this.typeTable = typeTable;
    }

    public BaallType getTypeForNode(TypeNode node) {
        return switch (node) {
            case PrimitiveTypeNode p -> createPrimitiveType(p.getKind());
            case ListTypeNode l -> createListType(getTypeForNode(l.getInnerType()));
            case FunctionTypeNode f -> createFunctionType(
                    getTypeForNode(f.getInnerType()),
                    f.getParameterTypes()
                     .stream()
                     .map(this::getTypeForNode)
                     .toList()
            );
            // todo where do i get this scope from? argument?
            case IdentifierTypeNode i -> createUserDefinedType(typeTable.resolveType(i, null), i.isNoneSafe());
        };
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

    public UserDefinedType createUserDefinedType(TypeReference typeReference, boolean isNoneSafe) {
        return new UserDefinedTypeImpl(typeReference, isNoneSafe);
    }

    public FunctionType createFunctionType(BaallType returnType, List<BaallType> parameterTypes) {
        return new FunctionTypeImpl(returnType, parameterTypes);
    }

    public ListType createListType(BaallType elementType) {
        return new ListTypeImpl(elementType);
    }

    public TypeTable getTypeTable() {
        return typeTable;
    }

}
