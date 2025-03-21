package de.nikschadowsky.baall.compiler.semantic.attribute;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.BinaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ParenthesizedExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.PrefixOperationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.UnaryExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.FunctionDefinitionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.ListLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.PrimitiveLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.StructDefinitionLiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.SimpleTreeTraverser;

import java.util.*;

/**
 * @since 26.01.2025
 */
public class TreeTraverserFactory {

    private TreeTraverserFactory() {
    }

    public static FunctionDeclarationAndScoreTraverser newFunctionDeclarationAndScoreTraverser() {
        return new FunctionDeclarationAndScoreTraverser();
    }

    // todo revise!!!
    public static class FunctionDeclarationAndScoreTraverser extends SimpleTreeTraverser<Scope, Boolean> {

        private final Map<Node, Scope> scopes = new HashMap<>();
        private final Set<FunctionDefinitionNode> functions = new HashSet<>();

        private FunctionDeclarationAndScoreTraverser() {
        }

        public void reset() {
            scopes.clear();
            functions.clear();
        }

        public Map<Node, Scope> getScopes() {
            return Collections.unmodifiableMap(scopes);
        }

        public Set<FunctionDefinitionNode> getFunctions() {
            return Collections.unmodifiableSet(functions);
        }

        @Override
        public Boolean visitProgram(ProgramNode that, Scope scope) {
            scopes.put(that, scope);

            scan(that.getImports(), scope);
            Scope bodyScope = new Scope(scope);
            scan(that.getStatements(), bodyScope);
            scan(that.getExports(), bodyScope);

            return true;
        }

        @Override
        public Boolean visitImports(ImportsNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitImports(that, scope);
        }

        @Override
        public Boolean visitStatements(StatementsNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitStatements(that, scope);
        }

        @Override
        public Boolean visitExports(ExportsNode that, Scope scope) {
            scopes.put(that, scope);
            scan(that.getExportedElements(), new Scope(scope));
            return true;
        }

        @Override
        public Boolean visitConstantDeclaration(ConstantDeclarationNode that, Scope scope) {
            scopes.put(that, scope);
            // todo functions need to be registered here to have their identifiers registered

            if (that.getInitializationValue() instanceof FunctionDefinitionNode function) {
                functions.add(function);
            }

            return super.visitConstantDeclaration(that, scope);
        }

        @Override
        public Boolean visitVariableDeclaration(VariableDeclarationNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitVariableDeclaration(that, scope);
        }

        @Override
        public Boolean visitVariableReassignment(VariableReassignmentNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitVariableReassignment(that, scope);
        }

        @Override
        public Boolean visitConditional(ConditionalNode that, Scope scope) {
            scopes.put(that, scope);
            Scope conditionScope = new Scope(scope);
            scan(that.getCondition(), conditionScope);
            scan(that.getThenBlock(), new Scope(conditionScope));
            scan(that.getElseBranch().orElse(null), scope);
            return true;
        }

        @Override
        public Boolean visitForLoop(ForLoopNode that, Scope scope) {
            scopes.put(that, scope);
            Scope forScope = new Scope(scope);
            scan(that.getIdentifier(), forScope);
            scan(that.getStartIndexExpression(), forScope);
            scan(that.getEndIndexExpression(), forScope);
            scan(that.getOptionalStepperStatement().orElse(null), forScope);
            // body is an inner scope to prevent access to functions declared in body in the for header
            scan(that.getBody(), new Scope(forScope));

            return true;
        }

        @Override
        public Boolean visitWhileLoop(WhileLoopNode that, Scope scope) {
            scopes.put(that, scope);
            Scope conditionScope = new Scope(scope);
            scan(that.getCondition(), conditionScope);
            // body is an inner scope to prevent access to functions declared in body in the while header
            scan(that.getBody(), new Scope(conditionScope));

            return true;
        }

        @Override
        public Boolean visitBinaryExpression(BinaryExpressionNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitBinaryExpression(that, scope);
        }

        @Override
        public Boolean visitParenthesizedExpression(ParenthesizedExpressionNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitParenthesizedExpression(that, scope);
        }

        @Override
        public Boolean visitPrefixOperation(PrefixOperationNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitPrefixOperation(that, scope);
        }

        @Override
        public Boolean visitUnaryExpression(UnaryExpressionNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitUnaryExpression(that, scope);
        }

        @Override
        public Boolean visitEnsureStatement(EnsureStatementNode that, Scope scope) {
            scopes.put(that, scope);
            scan(that.getBody(), new Scope(scope));
            return true;
        }

        @Override
        public Boolean visitInterceptStatement(InterceptStatementNode that, Scope scope) {
            scopes.put(that, scope);
            Scope interceptedScope = new Scope(scope);
            scan(that.getInterceptedExceptions(), interceptedScope);
            // body should access raised exception but not vice versa
            scan(that.getBody(), new Scope(interceptedScope));
            return true;
        }

        @Override
        public Boolean visitRaiseStatement(RaiseStatementNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitRaiseStatement(that, scope);
        }

        @Override
        public Boolean visitTryStatement(TryStatementNode that, Scope scope) {
            scopes.put(that, scope);
            scan(that.getBody(), new Scope(scope));
            return true;
        }

        @Override
        public Boolean visitListLiteral(ListLiteralNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitListLiteral(that, scope);
        }

        @Override
        public Boolean visitFunctionDefinition(FunctionDefinitionNode that, Scope scope) {
            scopes.put(that, scope);
            functions.add(that);
            Scope parameterScope = new Scope(scope);
            scan(that.getParameters(), parameterScope);
            scan(that.getFunctionBody(), new Scope(parameterScope));

            return true;
        }

        @Override
        public Boolean visitPrimitiveLiteral(PrimitiveLiteralNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitPrimitiveLiteral(that, scope);
        }

        @Override
        public Boolean visitStructDefinitionLiteral(StructDefinitionLiteralNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitStructDefinitionLiteral(that, scope);
        }

        @Override
        public Boolean visitFunctionCall(FunctionCallNode that, Scope scope) {
            scopes.put(that, scope);
            return super.visitFunctionCall(that, scope);
        }

    }
}
