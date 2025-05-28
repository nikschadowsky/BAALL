package de.nikschadowsky.baall.compiler.semantic.traversal;

import de.nikschadowsky.baall.compiler.output.error.DiagnosticCollector;
import de.nikschadowsky.baall.compiler.semantic.SemanticDiagnostic;
import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.TypeReference;
import de.nikschadowsky.baall.compiler.semantic.type.TypeTable;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.symbol.TypeAlreadyExistsException;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.StructDefinitionLiteralNode;

public class TypeScanner extends ScopeTraverser {

    private final TypeTable table;
    private final DiagnosticCollector<SemanticDiagnostic> diagnosticCollector;
    private static final String ILLEGAL_VAR_TYPE =
            "Cannot use %1%s when declaring a variable! %1$ss can only be declared as constants.";

    public TypeScanner(TypeTable table, DiagnosticCollector<SemanticDiagnostic> diagnosticCollector) {
        this.table = table;
        this.diagnosticCollector = diagnosticCollector;
    }

    @Override
    public Boolean visitConstantDeclaration(ConstantDeclarationNode that, Scope scope) {
        if (that.getType() instanceof PrimitiveTypeNode type && that.getInitializationValue() instanceof StructDefinitionLiteralNode structDefinition) {
            try {
                switch (type.getKind()) {
                    case STRUCT ->
                            table.registerType(that.getIdentifier(), scope, TypeReference.Kind.STRUCT, structDefinition);
                    case EXCEPTION ->
                            table.registerType(that.getIdentifier(), scope, TypeReference.Kind.EXCEPTION, structDefinition);
                }
            } catch (TypeAlreadyExistsException e) {
                diagnosticCollector.addError(new SemanticDiagnostic(
                        "Type with name '%s' already declared in this scope!".formatted(that.getIdentifier().getIdentifier().value()))
                );
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
