package de.nikschadowsky.baall.compiler.syntax.tree.traversal;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ControlStructureNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.LoopControlStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;

import java.util.Optional;

/**
 * @since 23.01.2025
 */
public class SimpleTreeTraverser<D, R> implements ASTVisitor<D, R> {

    @Override
    public Optional<R> visitProgram(ProgramNode that, D data) {
        R r = scan(that.getImports(), data);
        r = scanAndReduce(that.getStatements(), data, r);
        r = scanAndReduce(that.getExports(), data, r);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitImports(ImportsNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitStatements(StatementsNode that, D data) {
        R r = scan(that.getStatements(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitExports(ExportsNode that, D data) {
        R r = scan(that.getExportedElements(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitDeclaration(DeclarationNode that, D data) {
        R r = scan(that.getType(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitConstantDeclaration(ConstantDeclarationNode that, D data) {
        R r = scan(that.getType(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitVariableDeclaration(VariableDeclarationNode that, D data) {
        R r = scan(that.getType(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitReassignment(ReassignmentNode that, D data) {
        R r = scan(that.getIdentifierAccess(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitVariableReassignment(VariableReassignmentNode that, D data) {
        R r = scan(that.getIdentifierAccess(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitType(TypeNode that, D data) {
        R r = scan(that.getArrayDimensionDefinitions(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitControlStructure(ControlStructureNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitConditional(ConditionalNode that, D data) {
        R r = scan(that.getCondition(), data);
        r = scanAndReduce(that.getThenBlock(), data, r);
        if (that.getElseBranch().isPresent()) {
            r = scanAndReduce(that.getElseBranch().get(), data, r);
        }
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitForLoop(ForLoopNode that, D data) {
        R r = scan(that.getStartIndexExpression(), data);
        r = scanAndReduce(that.getEndIndexExpression(), data, r);
        if (that.getOptionalStepperStatement().isPresent()) {
            r = scanAndReduce(that.getOptionalStepperStatement().get(), data, r);
        }
        r = scanAndReduce(that.getBody(), data, r);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitWhileLoop(WhileLoopNode that, D data) {
        R r = scan(that.getCondition(), data);
        r = scanAndReduce(that.getBody(), data, r);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitExpression(ExpressionNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitBinaryExpression(BinaryExpressionNode that, D data) {
        R r = scan(that.getLeftOperand(), data);
        r = scanAndReduce(that.getRightOperand(), data, r);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitParenthesizedExpression(ParenthesizedExpressionNode that, D data) {
        R r = scan(that.getInnerExpressionNode(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitPrefixOperation(PrefixOperationNode that, D data) {
        R r = scan(that.getOperand(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitUnaryExpression(UnaryExpressionNode that, D data) {
        R r = scan(that.getIdentifierAccess(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitEnsure(EnsureStatementNode that, D data) {
        R r = scan(that.getBody(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitIntercept(InterceptStatementNode that, D data) {
        R r = scan(that.getInterceptedExceptions(), data);
        r = scanAndReduce(that.getBody(), data, r);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitRaise(RaiseStatementNode that, D data) {
        R r = scan(that.getException(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitTry(TryStatementNode that, D data) {
        R r = scan(that.getBody(), data);
        r = scanAndReduce(that.getInterceptBlocks(), data, r);
        if (that.getEnsureBlock().isPresent()) {
            r = scanAndReduce(that.getEnsureBlock().get(), data, r);
        }
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitControlStatement(ControlStatementNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitLoopControlStatement(LoopControlStatementNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitReturnStatement(ReturnStatementNode that, D data) {
        R r = scan(that.getReturnExpression(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitStatement(StatementNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitArrayLiteral(ArrayLiteralNode that, D data) {
        R r = scan(that.getElements(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitFunctionDefinition(FunctionDefinitionNode that, D data) {
        R r = scan(that.getParameters(), data);
        r = scanAndReduce(that.getFunctionBody(), data, r);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitPrimitiveLiteral(PrimitiveLiteralNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitStructDefinitionLiteral(StructDefinitionLiteralNode that, D data) {
        R r = scan(that.getFields(), data);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitStructInitializationLiteral(StructInitializationLiteralNode that, D data) {
        R r = scan(that.getIdentifier(), data);
        r = scanAndReduce(that.getArguments(), data, r);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitFunctionCall(FunctionCallNode that, D data) {
        R r = scan(that.getFunctionIdentifier(), data);
        r = scanAndReduce(that.getArguments(), data, r);
        return Optional.ofNullable(r);
    }

    @Override
    public Optional<R> visitIdentifierAccess(IdentifierAccessNode that, D data) {
        return Optional.ofNullable(scan(that.getArrayIndices(), data));
    }

    @Override
    public Optional<R> visitLiteral(LiteralNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitTerm(TermNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    //todo
    @Override
    public Optional<R> visitField(FieldNode that, D data) {
        R r = scan(that.getType(), data);
        return Optional.ofNullable(scanAndReduce(that.getIdentifier(), data, r));
    }

    @Override
    public Optional<R> visitIdentifier(IdentifierNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    @Override
    public Optional<R> visitOperator(OperatorNode that, D data) {
        // empty implementation
        return Optional.empty();
    }

    /*
    UTILITY
     */

    protected R scan(Node node, D data) {
        if (node != null) {
            return node.accept(this, data).orElse(null);
        }
        return null;
    }

    protected R scan(Iterable<? extends Node> nodes, D data) {
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

    protected R scanAndReduce(Node node, D data, R r) {
        return reduce(scan(node, data), r);
    }

    protected R scanAndReduce(Iterable<? extends Node> nodes, D data, R r) {
        return reduce(scan(nodes, data), r);
    }

    protected R reduce(R r1, R r2) {
        return r1;
    }

}
