package de.nikschadowsky.baall.compiler._utility.ast.nodes;


import de.nikschadowsky.baall.compiler._utility.BaseAssertion;
import de.nikschadowsky.baall.compiler._utility.ast.NodeAssertionFactory;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.UnaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.BreakStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ContinueStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;

/**
 * @since 25.02.2025
 */
public class StatementNodeAssertion extends BaseAssertion<StatementNodeAssertion, StatementNode> {

    public StatementNodeAssertion(StatementNode actual) {
        super(actual, StatementNodeAssertion.class);
    }

    public VariableDeclarationNodeAssertion isVariableDeclaration() {
        isInstanceOf(VariableDeclarationNode.class);
        return NodeAssertionFactory.create((VariableDeclarationNode) actual);
    }

    public ConstantDeclarationNodeAssertion isConstantDeclaration() {
        isInstanceOf(ConstantDeclarationNode.class);
        return NodeAssertionFactory.create((ConstantDeclarationNode) actual);
    }

    public VariableReassignmentNodeAssertion isVariableReassignment() {
        isInstanceOf(VariableReassignmentNode.class);
        return NodeAssertionFactory.create((VariableReassignmentNode) actual);
    }

    public ExpressionNodeAssertion.UnaryExpressionNodeAssertion isUnaryExpression() {
        isInstanceOf(UnaryExpressionNode.class);
        return NodeAssertionFactory.create((UnaryExpressionNode) actual);
    }

    public ExpressionNodeAssertion.FunctionCallNodeAssertion isFunctionCall() {
        isInstanceOf(FunctionCallNode.class);
        return NodeAssertionFactory.create((FunctionCallNode) actual);
    }

    public ForLoopNodeAssertion isForLoop() {
        isInstanceOf(ForLoopNode.class);
        return NodeAssertionFactory.create((ForLoopNode) actual);
    }

    public WhileLoopNodeAssertion isWhileLoop() {
        isInstanceOf(WhileLoopNode.class);
        return NodeAssertionFactory.create((WhileLoopNode) actual);
    }

    public ConditionalNodeAssertion isConditional() {
        isInstanceOf(ConditionalNode.class);
        return NodeAssertionFactory.create((ConditionalNode) actual);
    }

    public TryStatementNodeAssertion isTryStatement() {
        isInstanceOf(TryStatementNode.class);
        return NodeAssertionFactory.create((TryStatementNode) actual);
    }

    public StatementNodeAssertion isBreakStatement() {
        return isInstanceOf(BreakStatementNode.class);
    }

    public StatementNodeAssertion isContinueStatement() {
        return isInstanceOf(ContinueStatementNode.class);
    }

    public StatementNodeAssertion.ReturnStatementNodeAssertion isReturnStatement() {
        isInstanceOf(ReturnStatementNode.class);
        return NodeAssertionFactory.create((ReturnStatementNode) actual);
    }

    public StatementNodeAssertion.RaiseStatementNodeAssertion isRaiseStatement() {
        isInstanceOf(RaiseStatementNode.class);
        return NodeAssertionFactory.create((RaiseStatementNode) actual);
    }

    // todo useful?
    public static class ReassignmentNodeAssertion extends BaseAssertion<ReassignmentNodeAssertion, ReassignmentNode> {

        public ReassignmentNodeAssertion(ReassignmentNode actual) {
            super(actual, ReassignmentNodeAssertion.class);
        }

        public StatementNodeAssertion toStatementAssertion() {
            return NodeAssertionFactory.create((StatementNode) actual);
        }
    }

    // todo useful?
    public static class DeclarationNodeAssertion extends BaseAssertion<DeclarationNodeAssertion, DeclarationNode> {

        public DeclarationNodeAssertion(DeclarationNode actual) {
            super(actual, DeclarationNodeAssertion.class);
        }

        public StatementNodeAssertion toStatementAssertion() {
            return NodeAssertionFactory.create((StatementNode) actual);
        }
    }

    public static class VariableDeclarationNodeAssertion extends BaseAssertion<VariableDeclarationNodeAssertion, VariableDeclarationNode> {

        public VariableDeclarationNodeAssertion(VariableDeclarationNode actual) {
            super(actual, VariableDeclarationNodeAssertion.class);
        }

