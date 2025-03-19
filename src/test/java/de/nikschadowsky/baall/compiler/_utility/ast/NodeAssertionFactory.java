package de.nikschadowsky.baall.compiler._utility.ast;


import de.nikschadowsky.baall.compiler._utility.ast.nodes.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ExportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.LiteralNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;

import java.util.List;

/**
 * @since 11.02.2025
 */
public interface NodeAssertionFactory<ASSERTION, ACTUAL> {

    ASSERTION map(ACTUAL actual);

    static TypeNodeAssertion create(TypeNode actual) {
        return new TypeNodeAssertion(actual);
    }

    static TypeNodeAssertion.FunctionTypeNodeAssertion create(FunctionTypeNode actual) {
        return new TypeNodeAssertion.FunctionTypeNodeAssertion(actual);
    }

    static TypeNodeAssertion.PrimitiveTypeNodeAssertion create(PrimitiveTypeNode actual) {
        return new TypeNodeAssertion.PrimitiveTypeNodeAssertion(actual);
    }

    static TypeNodeAssertion.IdentifierTypeNodeAssertion create(IdentifierTypeNode actual) {
        return new TypeNodeAssertion.IdentifierTypeNodeAssertion(actual);
    }

    static TypeNodeAssertion.NestedTypeNodeAssertion create(NestedTypeNode actual) {
        return new TypeNodeAssertion.NestedTypeNodeAssertion(actual);
    }

    static <T> ListAssertion<T> create(List<T> actual) {
        return new ListAssertion<>(actual);
    }

    static StatementsNodeAssertion create(StatementsNode actual) {
        return new StatementsNodeAssertion(actual);
    }

    static FieldNodeAssertion create(FieldNode actual) {
        return new FieldNodeAssertion(actual);
    }

    static IdentifierNodeAssertion create(IdentifierNode actual) {
        return new IdentifierNodeAssertion(actual);
    }

    static OperatorNodeAssertion create(OperatorNode actual) {
        return new OperatorNodeAssertion(actual);
    }

    static ForLoopNodeAssertion create(ForLoopNode actual) {
        return new ForLoopNodeAssertion(actual);
    }

    static WhileLoopNodeAssertion create(WhileLoopNode actual) {
        return new WhileLoopNodeAssertion(actual);
    }

    static ConditionalNodeAssertion create(ConditionalNode actual) {
        return new ConditionalNodeAssertion(actual);
    }

    static TryStatementNodeAssertion create(TryStatementNode actual) {
        return new TryStatementNodeAssertion(actual);
    }

    // todo move class into control statement assertion?
    static StatementNodeAssertion.ReturnStatementNodeAssertion create(ReturnStatementNode actual) {
        return new StatementNodeAssertion.ReturnStatementNodeAssertion(actual);
    }

    static StatementNodeAssertion.RaiseStatementNodeAssertion create(RaiseStatementNode actual) {
        return new StatementNodeAssertion.RaiseStatementNodeAssertion(actual);
    }

    // todo move class into try assertion?
    static TryStatementNodeAssertion.InterceptStatementNodeAssertion create(InterceptStatementNode actual) {
        return new TryStatementNodeAssertion.InterceptStatementNodeAssertion(actual);
    }

    // todo move class into try assertion?
    static TryStatementNodeAssertion.EnsureStatementNodeAssertion create(EnsureStatementNode actual) {
        return new TryStatementNodeAssertion.EnsureStatementNodeAssertion(actual);
    }

    static ExpressionNodeAssertion create(ExpressionNode actual) {
        return new ExpressionNodeAssertion(actual);
    }

    static ExpressionNodeAssertion.ParenthesizedExpressionNodeAssertion create(ParenthesizedExpressionNode actual) {
        return new ExpressionNodeAssertion.ParenthesizedExpressionNodeAssertion(actual);
    }

    static ExpressionNodeAssertion.FunctionCallNodeAssertion create(FunctionCallNode actual) {
        return new ExpressionNodeAssertion.FunctionCallNodeAssertion(actual);
    }

