package de.nikschadowsky.baall.compiler.syntax.tree.ast;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IndexedAccessNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ComponentAccessNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ScopeElevationNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ConditionalNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.ForLoopNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.controlstructures.WhileLoopNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ExportsNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ProgramNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableReassignmentNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.BreakStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ContinueStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.ReturnStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.FunctionTypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.IdentifierTypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.ListTypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FunctionCallNodeImpl;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.*;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;

/**
 * @since 08.04.2024
 */
public class ASTNodeFactory {

    private final NodeDiagnosticCollector diagnostics;

    public ASTNodeFactory(NodeDiagnosticCollector diagnostics) {
        this.diagnostics = diagnostics;
    }

    // top level

    public ProgramNodeImpl createProgramNode() {
        return new ProgramNodeImpl(diagnostics);
    }

    public ImportsNodeImpl createImportNode() {
        return new ImportsNodeImpl(diagnostics);
    }

    public ExportsNodeImpl createExportNode() {
        return new ExportsNodeImpl(diagnostics);
    }

    public StatementsNodeImpl createStatementsNode() {
        return new StatementsNodeImpl(diagnostics);
    }

    // statements

    public VariableDeclarationNodeImpl createVariableDeclarationNode() {
        return new VariableDeclarationNodeImpl(diagnostics);
    }

    public ConstantDeclarationNodeImpl createConstantDeclarationNode() {
        return new ConstantDeclarationNodeImpl(diagnostics);
    }

    public VariableReassignmentNodeImpl createVariableReassignmentNode() {
        return new VariableReassignmentNodeImpl(diagnostics);
    }

    public WhileLoopNodeImpl createWhileLoopNode() {
        return new WhileLoopNodeImpl(diagnostics);
    }

    public ConditionalNodeImpl createConditionalNode() {
        return new ConditionalNodeImpl(diagnostics);
    }

    public ForLoopNodeImpl createForLoopNode() {
        return new ForLoopNodeImpl(diagnostics);
    }

    public FunctionCallNodeImpl createFunctionCallNode() {
        return new FunctionCallNodeImpl(diagnostics);
    }

    public ContinueStatementNodeImpl createContinueStatementNode() {
        return new ContinueStatementNodeImpl(diagnostics);
    }

    public BreakStatementNodeImpl createBreakStatementNode() {
        return new BreakStatementNodeImpl(diagnostics);
    }

    public ReturnStatementNodeImpl createReturnStatementNode() {
        return new ReturnStatementNodeImpl(diagnostics);
    }

    // expression

    public UnaryExpressionNodeImpl createUnaryExpressionNode() {
        return new UnaryExpressionNodeImpl(diagnostics);
    }

    public BinaryExpressionNodeImpl createBinaryExpressionNode() {
        return new BinaryExpressionNodeImpl(diagnostics);
    }

    public PrefixOperationNodeImpl createPrefixOperationNode() {
        return new PrefixOperationNodeImpl(diagnostics);
    }

    public ParenthesizedExpressionNodeImpl createParenthesizedExpressionNode() {
        return new ParenthesizedExpressionNodeImpl(diagnostics);
    }

    // exception handling
    public RaiseStatementNodeImpl createRaiseStatementNode() {
        return new RaiseStatementNodeImpl(diagnostics);
    }

    public TryStatementNodeImpl createTryStatementNode() {
        return new TryStatementNodeImpl(diagnostics);
    }

    public InterceptStatementNodeImpl createInterceptStatementNode() {
        return new InterceptStatementNodeImpl(diagnostics);
    }

    public EnsureStatementNodeImpl createEnsureStatementNode() {
        return new EnsureStatementNodeImpl(diagnostics);
    }

    // literals

    public BooleanLiteralNodeImpl createBooleanLiteralNode() {
        return new BooleanLiteralNodeImpl(diagnostics);
    }

    public NumberLiteralNodeImpl createNumberLiteralNode() {
        return new NumberLiteralNodeImpl(diagnostics);
    }

    public StringLiteralNodeImpl createStringLiteralNode() {
        return new StringLiteralNodeImpl(diagnostics);
    }

    public StructDefinitionLiteralNodeImpl createStructDefinitionLiteralNode() {
        return new StructDefinitionLiteralNodeImpl(diagnostics);
    }

    public ListLiteralNodeImpl createListLiteralNode() {
        return new ListLiteralNodeImpl(diagnostics);
    }

    public FunctionDefinitionNodeImpl createFunctionDefinitionNode() {
        return new FunctionDefinitionNodeImpl(diagnostics);
    }

    public FunctionTypeNodeImpl createFunctionTypeNode() {
        return new FunctionTypeNodeImpl(diagnostics);
    }

    public IdentifierTypeNodeImpl createIdentifierTypeNode() {
        return new IdentifierTypeNodeImpl(diagnostics);
    }

    public PrimitiveTypeNodeImpl createPrimitiveTypeNode() {
        return new PrimitiveTypeNodeImpl(diagnostics);
    }

    public ListTypeNodeImpl createListTypeNode() {
        return new ListTypeNodeImpl(diagnostics);
    }

    public IdentifierNodeImpl createIdentifierNode() {
        return new IdentifierNodeImpl(diagnostics);
    }

    public FieldNodeImpl createFieldNode() {
        return new FieldNodeImpl(diagnostics);
    }

    public OperatorNodeImpl createOperatorNode() {
        return new OperatorNodeImpl(diagnostics);
    }

    public StructNoneLiteralNodeImpl createStructNoneLiteralNode() {
        return new StructNoneLiteralNodeImpl(diagnostics);
    }

    public IndexedAccessNodeImpl createIndexedAccessNode() {
        return new IndexedAccessNodeImpl(diagnostics);
    }

    public ComponentAccessNodeImpl createMemberReferenceNode() {
        return new ComponentAccessNodeImpl(diagnostics);
    }

    public ScopeElevationNodeImpl createScopeElevationNode() {
        return new ScopeElevationNodeImpl(diagnostics);
    }
}
