package de.nikschadowsky.baall.compiler.semantic.traversal;

import de.nikschadowsky.baall.compiler.output.error.DiagnosticCollector;
import de.nikschadowsky.baall.compiler.semantic.SemanticDiagnostic;
import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.FunctionTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;

public class TypeAndFunctionScanner extends ScopeTraverser {

    private final DiagnosticCollector<SemanticDiagnostic> diagnosticCollector;

    public TypeAndFunctionScanner(DiagnosticCollector<SemanticDiagnostic> diagnosticCollector) {
        this.diagnosticCollector = diagnosticCollector;
    }

    @Override
    public Boolean visitConstantDeclaration(ConstantDeclarationNode that, Scope data) {
        return super.visitConstantDeclaration(that, data);
    }

    @Override
    public Boolean visitVariableDeclaration(VariableDeclarationNode that, Scope data) {
        if (that.getType() instanceof PrimitiveTypeNode type) {
            switch (type.getKind()) {
                case STRUCT ->
                        diagnosticCollector.addError(new SemanticDiagnostic("Cannot use struct when declaring a variable! Structs can only be declared as constants."));
                case EXCEPTION ->
                        diagnosticCollector.addError(new SemanticDiagnostic("Cannot use exception when declaring a variable! Exceptions can only be declared as constants."));
            }
        }

        return super.visitVariableDeclaration(that, data);
    }
}
