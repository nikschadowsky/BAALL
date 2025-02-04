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

/**
 * @since 23.01.2025
 */
public interface ASTVisitor<D, R> {

    R visitProgram(ProgramNode that, D data);

    R visitImports(ImportsNode that, D data);

    R visitStatements(StatementsNode that, D data);

    R visitExports(ExportsNode that, D data);

    //
    R visitDeclaration(DeclarationNode that, D data);

    R visitConstantDeclaration(ConstantDeclarationNode that, D data);

    R visitVariableDeclaration(VariableDeclarationNode that, D data);

    R visitReassignment(ReassignmentNode that, D data);

    R visitVariableReassignment(VariableReassignmentNode that, D data);

    //
    R visitType(TypeNode that, D data);

    //
    R visitControlStructure(ControlStructureNode that, D data);

    R visitConditional(ConditionalNode that, D data);

    R visitForLoop(ForLoopNode that, D data);

    R visitWhileLoop(WhileLoopNode that, D data);

    //
    R visitExpression(ExpressionNode that, D data);

    R visitBinaryExpression(BinaryExpressionNode that, D data);

    R visitParenthesizedExpression(ParenthesizedExpressionNode that, D data);

    R visitPrefixOperation(PrefixOperationNode that, D data);

    R visitUnaryExpression(UnaryExpressionNode that, D data);

    //
    R visitEnsure(EnsureStatementNode that, D data);

    R visitIntercept(InterceptStatementNode that, D data);

    R visitRaise(RaiseStatementNode that, D data);

    R visitTry(TryStatementNode that, D data);

    //
    R visitControlStatement(ControlStatementNode that, D data);

    R visitLoopControlStatement(LoopControlStatementNode that, D data);

    R visitReturnStatement(ReturnStatementNode that, D data);

    R visitStatement(StatementNode that, D data);

    //
    R visitArrayLiteral(ArrayLiteralNode that, D data);

    R visitFunctionDefinition(FunctionDefinitionNode that, D data);

    R visitPrimitiveLiteral(PrimitiveLiteralNode that, D data);

    R visitStructDefinitionLiteral(StructDefinitionLiteralNode that, D data);

    R visitStructInitializationLiteral(StructInitializationLiteralNode that, D data);

    //
    R visitFunctionCall(FunctionCallNode that, D data);

    R visitIdentifierAccess(IdentifierAccessNode that, D data);

    R visitLiteral(LiteralNode that, D data);

    R visitTerm(TermNode that, D data);

    R visitField(FieldNode that, D data);

    R visitIdentifier(IdentifierNode that, D data);

    R visitOperator(OperatorNode that, D data);
}
