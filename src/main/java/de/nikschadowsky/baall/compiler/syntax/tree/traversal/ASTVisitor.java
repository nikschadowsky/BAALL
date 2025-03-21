package de.nikschadowsky.baall.compiler.syntax.tree.traversal;


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
public interface ASTVisitor<D, R> {

    R visitConditional(ConditionalNode that, D data);

    R visitForLoop(ForLoopNode that, D data);

    R visitWhileLoop(WhileLoopNode that, D data);

    R visitExports(ExportsNode that, D data);

    R visitImports(ImportsNode that, D data);

    R visitProgram(ProgramNode that, D data);

    R visitStatements(StatementsNode that, D data);

    R visitConstantDeclaration(ConstantDeclarationNode that, D data);

    R visitVariableDeclaration(VariableDeclarationNode that, D data);

    R visitVariableReassignment(VariableReassignmentNode that, D data);

    R visitBreakStatement(BreakStatementNode that, D data);

    R visitContinueStatement(ContinueStatementNode that, D data);

    R visitReturnStatement(ReturnStatementNode that, D data);

    R visitEnsureStatement(EnsureStatementNode that, D data);

    R visitInterceptStatement(InterceptStatementNode that, D data);

    R visitRaiseStatement(RaiseStatementNode that, D data);

    R visitTryStatement(TryStatementNode that, D data);

    R visitFunctionType(FunctionTypeNode that, D data);

    R visitIdentifierType(IdentifierTypeNode that, D data);

    R visitListType(ListTypeNode that, D data);

    R visitPrimitiveType(PrimitiveTypeNode that, D data);

    R visitUnaryExpression(UnaryExpressionNode that, D data);

    R visitBinaryExpression(BinaryExpressionNode that, D data);

    R visitPrefixOperation(PrefixOperationNode that, D data);

    R visitParenthesizedExpression(ParenthesizedExpressionNode that, D data);

    R visitFunctionCall(FunctionCallNode that, D data);

    R visitPrimitiveLiteral(PrimitiveLiteralNode that, D data);

    R visitStructDefinitionLiteral(StructDefinitionLiteralNode that, D data);

    R visitListLiteral(ListLiteralNode that, D data);

    R visitFunctionDefinition(FunctionDefinitionNode that, D data);

    R visitIdentifier(IdentifierNode that, D data);

    R visitField(FieldNode that, D data);

    R visitOperator(OperatorNode that, D data);

    R visitStructNoneLiteral(StructNoneLiteralNode that, D data);

    R visitIndexedAccess(IndexedAccessNode that, D data);

    R visitMemberReference(MemberReferenceNode that, D data);

    R visitScopeElevation(ScopeElevationNode that, D data);
}
