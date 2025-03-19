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

    public NestedTypeNodeAssertion isNestedType() {
        myself.isInstanceOf(NestedTypeNode.class);
        return NodeAssertionFactory.create((NestedTypeNode) actual);
    }

    public static class PrimitiveTypeNodeAssertion extends BaseAssertion<PrimitiveTypeNodeAssertion, PrimitiveTypeNode> {

        public PrimitiveTypeNodeAssertion(PrimitiveTypeNode actual) {
            super(actual, PrimitiveTypeNodeAssertion.class);
        }

        public PrimitiveTypeNodeAssertion hasIdentifier(String identifier) {
            return hasIdentifierMatching(a -> a.isIdentifier().hasName(identifier));
        }

        public PrimitiveTypeNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<ElementAccessNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getType())
                                             .withFailMessage("Primitive identifier does not match"));
            return this;
        }

    }

    public static class IdentifierTypeNodeAssertion extends BaseAssertion<IdentifierTypeNodeAssertion, IdentifierTypeNode> {

        public IdentifierTypeNodeAssertion(IdentifierTypeNode actual) {
            super(actual, IdentifierTypeNodeAssertion.class);
        }

        public IdentifierTypeNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<ElementAccessNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getType()).withFailMessage("Identifier does not match"));
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
            a.assertThat(NodeAssertionFactory.create(actual.getParameterTypes().get(index))
                                             .withFailMessage("Parameter type does not match"));
            return this;
        }

        public PrimitiveTypeNodeAssertion isInnerPrimitiveType() {
            assertThat(actual.getInnerType()).isInstanceOf(PrimitiveTypeNode.class);
            return NodeAssertionFactory.create((PrimitiveTypeNode) actual.getInnerType());
        }

        public IdentifierTypeNodeAssertion isInnerIdentifierType() {
            assertThat(actual.getInnerType()).isInstanceOf(IdentifierTypeNode.class);
            return NodeAssertionFactory.create((IdentifierTypeNode) actual.getInnerType());
        }

        public FunctionTypeNodeAssertion isInnerFunctionType() {
            assertThat(actual.getInnerType()).isInstanceOf(FunctionTypeNode.class);
            return NodeAssertionFactory.create((FunctionTypeNode) actual.getInnerType());
        }

        public NestedTypeNodeAssertion isInnerNestedType() {
            assertThat(actual.getInnerType()).isInstanceOf(NestedTypeNode.class);
            return NodeAssertionFactory.create((NestedTypeNode) actual.getInnerType());
        }
    }

    public static class NestedTypeNodeAssertion extends BaseAssertion<NestedTypeNodeAssertion, NestedTypeNode> {

        public NestedTypeNodeAssertion(NestedTypeNode actual) {
            super(actual, NestedTypeNodeAssertion.class);
        }

        public NestedTypeNodeAssertion doesNotHaveArrayDimension() {
            return truthinessAssert(
                    node -> "Node does have array dimension",
                    node -> node.getArrayDimension().isEmpty()
            );
        }

        public NestedTypeNodeAssertion hasArrayDimension() {
            return truthinessAssert(
                    node -> "Node does not have array dimension",
                    node -> node.getArrayDimension().isPresent()
            );
        }

        public NestedTypeNodeAssertion hasArrayDimensionMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getArrayDimension().orElseThrow()));
            return this;
        }

        public PrimitiveTypeNodeAssertion isInnerPrimitiveType() {
            assertThat(actual.getInnerType()).isInstanceOf(PrimitiveTypeNode.class);
            return NodeAssertionFactory.create((PrimitiveTypeNode) actual.getInnerType());
        }

        public IdentifierTypeNodeAssertion isInnerIdentifierType() {
            assertThat(actual.getInnerType()).isInstanceOf(IdentifierTypeNode.class);
            return NodeAssertionFactory.create((IdentifierTypeNode) actual.getInnerType());
        }

        public FunctionTypeNodeAssertion isInnerFunctionType() {
            assertThat(actual.getInnerType()).isInstanceOf(FunctionTypeNode.class);
            return NodeAssertionFactory.create((FunctionTypeNode) actual.getInnerType());
        }

        public NestedTypeNodeAssertion isInnerNestedType() {
            assertThat(actual.getInnerType()).isInstanceOf(NestedTypeNode.class);
            return NodeAssertionFactory.create((NestedTypeNode) actual.getInnerType());
        }
    }

}
