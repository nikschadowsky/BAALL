package de.nikschadowsky.baall.compiler.syntax.tree.traversal;


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

/**
 * @since 23.01.2025
 */
public class SimpleTreeTraverser<D, R> implements ASTVisitor<D, R> {

    @Override
    public R visitProgram(ProgramNode that, D data) {
        R r = scan(that.getImports(), data);
        r = scanAndReduce(that.getStatements(), data, r);
        r = scanAndReduce(that.getExports(), data, r);
        return r;
    }

    @Override
    public R visitImports(ImportsNode that, D data) {
        // empty implementation
        return null;
    }

    @Override
    public R visitStatements(StatementsNode that, D data) {
        return scan(that.getStatements(), data);
    }

    @Override
    public R visitExports(ExportsNode that, D data) {
        return scan(that.getExportedElements(), data);
    }

    @Override
    public R visitConstantDeclaration(ConstantDeclarationNode that, D data) {
        R r = scan(that.getType(), data);
        r = scanAndReduce(that.getIdentifier(), data, r);
        return scanAndReduce(that.getInitializationValue(), data, r);
    }

    @Override
    public R visitVariableDeclaration(VariableDeclarationNode that, D data) {
        return scan(that.getType(), data);
    }


    @Override
    public R visitVariableReassignment(VariableReassignmentNode that, D data) {
        return scan(that.getElementAccess(), data);
    }

    @Override
    public R visitConditional(ConditionalNode that, D data) {
        R r = scan(that.getCondition(), data);
        r = scanAndReduce(that.getThenBlock(), data, r);
        if (that.getElseBranch().isPresent()) {
            r = scanAndReduce(that.getElseBranch().get(), data, r);
        }
        return r;
    }

    @Override
    public R visitForLoop(ForLoopNode that, D data) {
        R r = scan(that.getStartIndexExpression(), data);
        r = scanAndReduce(that.getEndIndexExpression(), data, r);
        if (that.getOptionalStepperStatement().isPresent()) {
            r = scanAndReduce(that.getOptionalStepperStatement().get(), data, r);
        }
        return scanAndReduce(that.getBody(), data, r);
    }

    @Override
    public R visitWhileLoop(WhileLoopNode that, D data) {
        R r = scan(that.getCondition(), data);
        return scanAndReduce(that.getBody(), data, r);
    }

    @Override
    public R visitBinaryExpression(BinaryExpressionNode that, D data) {
        R r = scan(that.getLeftOperand(), data);
        return scanAndReduce(that.getRightOperand(), data, r);
    }

    @Override
    public R visitParenthesizedExpression(ParenthesizedExpressionNode that, D data) {
        return scan(that.getInnerExpressionNode(), data);
    }

    @Override
    public R visitPrefixOperation(PrefixOperationNode that, D data) {
        return scan(that.getOperand(), data);
    }

    @Override
    public R visitUnaryExpression(UnaryExpressionNode that, D data) {
        return scan(that.getElementAccess(), data);
    }

    @Override
    public R visitEnsureStatement(EnsureStatementNode that, D data) {
        return scan(that.getBody(), data);
    }

    @Override
    public R visitInterceptStatement(InterceptStatementNode that, D data) {
        R r = scan(that.getInterceptedExceptions(), data);
        return scanAndReduce(that.getBody(), data, r);
    }

    @Override
    public R visitRaiseStatement(RaiseStatementNode that, D data) {
        return scan(that.getException(), data);
    }

    @Override
    public R visitTryStatement(TryStatementNode that, D data) {
        R r = scan(that.getBody(), data);
        r = scanAndReduce(that.getInterceptBlocks(), data, r);
        if (that.getEnsureBlock().isPresent()) {
            r = scanAndReduce(that.getEnsureBlock().get(), data, r);
        }
        return r;
    }

    @Override
    public R visitReturnStatement(ReturnStatementNode that, D data) {
        return scan(that.getReturnExpression(), data);
    }

    @Override
    public R visitListLiteral(ListLiteralNode that, D data) {
        return scan(that.getElements(), data);
    }

    @Override
    public R visitFunctionDefinition(FunctionDefinitionNode that, D data) {
        R r = scan(that.getParameters(), data);
        return scanAndReduce(that.getFunctionBody(), data, r);
    }

    @Override
    public R visitPrimitiveLiteral(PrimitiveLiteralNode that, D data) {
        // empty implementation
        return null;
    }

    @Override
    public R visitStructDefinitionLiteral(StructDefinitionLiteralNode that, D data) {
        return scan(that.getFields(), data);
    }

    @Override
    public R visitStructNoneLiteral(StructNoneLiteralNode that, D data) {
        // empty implementation
        return null;
    }

    @Override
    public R visitFunctionCall(FunctionCallNode that, D data) {
        R r = scan(that.getFunctionIdentifier(), data);
        return scanAndReduce(that.getArguments(), data, r);
    }

    @Override
    public R visitField(FieldNode that, D data) {
        R r = scan(that.getType(), data);
        return scanAndReduce(that.getIdentifier(), data, r);
    }

    @Override
    public R visitIdentifier(IdentifierNode that, D data) {
        // empty implementation
        return null;
    }

    @Override
    public R visitOperator(OperatorNode that, D data) {
        // empty implementation
        return null;
    }

    @Override
    public R visitBreakStatement(BreakStatementNode that, D data) {
        // empty implementation
        return null;
    }

    @Override
    public R visitContinueStatement(ContinueStatementNode that, D data) {
        // empty implementation
        return null;
    }

    @Override
    public R visitFunctionType(FunctionTypeNode that, D data) {
        R r = scan(that.getParameterTypes(), data);
        r = scanAndReduce(that.getInnerType(), data, r);
        return r;
    }

    @Override
    public R visitListType(ListTypeNode that, D data) {
        return scan(that.getInnerType(), data);
    }

    @Override
    public R visitIdentifierType(IdentifierTypeNode that, D data) {
        return scan(that.getType(), data);
    }

    @Override
    public R visitPrimitiveType(PrimitiveTypeNode that, D data) {
        return null;
    }

    @Override
    public R visitIndexedAccess(IndexedAccessNode that, D data) {
        R r = scan(that.getIndex(), data);
        r = scanAndReduce(that.getInnerIdentifier(), data, r);
        return r;
    }

    @Override
    public R visitMemberReference(MemberReferenceNode that, D data) {
        R r = scan(that.getSelf(), data);
        r = scanAndReduce(that.getInnerIdentifier(), data, r);
        return r;
    }

    @Override
    public R visitScopeElevation(ScopeElevationNode that, D data) {
        return scan(that.getInnerIdentifier(), data);
    }

    /*
    UTILITY
     */

    protected final R scan(Node node, D data) {
        if (node != null) {
            return node.accept(this, data);
        }
        return null;
    }

    protected final R scan(Iterable<? extends Node> nodes, D data) {
        R r = null;
        if (nodes != null) {
            boolean first = true;
            for (Node node : nodes) {
                r = (first ? scan(node, data) : scanAndReduce(node, data, r));
                first = false;
            }
        }
        return r;
    }

    protected final R scanAndReduce(Node node, D data, R r) {
        return reduce(scan(node, data), r);
    }

    protected final R scanAndReduce(Iterable<? extends Node> nodes, D data, R r) {
        return reduce(scan(nodes, data), r);
    }

    protected final R reduce(R r1, R r2) {
        return r1;
    }

}
