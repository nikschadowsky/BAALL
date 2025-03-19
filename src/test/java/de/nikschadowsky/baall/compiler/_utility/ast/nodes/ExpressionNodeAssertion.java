package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;

/**
 * @since 23.02.2025
 */
public class ExpressionNodeAssertion extends BaseAssertion<ExpressionNodeAssertion, ExpressionNode> {

    public ExpressionNodeAssertion(ExpressionNode actual) {
        super(actual, ExpressionNodeAssertion.class);
    }

    public ParenthesizedExpressionNodeAssertion isParenthesizedExpression() {
        myself.isInstanceOf(ParenthesizedExpressionNode.class);
        return NodeAssertionFactory.create((ParenthesizedExpressionNode) actual);
    }

    public FunctionCallNodeAssertion isFunctionCall() {
        myself.isInstanceOf(FunctionCallNode.class);
        return NodeAssertionFactory.create((FunctionCallNode) actual);
    }

    public PrefixOperationNodeAssertion isPrefixOperation() {
        myself.isInstanceOf(PrefixOperationNode.class);
        return NodeAssertionFactory.create((PrefixOperationNode) actual);
    }

    public BinaryExpressionNodeAssertion isBinaryExpression() {
        myself.isInstanceOf(BinaryExpressionNode.class);
        return NodeAssertionFactory.create((BinaryExpressionNode) actual);
    }

    public LiteralNodeAssertion.PrimitiveLiteralNodeAssertion isPrimitiveLiteral() {
        myself.isInstanceOf(PrimitiveLiteralNode.class);
        return NodeAssertionFactory.create((PrimitiveLiteralNode) actual);
    }

    public LiteralNodeAssertion.ArrayLiteralNodeAssertion isArrayLiteral() {
        myself.isInstanceOf(ArrayLiteralNode.class);
        return NodeAssertionFactory.create((ArrayLiteralNode) actual);
    }

    public LiteralNodeAssertion.StructDefinitionLiteralNodeAssertion isStructDefinitionLiteral() {
        myself.isInstanceOf(StructDefinitionLiteralNode.class);
        return NodeAssertionFactory.create(((StructDefinitionLiteralNode) actual));
    }

    public LiteralNodeAssertion.FunctionDefinitionNodeAssertion isFunctionDefinition() {
        myself.isInstanceOf(FunctionDefinitionNode.class);
        return NodeAssertionFactory.create(((FunctionDefinitionNode) actual));
    }

    public LiteralNodeAssertion.StructNoneLiteralNodeAssertion isStructNoneLiteral() {
        myself.isInstanceOf(StructNoneLiteralNode.class);
        return NodeAssertionFactory.create(((StructNoneLiteralNode) actual));
    }

    public UnaryExpressionNodeAssertion isUnaryExpression() {
        myself.isInstanceOf(UnaryExpressionNode.class);
        return NodeAssertionFactory.create((UnaryExpressionNode) actual);
    }

    public ElementAccessNodeAssertion isElementAccess() {
        myself.isInstanceOf(ElementAccessNode.class);
        return NodeAssertionFactory.create((ElementAccessNode) actual);
    }

    public static class ParenthesizedExpressionNodeAssertion
            extends BaseAssertion<ParenthesizedExpressionNodeAssertion, ParenthesizedExpressionNode> {

        public ParenthesizedExpressionNodeAssertion(ParenthesizedExpressionNode actual) {
            super(actual, ParenthesizedExpressionNodeAssertion.class);
        }

        public ExpressionNodeAssertion mapToInner() {
            return NodeAssertionFactory.create(actual.getInnerExpressionNode());
        }
    }

    public static class FunctionCallNodeAssertion extends BaseAssertion<FunctionCallNodeAssertion, FunctionCallNode> {

        public FunctionCallNodeAssertion(FunctionCallNode actual) {
            super(actual, FunctionCallNodeAssertion.class);
        }

        public FunctionCallNodeAssertion hasFunctionNameMatching(NodeAssertionBuilder<ElementAccessNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getFunctionIdentifier())
                                             .withFailMessage("Called function's name does not match"));
            return this;
        }

        public FunctionCallNodeAssertion hasArguments(int size) {
            return baseAssert("argument count", n -> n.getArguments().size(), size);
        }

        public FunctionCallNodeAssertion hasNoArguments() {
            return hasArguments(0);
        }

        public FunctionCallNodeAssertion hasArgumentMatching(int index, NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getArguments().get(index))
                                             .withFailMessage("Argument at %s does not match", index));
            return this;
        }
    }

    public static class PrefixOperationNodeAssertion extends BaseAssertion<PrefixOperationNodeAssertion, PrefixOperationNode> {

        public PrefixOperationNodeAssertion(PrefixOperationNode actual) {
            super(actual, PrefixOperationNodeAssertion.class);
        }

        public PrefixOperationNodeAssertion hasOperator(String expected) {
            NodeAssertionFactory.create(actual.getOperator())
                                .withFailMessage("Operator does not match")
                                .hasOperator(expected);
            return this;
        }

        public PrefixOperationNodeAssertion hasOperandMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getOperand()).withFailMessage("Operand does not match"));
            return this;
        }
    }

    public static class BinaryExpressionNodeAssertion extends BaseAssertion<BinaryExpressionNodeAssertion, BinaryExpressionNode> {

        public BinaryExpressionNodeAssertion(BinaryExpressionNode actual) {
            super(actual, BinaryExpressionNodeAssertion.class);
        }

        public BinaryExpressionNodeAssertion hasLeftOperandMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getLeftOperand())
                                             .withFailMessage("Left operand does not match"));
            return this;
        }

        public BinaryExpressionNodeAssertion hasRightOperandMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getRightOperand())
                                             .withFailMessage("Right operand does not match"));
            return this;
        }

        public BinaryExpressionNodeAssertion hasOperator(String expected) {
            return hasOperatorMatching(a -> a.hasOperator(expected));
        }

        public BinaryExpressionNodeAssertion hasOperatorMatching(NodeAssertionBuilder<OperatorNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getOperator()).withFailMessage("Operator does not match"));
            return this;
        }
    }

    public static class UnaryExpressionNodeAssertion extends BaseAssertion<UnaryExpressionNodeAssertion, UnaryExpressionNode> {

        public UnaryExpressionNodeAssertion(UnaryExpressionNode actual) {
            super(actual, UnaryExpressionNodeAssertion.class);
        }

        public UnaryExpressionNodeAssertion hasOperator(String expected) {
            return hasOperatorMatching(a -> a.hasOperator(expected));
        }

        public UnaryExpressionNodeAssertion hasOperatorMatching(NodeAssertionBuilder<OperatorNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getOperator()).withFailMessage("Operator does not match"));
            return this;
        }

        public UnaryExpressionNodeAssertion isPrefixOperation() {
            return truthinessAssert(
                    node -> "Operation is not prefix",
                    UnaryExpressionNode::isPrefix
            );
        }

        public UnaryExpressionNodeAssertion isPostfixOperation() {
            return falsenessAssert(
                    node -> "Operation is not postfix",
                    UnaryExpressionNode::isPrefix
            );
        }

        public ElementAccessNodeAssertion mapToInner() {
            return NodeAssertionFactory.create(actual.getElementAccess());
        }
    }
}
