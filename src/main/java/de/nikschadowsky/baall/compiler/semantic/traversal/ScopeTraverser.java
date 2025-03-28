package de.nikschadowsky.baall.compiler.semantic.traversal;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IndexedAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.MemberReferenceNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ScopeElevationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableReassignmentNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.BreakStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ContinueStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.FunctionTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.IdentifierTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.ListTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.SimpleTreeTraverser;

import java.util.HashMap;
import java.util.Map;

public class ScopeTraverser extends SimpleTreeTraverser<Scope, Boolean> {

    private final Map<Node, Scope> scopes = new HashMap<>();

    /**
     * @param that node
     * @param data initial scope
     * @return true
     * @implNote The program node is assigned the passed scope. Imports are assigned the same scope as the program node.
     * Every other child element of a program node operates in a child scope of the parents scope, but in the same
     * scope.
     */
    @Override
    public Boolean visitProgram(ProgramNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getImports(), data);
        scan(that.getStatements(), new Scope(data));
        scan(that.getExports(), new Scope(data));
        return true;
    }

    @Override
    public Boolean visitImports(ImportsNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    @Override
    public Boolean visitStatements(StatementsNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getStatements(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The set of the exported elements is at a deeper scope level than its parent.
     */
    @Override
    public Boolean visitExports(ExportsNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getExportedElements(), new Scope(data));
        that.getNamespace().ifPresent(n -> scan(n, new Scope(data)));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitConstantDeclaration(ConstantDeclarationNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getType(), data);
        scan(that.getIdentifier(), data);
        scan(that.getInitializationValue(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitVariableDeclaration(VariableDeclarationNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getType(), data);
        scan(that.getIdentifier(), data);
        that.getInitializationValue().ifPresent(i -> scan(i, data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitVariableReassignment(VariableReassignmentNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getElementAccess(), data);
        scan(that.getOperator(), data);
        scan(that.getValueExpression(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The condition of the conditional is in a scope deeper as the conditional itself. The body assigned an
     * even deeper scope. An optional else branch is in the same scope as this node.
     */
    @Override
    public Boolean visitConditional(ConditionalNode that, Scope data) {
        scopes.put(that, data);
        Scope conditionScope = new Scope(data);
        scan(that.getCondition(), conditionScope);
        scan(that.getThenBlock(), new Scope(conditionScope));
        that.getElseBranch().ifPresent(e -> scan(e, data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The header of this for loop defines a new scope for the identifier and each a new scope one level
     * deeper for the start index, end index and for the optional reassignment. The body of this for loop is also at a
     * scope level beneath the identifier, but cannot access potentially declared fields in the start or end index.
     */
    @Override
    public Boolean visitForLoop(ForLoopNode that, Scope data) {
        scopes.put(that, data);
        Scope forIdentifierScope = new Scope(data);

        scan(that.getIdentifier(), forIdentifierScope);
        scan(that.getStartIndexExpression(), new Scope(forIdentifierScope));
        scan(that.getEndIndexExpression(), new Scope(forIdentifierScope));
        that.getOptionalStepperStatement().ifPresent(e -> scan(e, new Scope(forIdentifierScope)));
        scan(that.getBody(), new Scope(forIdentifierScope));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The condition of this while loop is assigned a new deeper scope and this body one scope deeper.
     */
    @Override
    public Boolean visitWhileLoop(WhileLoopNode that, Scope data) {
        scopes.put(that, data);
        Scope whileConditionScope = new Scope(data);
        scan(that.getCondition(), whileConditionScope);
        scan(that.getBody(), new Scope(whileConditionScope));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitBinaryExpression(BinaryExpressionNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getLeftOperand(), data);
        scan(that.getOperator(), data);
        scan(that.getRightOperand(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote Inner expression of this node is at a deeper scope
     */
    @Override
    public Boolean visitParenthesizedExpression(ParenthesizedExpressionNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getInnerExpressionNode(), new Scope(data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitPrefixOperation(PrefixOperationNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getOperator(), data);
        scan(that.getOperand(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitUnaryExpression(UnaryExpressionNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getElementAccess(), data);
        scan(that.getOperator(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The body of this ensure node is at a deeper scope.
     */
    @Override
    public Boolean visitEnsureStatement(EnsureStatementNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getBody(), new Scope(data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The intercept header is at a deeper scope and its body at a level one deeper than that.
     */
    @Override
    public Boolean visitInterceptStatement(InterceptStatementNode that, Scope data) {
        scopes.put(that, data);
        Scope interceptHeaderScope = new Scope(data);
        scan(that.getInterceptedExceptions(), interceptHeaderScope);
        scan(that.getRaisedExceptionIdentifier(), interceptHeaderScope);
        scan(that.getBody(), new Scope(interceptHeaderScope));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitRaiseStatement(RaiseStatementNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getException(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The body of this try node is at a deeper scope level than the node itself. All intercepts and the
     * optional ensure is at the same scope level as this node is.
     */
    @Override
    public Boolean visitTryStatement(TryStatementNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getBody(), new Scope(data));
        scan(that.getInterceptBlocks(), data);
        that.getEnsureBlock().ifPresent(e -> scan(e, data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitReturnStatement(ReturnStatementNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getReturnExpression(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The list literal elements are at a deeper scope level.
     */
    @Override
    public Boolean visitListLiteral(ListLiteralNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getElements(), new Scope(data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The header of this function definition is in a deeper scope that the node itself. The body of this
     * function is at a deeper scope level than the header.
     */
    @Override
    public Boolean visitFunctionDefinition(FunctionDefinitionNode that, Scope data) {
        scopes.put(that, data);
        Scope functionHeaderScope = new Scope(data);
        scan(that.getParameters(), functionHeaderScope);
        scan(that.getFunctionBody(), new Scope(data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitNumberLiteral(NumberLiteralNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitStringLiteral(StringLiteralNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitBooleanLiteral(BooleanLiteralNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The field declarations are defined at a scope one level deeper than this node.
     */
    @Override
    public Boolean visitStructDefinitionLiteral(StructDefinitionLiteralNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getFields(), new Scope(data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitStructNoneLiteral(StructNoneLiteralNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote All passed arguments are at a deeper scope than this node.
     */
    @Override
    public Boolean visitFunctionCall(FunctionCallNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getFunctionIdentifier(), data);
        scan(that.getArguments(), new Scope(data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitField(FieldNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getType(), data);
        scan(that.getIdentifier(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitIdentifier(IdentifierNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitOperator(OperatorNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitBreakStatement(BreakStatementNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitContinueStatement(ContinueStatementNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The identifier of the function type is at the same scope as this function type. The parameter types is
     * in a scope one deeper.
     */
    @Override
    public Boolean visitFunctionType(FunctionTypeNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getInnerType(), data);
        scan(that.getParameterTypes(), new Scope(data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitListType(ListTypeNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getInnerType(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitIdentifierType(IdentifierTypeNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getType(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitPrimitiveType(PrimitiveTypeNode that, Scope data) {
        scopes.put(that, data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     * @implNote The index expression is at a deeper scope level than this node.
     */
    @Override
    public Boolean visitIndexedAccess(IndexedAccessNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getInnerIdentifier(), data);
        scan(that.getInnerIdentifier(), new Scope(data));
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitMemberReference(MemberReferenceNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getSelf(), data);
        scan(that.getInnerIdentifier(), data);
        return true;
    }

    /**
     * @param that node
     * @param data own scope
     * @return true
     */
    @Override
    public Boolean visitScopeElevation(ScopeElevationNode that, Scope data) {
        scopes.put(that, data);
        scan(that.getInnerIdentifier(), data);
        return true;
    }
}
