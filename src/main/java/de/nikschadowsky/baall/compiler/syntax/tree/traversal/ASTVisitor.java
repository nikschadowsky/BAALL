package de.nikschadowsky.baall.compiler.syntax.tree.traversal;


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
public interface ASTVisitor<D, R> {

    Optional<R> visitProgram(ProgramNode that, D data);

    Optional<R> visitImports(ImportsNode that, D data);

    Optional<R> visitStatements(StatementsNode that, D data);

    Optional<R> visitExports(ExportsNode that, D data);

    //
    Optional<R> visitDeclaration(DeclarationNode that, D data);

    Optional<R> visitConstantDeclaration(ConstantDeclarationNode that, D data);

    Optional<R> visitVariableDeclaration(VariableDeclarationNode that, D data);

    Optional<R> visitReassignment(ReassignmentNode that, D data);

    Optional<R> visitVariableReassignment(VariableReassignmentNode that, D data);

    //
    Optional<R> visitType(TypeNode that, D data);

    //
    Optional<R> visitControlStructure(ControlStructureNode that, D data);

    Optional<R> visitConditional(ConditionalNode that, D data);

    Optional<R> visitForLoop(ForLoopNode that, D data);

    Optional<R> visitWhileLoop(WhileLoopNode that, D data);

    //
    Optional<R> visitExpression(ExpressionNode that, D data);

    Optional<R> visitBinaryExpression(BinaryExpressionNode that, D data);

    Optional<R> visitParenthesizedExpression(ParenthesizedExpressionNode that, D data);

    Optional<R> visitPrefixOperation(PrefixOperationNode that, D data);

    Optional<R> visitUnaryExpression(UnaryExpressionNode that, D data);

    //
    Optional<R> visitEnsure(EnsureStatementNode that, D data);

    Optional<R> visitIntercept(InterceptStatementNode that, D data);

    Optional<R> visitRaise(RaiseStatementNode that, D data);

    Optional<R> visitTry(TryStatementNode that, D data);

    //
    Optional<R> visitControlStatement(ControlStatementNode that, D data);

    Optional<R> visitLoopControlStatement(LoopControlStatementNode that, D data);

    Optional<R> visitReturnStatement(ReturnStatementNode that, D data);

    Optional<R> visitStatement(StatementNode that, D data);

    //
    Optional<R> visitArrayLiteral(ArrayLiteralNode that, D data);

    Optional<R> visitFunctionDefinition(FunctionDefinitionNode that, D data);

    Optional<R> visitPrimitiveLiteral(PrimitiveLiteralNode that, D data);

    Optional<R> visitStructDefinitionLiteral(StructDefinitionLiteralNode that, D data);

    Optional<R> visitStructInitializationLiteral(StructInitializationLiteralNode that, D data);

    //
    Optional<R> visitFunctionCall(FunctionCallNode that, D data);

    Optional<R> visitIdentifierAccess(IdentifierAccessNode that, D data);

    Optional<R> visitLiteral(LiteralNode that, D data);

    Optional<R> visitTerm(TermNode that, D data);

    Optional<R> visitField(FieldNode that, D data);

    Optional<R> visitIdentifier(IdentifierNode that, D data);

    Optional<R> visitOperator(OperatorNode that, D data);
}
