package de.nikschadowsky.baall.compiler.semantic.traversal;

import de.nikschadowsky.baall.compiler.output.error.DiagnosticCollector;
import de.nikschadowsky.baall.compiler.semantic.SemanticDiagnostic;
import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.symbol.SymbolAlreadyExistsException;
import de.nikschadowsky.baall.compiler.symbol.SymbolTable;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;

public class TypeScanner extends ScopeTraverser {

    private final SymbolTable table;
    private final DiagnosticCollector<SemanticDiagnostic> diagnosticCollector;
    private static final String ILLEGAL_VAR_TYPE =
            "Cannot use %1%s when declaring a variable! %1$ss can only be declared as constants.";

    public TypeScanner(SymbolTable table, DiagnosticCollector<SemanticDiagnostic> diagnosticCollector) {
        this.table = table;
        this.diagnosticCollector = diagnosticCollector;
    }

    @Override
    public Boolean visitConstantDeclaration(ConstantDeclarationNode that, Scope scope) {
        if (that.getType() instanceof PrimitiveTypeNode type) {
            if (type.getKind() == PrimitiveTypeNode.Kind.STRUCT ||
                    type.getKind() == PrimitiveTypeNode.Kind.EXCEPTION) {
                Token identifier = that.getIdentifier().getIdentifier();
                try {
                    table.registerSymbol(identifier.value(), scope, identifier.lineInformation());
                } catch (SymbolAlreadyExistsException e) {
                    diagnosticCollector.addError(new SemanticDiagnostic(
                            "Type with name '%s' already declared in this scope!".formatted(identifier))
                    );
                }
            }
        }

        return super.visitConstantDeclaration(that, scope);
    }

    @Override
    public Boolean visitVariableDeclaration(VariableDeclarationNode that, Scope data) {
        if (that.getType() instanceof PrimitiveTypeNode type) {
            switch (type.getKind()) {
                case STRUCT ->
                        diagnosticCollector.addError(new SemanticDiagnostic(ILLEGAL_VAR_TYPE.formatted("struct")));
                case EXCEPTION ->
                        diagnosticCollector.addError(new SemanticDiagnostic(ILLEGAL_VAR_TYPE.formatted("exception")));
            }
        }

        return super.visitVariableDeclaration(that, data);
    }
}
