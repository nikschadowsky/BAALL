package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.*;

/**
 * @since 12.02.2025
 */
public class TypeNodeAssertion extends BaseAssertion<TypeNodeAssertion, TypeNode> {

    public TypeNodeAssertion(TypeNode actual) {
        super(actual, TypeNodeAssertion.class);
    }

    public PrimitiveTypeNodeAssertion isPrimitiveType() {
        myself.isInstanceOf(PrimitiveTypeNode.class);
        return NodeAssertionFactory.create((PrimitiveTypeNode) actual);
    }

    public IdentifierTypeNodeAssertion isIdentifierType() {
        myself.isInstanceOf(IdentifierTypeNode.class);
        return NodeAssertionFactory.create((IdentifierTypeNode) actual);
    }

    public FunctionTypeNodeAssertion isFunctionType() {
        myself.isInstanceOf(FunctionTypeNode.class);
        return NodeAssertionFactory.create((FunctionTypeNode) actual);
    }

    public ListTypeNodeAssertion isListType() {
        myself.isInstanceOf(ListTypeNode.class);
        return NodeAssertionFactory.create((ListTypeNode) actual);
    }

    public static class PrimitiveTypeNodeAssertion extends BaseAssertion<PrimitiveTypeNodeAssertion, PrimitiveTypeNode> {

        public PrimitiveTypeNodeAssertion(PrimitiveTypeNode actual) {
            super(actual, PrimitiveTypeNodeAssertion.class);
        }

        public PrimitiveTypeNodeAssertion hasIdentifier(String identifier) {
            return hasIdentifierMatching(a -> a.isIdentifier().hasName(identifier));
        }

        public PrimitiveTypeNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<ElementAccessNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getType()));
            return this;
        }

    }

    public static class IdentifierTypeNodeAssertion extends BaseAssertion<IdentifierTypeNodeAssertion, IdentifierTypeNode> {

        public IdentifierTypeNodeAssertion(IdentifierTypeNode actual) {
            super(actual, IdentifierTypeNodeAssertion.class);
        }

        public IdentifierTypeNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<ElementAccessNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getType()));
            return this;
        }

        public IdentifierTypeNodeAssertion isNoneSafe() {
            return truthinessAssert(
                    node -> "Type is not none safe",
                    TypeNode::isNoneSafe
            );
        }

        public IdentifierTypeNodeAssertion isNotNoneSafe() {
            return falsenessAssert(
                    node -> "Type is none safe",
                    TypeNode::isNoneSafe
            );
        }
    }

    public static class FunctionTypeNodeAssertion extends BaseAssertion<FunctionTypeNodeAssertion, FunctionTypeNode> {

        public FunctionTypeNodeAssertion(FunctionTypeNode actual) {
            super(actual, FunctionTypeNodeAssertion.class);
        }


        public FunctionTypeNodeAssertion hasParameterTypes(int expected) {
            return baseAssert("parameter count", n -> n.getParameterTypes().size(), expected);
        }

        public FunctionTypeNodeAssertion hasNoParameterTypes() {
            return hasParameterTypes(0);
        }

        public FunctionTypeNodeAssertion hasParameterTypeMatching(int index, NodeAssertionBuilder<TypeNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getParameterTypes().get(index)));
            return this;
        }

        public TypeNodeAssertion mapToInner() {
            return NodeAssertionFactory.create(actual.getInnerType());
        }
    }

    public static class ListTypeNodeAssertion extends BaseAssertion<ListTypeNodeAssertion, ListTypeNode> {

        public ListTypeNodeAssertion(ListTypeNode actual) {
            super(actual, ListTypeNodeAssertion.class);
        }

        public TypeNodeAssertion mapToInner() {
            return NodeAssertionFactory.create(actual.getInnerType());
        }
    }

}
