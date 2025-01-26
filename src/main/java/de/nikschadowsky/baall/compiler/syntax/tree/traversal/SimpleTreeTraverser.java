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
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.IdentifierAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.TermNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;

/**
 * @since 23.01.2025
 */
public class SimpleTreeTraverser implements ASTVisitor {

    @Override
    public void visitProgram(ProgramNode that) {
        scan(that.getImports());
        scan(that.getStatements());
        scan(that.getExports());
    }

    @Override
    public void visitImports(ImportsNode that) {
        // empty implementation
    }

    @Override
    public void visitStatements(StatementsNode that) {
        scan(that.getStatements());
    }

    @Override
    public void visitExports(ExportsNode that) {
        scan(that.getExportedElements());
    }

    @Override
    public void visitDeclaration(DeclarationNode that) {
        scan(that.getType());
    }

    @Override
    public void visitConstantDeclaration(ConstantDeclarationNode that) {
        scan(that.getType());
    }

    @Override
    public void visitVariableDeclaration(VariableDeclarationNode that) {
        scan(that.getType());
    }

    @Override
    public void visitReassignment(ReassignmentNode that) {
        scan(that.getIdentifierAccess());
    }

    @Override
    public void visitVariableReassignment(VariableReassignmentNode that) {
        scan(that.getIdentifierAccess());
    }

    @Override
    public void visitType(TypeNode that) {
        scan(that.getArrayDimensionDefinitions());
    }

    @Override
    public void visitControlStructure(ControlStructureNode that) {
        // empty implementation
    }

    @Override
    public void visitConditional(ConditionalNode that) {
        scan(that.getCondition());
        scan(that.getThenBlock());
        that.getElseBranch().ifPresent(this::scan);
    }

    @Override
    public void visitForLoop(ForLoopNode that) {
        scan(that.getStartIndexExpression());
        scan(that.getEndIndexExpression());
        that.getOptionalStepperStatement().ifPresent(this::scan);
        scan(that.getBody());
    }

    @Override
    public void visitWhileLoop(WhileLoopNode that) {
        scan(that.getCondition());
        scan(that.getBody());
    }

    @Override
    public void visitExpression(ExpressionNode that) {
        // empty implementation
    }

    @Override
    public void visitBinaryExpression(BinaryExpressionNode that) {
        scan(that.getLeftOperand());
        scan(that.getRightOperand());
    }

    @Override
    public void visitParenthesizedExpression(ParenthesizedExpressionNode that) {
        scan(that.getInnerExpressionNode());
    }

    @Override
    public void visitPrefixOperation(PrefixOperationNode that) {
        scan(that.getOperand());
    }

    @Override
    public void visitUnaryExpression(UnaryExpressionNode that) {
        scan(that.getIdentifierAccess());
    }

    @Override
    public void visitEnsure(EnsureStatementNode that) {
        scan(that.getBody());
    }

    @Override
    public void visitIntercept(InterceptStatementNode that) {
        scan(that.getInterceptedExceptions());
        scan(that.getBody());
    }

    @Override
    public void visitRaise(RaiseStatementNode that) {
        scan(that.getException());
    }

    @Override
    public void visitTry(TryStatementNode that) {
        scan(that.getBody());
        scan(that.getInterceptBlocks());
        that.getEnsureBlock().ifPresent(this::scan);
    }

    @Override
    public void visitControlStatement(ControlStatementNode that) {
        // empty implementation
    }

    @Override
    public void visitLoopControlStatement(LoopControlStatementNode that) {
        // empty implementation
    }

    @Override
    public void visitReturnStatement(ReturnStatementNode that) {
        scan(that.getReturnExpression());
    }

    @Override
    public void visitStatement(StatementNode that) {
        // empty implementation
    }

    @Override
    public void visitArrayLiteral(ArrayLiteralNode that) {
        scan(that.getElements());
    }

    @Override
    public void visitFunctionDefinition(FunctionDefinitionNode that) {
        scan(that.getParameterTypes());
        scan(that.getFunctionBody());
    }

    @Override
    public void visitPrimitiveLiteral(PrimitiveLiteralNode that) {
        // empty implementation
    }

    @Override
    public void visitStructDefinitionLiteral(StructDefinitionLiteralNode that) {
        scan(that.getFieldTypes());
    }

    @Override
    public void visitStructInitializationLiteral(StructInitializationLiteralNode that) {
        scan(that.getIdentifier());
        scan(that.getArguments());
    }

    @Override
    public void visitFunctionCall(FunctionCallNode that) {
        scan(that.getFunctionIdentifier());
        scan(that.getArguments());
    }

    @Override
    public void visitIdentifierAccess(IdentifierAccessNode that) {
        scan(that.getArrayIndices());
    }

    @Override
    public void visitLiteral(LiteralNode that) {
        // empty implementation
    }

    @Override
    public void visitTerm(TermNode that) {
        // empty implementation
    }

    private void scan(Node node) {
        if (node != null) {
            node.accept(this);
        }
    }

    private void scan(Iterable<? extends Node> nodes) {
        if (nodes != null) {
            nodes.forEach(node -> node.accept(this));
        }
    }
}
