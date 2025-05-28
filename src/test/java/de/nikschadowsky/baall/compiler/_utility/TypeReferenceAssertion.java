package de.nikschadowsky.baall.compiler._utility;

import de.nikschadowsky.baall.compiler.semantic.type.TypeReference;
import de.nikschadowsky.baall.compiler.semantic.type.TypeTable;
import de.nikschadowsky.baall.compiler.semantic.type.UnknownTypeReference;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;

public class TypeReferenceAssertion extends BaseAssertion<TypeReferenceAssertion, TypeReference> {

    protected TypeReferenceAssertion(TypeReference actual) {
        super(actual, TypeReferenceAssertion.class);
    }

    public TypeReferenceAssertion isStruct() {
        return baseAssert("kind", TypeReference::getKind, TypeReference.Kind.STRUCT);
    }

    public TypeReferenceAssertion isException() {
        return baseAssert("kind", TypeReference::getKind, TypeReference.Kind.EXCEPTION);
    }

    public UnknownTypeReferenceAssertion isUnknown() {
        baseAssert("kind", TypeReference::getKind, TypeReference.Kind.UNKNOWN);
        return new UnknownTypeReferenceAssertion((UnknownTypeReference) actual);
    }

    public TypeReferenceAssertion hasField(TypeTable lookup, IdentifierNode identifierNode) {
        return truthinessAssert(
                typeReference -> "Type does not have field: " + identifierNode,
                typeReference -> lookup.hasField(typeReference, identifierNode)
        );
    }

    public TypeReferenceAssertion doesNotHaveField(TypeTable lookup, IdentifierNode identifierNode) {
        return falsenessAssert(
                typeReference -> "Type has field but shouldn't: " + identifierNode,
                typeReference -> lookup.hasField(typeReference, identifierNode)
        );
    }

    public static class UnknownTypeReferenceAssertion extends BaseAssertion<UnknownTypeReferenceAssertion, UnknownTypeReference> {

        protected UnknownTypeReferenceAssertion(UnknownTypeReference actual) {
            super(actual, UnknownTypeReferenceAssertion.class);
        }

        public UnknownTypeReferenceAssertion hasReasonContaining(String expected) {
            return truthinessAssert(
                    unknownTypeReference -> "Expected reason to contain '%s' but was '%s'".formatted(expected, unknownTypeReference.getReason()),
                    unknownTypeReference -> unknownTypeReference.getReason().contains(expected)
            );
        }
    }
}
