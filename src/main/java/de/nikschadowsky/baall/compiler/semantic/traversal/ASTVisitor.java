package de.nikschadowsky.baall.compiler.semantic.traversal;


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
public interface ASTVisitor {

    void visitProgram(ProgramNode that);

    void visitImports(ImportsNode that);

    void visitStatements(StatementsNode that);

    void visitExports(ExportsNode that);

    //
    void visitDeclaration(DeclarationNode that);

    void visitConstantDeclaration(ConstantDeclarationNode that);

    void visitVariableDeclaration(VariableDeclarationNode that);

    void visitReassignment(ReassignmentNode that);

    void visitVariableReassignment(VariableReassignmentNode that);

    //
    void visitType(TypeNode that);

    //
    void visitControlStructure(ControlStructureNode that);

    void visitConditional(ConditionalNode that);

    void visitForLoop(ForLoopNode that);

    void visitWhileLoop(WhileLoopNode that);

    //
    void visitExpression(ExpressionNode that);

    void visitBinaryExpression(BinaryExpressionNode that);

    void visitParenthesizedExpression(ParenthesizedExpressionNode that);

    void visitPrefixOperation(PrefixOperationNode that);

    void visitUnaryExpression(UnaryExpressionNode that);

    //
    void visitEnsure(EnsureStatementNode that);

    void visitIntercept(InterceptStatementNode that);

    void visitRaise(RaiseStatementNode that);

    void visitTry(TryStatementNode that);

    //
    void visitControlStatement(ControlStatementNode that);

    void visitLoopControlStatement(LoopControlStatementNode that);

    void visitReturnStatement(ReturnStatementNode that);

    void visitStatement(StatementNode that);

    //
    void visitArrayLiteral(ArrayLiteralNode that);

    void visitFunctionDefinition(FunctionDefinitionNode that);

    void visitPrimitiveLiteral(PrimitiveLiteralNode that);

    void visitStructDefinitionLiteral(StructDefinitionLiteralNode that);

    void visitStructInitializationLiteral(StructInitializationLiteralNode that);

    //
    void visitFunctionCall(FunctionCallNode that);

    void visitIdentifierAccess(IdentifierAccessNode that);

    void visitLiteral(LiteralNode that);

    void visitTerm(TermNode that);
}
