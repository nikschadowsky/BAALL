package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;

/**
 * @since 23.02.2025
 */
public class LiteralNodeAssertion extends BaseAssertion<LiteralNodeAssertion, LiteralNode> {

    public LiteralNodeAssertion(LiteralNode actual) {
        super(actual, LiteralNodeAssertion.class);
    }

    public PrimitiveLiteralNodeAssertion isPrimitiveLiteral() {
        myself.isInstanceOf(PrimitiveLiteralNode.class);
        return NodeAssertionFactory.create((PrimitiveLiteralNode) actual);
    }

    public ArrayLiteralNodeAssertion isArrayLiteral() {
        myself.isInstanceOf(ListLiteralNode.class);
        return NodeAssertionFactory.create((ListLiteralNode) actual);
    }

    public StructDefinitionLiteralNodeAssertion isStructDefinitionLiteral() {
        myself.isInstanceOf(StructDefinitionLiteralNode.class);
        return NodeAssertionFactory.create(((StructDefinitionLiteralNode) actual));
    }

    public FunctionDefinitionNodeAssertion isFunctionDefinition() {
        myself.isInstanceOf(FunctionDefinitionNode.class);
        return NodeAssertionFactory.create(((FunctionDefinitionNode) actual));
    }

    public StructNoneLiteralNodeAssertion isStructNoneLiteral() {
        myself.isInstanceOf(StructNoneLiteralNode.class);
        return NodeAssertionFactory.create(((StructNoneLiteralNode) actual));
    }

    public static class PrimitiveLiteralNodeAssertion extends BaseAssertion<PrimitiveLiteralNodeAssertion, PrimitiveLiteralNode> {

        public PrimitiveLiteralNodeAssertion(PrimitiveLiteralNode actual) {
            super(actual, PrimitiveLiteralNodeAssertion.class);
        }

        public PrimitiveLiteralNodeAssertion isNumber() {
            return baseAssert(
                    "primitive type",
                    PrimitiveLiteralNode::getPrimitiveType,
                    PrimitiveLiteralNode.PrimitiveType.NUMBER
            );
        }

        public PrimitiveLiteralNodeAssertion isBoolean() {
            return baseAssert(
                    "primitive type",
                    PrimitiveLiteralNode::getPrimitiveType,
                    PrimitiveLiteralNode.PrimitiveType.BOOLEAN
            );
        }

        public PrimitiveLiteralNodeAssertion isString() {
            return baseAssert(
                    "primitive type",
                    PrimitiveLiteralNode::getPrimitiveType,
                    PrimitiveLiteralNode.PrimitiveType.STRING
            );
        }

        public PrimitiveLiteralNodeAssertion hasPrimitiveValue(String expected) {
            return baseAssert("primitive value", node -> node.getPrimitiveValue().value(), expected);
        }
    }

    public static class ArrayLiteralNodeAssertion extends BaseAssertion<ArrayLiteralNodeAssertion, ListLiteralNode> {

        public ArrayLiteralNodeAssertion(ListLiteralNode actual) {
            super(actual, ArrayLiteralNodeAssertion.class);
        }

        public ArrayLiteralNodeAssertion hasElements(int expectedSize) {
            return baseAssert(
                    "Array element count",
                    node -> node.getElements().size(),
                    expectedSize
            );
        }

        public ArrayLiteralNodeAssertion hasNoElements() {
            return hasElements(0);
        }

        public ArrayLiteralNodeAssertion hasElementMatching(int index, NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getElements().get(index)));
            return this;
        }
    }

    public static class StructDefinitionLiteralNodeAssertion extends BaseAssertion<StructDefinitionLiteralNodeAssertion, StructDefinitionLiteralNode> {

        public StructDefinitionLiteralNodeAssertion(StructDefinitionLiteralNode actual) {
            super(actual, StructDefinitionLiteralNodeAssertion.class);
        }

        public StructDefinitionLiteralNodeAssertion hasFields(int expected) {
            return baseAssert(
                    "field count",
                    node -> node.getFields().size(),
                    expected
            );
        }

        public StructDefinitionLiteralNodeAssertion hasNoFields() {
            return hasFields(0);
        }

        public StructDefinitionLiteralNodeAssertion hasFieldNameMatching(int index, NodeAssertionBuilder<ElementAccessNodeAssertion.IdentifierNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getFields().get(index).getIdentifier()));
            return this;
        }

        public StructDefinitionLiteralNodeAssertion hasFieldName(int index, String fieldName) {
            return hasFieldNameMatching(index, a -> a.hasName(fieldName));
        }

        public StructDefinitionLiteralNodeAssertion hasFieldTypeMatching(int index, NodeAssertionBuilder<TypeNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getFields().get(index).getType()));
            return this;
        }
    }

    public static class FunctionDefinitionNodeAssertion extends BaseAssertion<FunctionDefinitionNodeAssertion, FunctionDefinitionNode> {

        public FunctionDefinitionNodeAssertion(FunctionDefinitionNode actual) {
            super(actual, FunctionDefinitionNodeAssertion.class);
        }

        public FunctionDefinitionNodeAssertion hasParameters(int expected) {
            return baseAssert(
                    "parameter count",
                    node -> node.getParameters().size(),
                    expected
            );
        }

        public FunctionDefinitionNodeAssertion hasNoParameters() {
            return hasParameters(0);
        }

        public FunctionDefinitionNodeAssertion hasParameterNameMatching(int index, NodeAssertionBuilder<ElementAccessNodeAssertion.IdentifierNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getParameters().get(index).getIdentifier()));
            return this;
        }

        public FunctionDefinitionNodeAssertion hasParameterName(int index, String expected) {
            return hasParameterNameMatching(index, a -> a.hasName(expected));
        }

        public FunctionDefinitionNodeAssertion hasParameterTypeMatching(int index, NodeAssertionBuilder<TypeNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getParameters().get(index).getType()));
            return this;
        }

        public FunctionDefinitionNodeAssertion hasBodyMatching(NodeAssertionBuilder<StatementsNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getFunctionBody()));
            return this;
        }
    }

    public static class StructNoneLiteralNodeAssertion extends BaseAssertion<StructNoneLiteralNodeAssertion, StructNoneLiteralNode> {

        public StructNoneLiteralNodeAssertion(StructNoneLiteralNode actual) {
            super(actual, StructNoneLiteralNodeAssertion.class);
        }
    }

}
