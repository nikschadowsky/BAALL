package de.nikschadowsky.baall.compiler.syntaxtree.ast;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.ConditionalNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.ForLoopNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.controlstructures.WhileLoopNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.BinaryExpressionNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.ParenthesizedExpressionNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.PrefixOperationNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.expression.UnaryExpressionNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ExportsNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ImportsNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.ProgramNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.ConstantDeclarationNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.VariableDeclarationNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.assignment.VariableReassignmentNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.BreakStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.ContinueStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.ReturnStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.EnsureStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.InterceptStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.RaiseStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.controlstatement.exceptionhandling.TryStatementNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.ExceptionCallNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.FunctionCallNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.IdentifierAccessNodeImpl;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal.*;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;

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

    public IdentifierAccessNodeImpl createIdentifierAccessNode() {
        return new IdentifierAccessNodeImpl(diagnostics);
    }


    // exception handling
    public RaiseStatementNodeImpl createRaiseStatementNode() {
        return new RaiseStatementNodeImpl(diagnostics);
    }

    public ExceptionCallNodeImpl createExceptionCallNode() {
        return new ExceptionCallNodeImpl(diagnostics);
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

    public PrimitiveLiteralNodeImpl createPrimitiveLiteralNode() {
        return new PrimitiveLiteralNodeImpl(diagnostics);
    }

    public StructDefinitionLiteralNodeImpl createStructDefinitionLiteralNode() {
        return new StructDefinitionLiteralNodeImpl(diagnostics);
    }

    public StructInitializationLiteralNodeImpl createStructInitializationLiteralNode() {
        return new StructInitializationLiteralNodeImpl(diagnostics);
    }

    public ArrayLiteralNodeImpl createArrayLiteralNode() {
        return new ArrayLiteralNodeImpl(diagnostics);
    }

    public FunctionDefinitionNodeImpl createFunctionDefinitionNode() {
        return new FunctionDefinitionNodeImpl(diagnostics);
    }

    public TypeNodeImpl createTypeNode() {
        return new TypeNodeImpl(diagnostics);
    }
}
