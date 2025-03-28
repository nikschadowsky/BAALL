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

    public BooleanLiteralNodeAssertion isBooleanLiteral() {
        myself.isInstanceOf(BooleanLiteralNode.class);
        return NodeAssertionFactory.create((BooleanLiteralNode) actual);
    }

    public StringLiteralNodeAssertion isStringLiteral() {
        myself.isInstanceOf(StringLiteralNode.class);
        return NodeAssertionFactory.create((StringLiteralNode) actual);
    }

    public NumberLiteralNodeAssertion isNumberLiteral() {
        myself.isInstanceOf(NumberLiteralNode.class);
        return NodeAssertionFactory.create((NumberLiteralNode) actual);
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

    public static class BooleanLiteralNodeAssertion extends BaseAssertion<BooleanLiteralNodeAssertion, BooleanLiteralNode> {

        public BooleanLiteralNodeAssertion(BooleanLiteralNode actual) {
            super(actual, BooleanLiteralNodeAssertion.class);
        }

        public BooleanLiteralNodeAssertion hasValue(String expected) {
            return baseAssert("value", n -> n.getValue().value(), expected);
        }
    }

    public static class StringLiteralNodeAssertion extends BaseAssertion<StringLiteralNodeAssertion, StringLiteralNode> {

        public StringLiteralNodeAssertion(StringLiteralNode actual) {
            super(actual, StringLiteralNodeAssertion.class);
        }

        public StringLiteralNodeAssertion hasValue(String expected) {
            return baseAssert("value", n -> n.getValue().value(), expected);
        }
    }

    public static class NumberLiteralNodeAssertion extends BaseAssertion<NumberLiteralNodeAssertion, NumberLiteralNode> {

        public NumberLiteralNodeAssertion(NumberLiteralNode actual) {
            super(actual, NumberLiteralNodeAssertion.class);
        }

        public NumberLiteralNodeAssertion hasValue(String expected) {
            return baseAssert("value", n -> n.getValue().value(), expected);
        }
    }

}