        public VariableDeclarationNodeAssertion hasTypeMatching(NodeAssertionBuilder<TypeNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getType()).withFailMessage("Type does not match"));
            return this;
        }

        public VariableDeclarationNodeAssertion hasIdentifierName(String identifier) {
            return hasIdentifierMatching(a -> a.hasName(identifier));
        }

        public VariableDeclarationNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<IdentifierNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getIdentifier())
                                             .withFailMessage("Identifier does not match"));
            return this;
        }

        public VariableDeclarationNodeAssertion hasInitializationValue() {
            return truthinessAssert(
                    node -> "Variable does not have an initialization value",
                    node -> node.getInitializationValue().isPresent()
            );
        }

        public VariableDeclarationNodeAssertion hasNoInitializationValue() {
            return truthinessAssert(
                    node -> "Variable has an initialization value",
                    node -> node.getInitializationValue().isEmpty()
            );
        }

        public VariableDeclarationNodeAssertion hasInitializationValueMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getInitializationValue().orElseThrow())
                                             .withFailMessage("Initialization value does not match"));
            return this;
        }
    }

    public static class ConstantDeclarationNodeAssertion extends BaseAssertion<ConstantDeclarationNodeAssertion, ConstantDeclarationNode> {

        public ConstantDeclarationNodeAssertion(ConstantDeclarationNode actual) {
            super(actual, ConstantDeclarationNodeAssertion.class);
        }

        public ConstantDeclarationNodeAssertion hasTypeMatching(NodeAssertionBuilder<TypeNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getType()).withFailMessage("Type does not match"));
            return this;
        }

        public ConstantDeclarationNodeAssertion hasIdentifierName(String identifier) {
            return hasIdentifierMatching(a -> a.hasName(identifier));
        }

        public ConstantDeclarationNodeAssertion hasIdentifierMatching(NodeAssertionBuilder<IdentifierNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getIdentifier())
                                             .withFailMessage("Identifier does not match"));
            return this;
        }

        public ConstantDeclarationNodeAssertion hasInitializationValueMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getInitializationValue())
                                             .withFailMessage("Initialization value does not match"));
            return this;
        }
    }

    public static class VariableReassignmentNodeAssertion extends BaseAssertion<VariableReassignmentNodeAssertion, VariableReassignmentNode> {

        public VariableReassignmentNodeAssertion(VariableReassignmentNode actual) {
            super(actual, VariableReassignmentNodeAssertion.class);
        }

        public VariableReassignmentNodeAssertion hasElementAccessMatching(NodeAssertionBuilder<ElementAccessNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getElementAccess())
                                             .withFailMessage("Element access does not match"));
            return this;
        }

        public VariableReassignmentNodeAssertion hasOperator(String operator) {
            return hasOperatorMatching(a -> a.hasOperator(operator));
        }

        public VariableReassignmentNodeAssertion hasOperatorMatching(NodeAssertionBuilder<OperatorNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getOperator()).withFailMessage("Operator does not match"));
            return this;
        }

        public VariableReassignmentNodeAssertion hasValueMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getValueExpression())
                                             .withFailMessage("Value does not match"));
            return this;
        }
    }

    public static class RaiseStatementNodeAssertion extends BaseAssertion<RaiseStatementNodeAssertion, RaiseStatementNode> {

        public RaiseStatementNodeAssertion(RaiseStatementNode actual) {
            super(actual, RaiseStatementNodeAssertion.class);
        }


        public RaiseStatementNodeAssertion hasExceptionCall(NodeAssertionBuilder<ExpressionNodeAssertion.FunctionCallNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getException())
                                             .withFailMessage("Exception call does not match"));
            return this;
        }

    }

    public static class ReturnStatementNodeAssertion extends BaseAssertion<ReturnStatementNodeAssertion, ReturnStatementNode> {

        public ReturnStatementNodeAssertion(ReturnStatementNode actual) {
            super(actual, ReturnStatementNodeAssertion.class);
        }

        public ReturnStatementNodeAssertion hasExpressionMatching(NodeAssertionBuilder<ExpressionNodeAssertion> a) {
            a.assertThat(NodeAssertionFactory.create(actual.getReturnExpression()));
            return this;
        }
    }
}