    static ExpressionNodeAssertion.PrefixOperationNodeAssertion create(PrefixOperationNode actual) {
        return new ExpressionNodeAssertion.PrefixOperationNodeAssertion(actual);
    }

    static ExpressionNodeAssertion.BinaryExpressionNodeAssertion create(BinaryExpressionNode actual) {
        return new ExpressionNodeAssertion.BinaryExpressionNodeAssertion(actual);
    }

    static ExpressionNodeAssertion.UnaryExpressionNodeAssertion create(UnaryExpressionNode actual) {
        return new ExpressionNodeAssertion.UnaryExpressionNodeAssertion(actual);
    }

    static LiteralNodeAssertion create(LiteralNode actual) {
        return new LiteralNodeAssertion(actual);
    }

    static LiteralNodeAssertion.PrimitiveLiteralNodeAssertion create(PrimitiveLiteralNode actual) {
        return new LiteralNodeAssertion.PrimitiveLiteralNodeAssertion(actual);
    }

    static LiteralNodeAssertion.ArrayLiteralNodeAssertion create(ArrayLiteralNode actual) {
        return new LiteralNodeAssertion.ArrayLiteralNodeAssertion(actual);
    }

    static LiteralNodeAssertion.StructDefinitionLiteralNodeAssertion create(StructDefinitionLiteralNode actual) {
        return new LiteralNodeAssertion.StructDefinitionLiteralNodeAssertion(actual);
    }

    static LiteralNodeAssertion.FunctionDefinitionNodeAssertion create(FunctionDefinitionNode actual) {
        return new LiteralNodeAssertion.FunctionDefinitionNodeAssertion(actual);
    }

    static LiteralNodeAssertion.StructNoneLiteralNodeAssertion create(StructNoneLiteralNode actual) {
        return new LiteralNodeAssertion.StructNoneLiteralNodeAssertion(actual);
    }

    static StatementNodeAssertion create(StatementNode actual) {
        return new StatementNodeAssertion(actual);
    }

    static StatementNodeAssertion.VariableDeclarationNodeAssertion create(VariableDeclarationNode actual) {
        return new StatementNodeAssertion.VariableDeclarationNodeAssertion(actual);
    }

    static StatementNodeAssertion.ConstantDeclarationNodeAssertion create(ConstantDeclarationNode actual) {
        return new StatementNodeAssertion.ConstantDeclarationNodeAssertion(actual);
    }

    static StatementNodeAssertion.VariableReassignmentNodeAssertion create(VariableReassignmentNode actual) {
        return new StatementNodeAssertion.VariableReassignmentNodeAssertion(actual);
    }

    static StatementNodeAssertion.ReassignmentNodeAssertion create(ReassignmentNode actual) {
        return new StatementNodeAssertion.ReassignmentNodeAssertion(actual);
    }

    static ImportsNodeAssertion create(ImportsNode actual) {
        return new ImportsNodeAssertion(actual);
    }

    static StatementNodeAssertion.DeclarationNodeAssertion create(DeclarationNode actual) {
        return new StatementNodeAssertion.DeclarationNodeAssertion(actual);
    }

    static ExportsNodeAssertion create(ExportsNode actual) {
        return new ExportsNodeAssertion(actual);
    }

    static ElementAccessNodeAssertion create(ElementAccessNode actual) {
        return new ElementAccessNodeAssertion(actual);
    }

    static ElementAccessNodeAssertion.IndexedAccessNodeAssertion create(IndexedAccessNode actual) {
        return new ElementAccessNodeAssertion.IndexedAccessNodeAssertion(actual);
    }

    static ElementAccessNodeAssertion.MemberReferenceNodeAssertion create(MemberReferenceNode actual) {
        return new ElementAccessNodeAssertion.MemberReferenceNodeAssertion(actual);
    }

    static ElementAccessNodeAssertion.ScopeElevationNodeAssertion create(ScopeElevationNode actual) {
        return new ElementAccessNodeAssertion.ScopeElevationNodeAssertion(actual);
    }
}
