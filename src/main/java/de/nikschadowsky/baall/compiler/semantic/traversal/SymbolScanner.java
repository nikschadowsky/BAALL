package de.nikschadowsky.baall.compiler.semantic.traversal;

import de.nikschadowsky.baall.compiler.output.error.DiagnosticCollector;
import de.nikschadowsky.baall.compiler.semantic.SemanticDiagnostic;
import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.semantic.type.BaallTypeFactory;
import de.nikschadowsky.baall.compiler.semantic.type.TypeTable;
import de.nikschadowsky.baall.compiler.symbol.SymbolAlreadyExistsException;
import de.nikschadowsky.baall.compiler.symbol.SymbolTable;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.ConstantDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment.VariableDeclarationNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;

public class SymbolScanner extends ScopeTraverser {

    private final TypeTable typeTable;
    private final SymbolTable symbolTable;
    private final DiagnosticCollector<SemanticDiagnostic> diagnosticCollector;
    private final BaallTypeFactory baallTypeFactory = BaallTypeFactory.create();

    public SymbolScanner(TypeTable typeTable, SymbolTable symbolTable, DiagnosticCollector<SemanticDiagnostic> diagnosticCollector) {
        this.typeTable = typeTable;
        this.symbolTable = symbolTable;
        this.diagnosticCollector = diagnosticCollector;
    }

    @Override
    public Boolean visitConstantDeclaration(ConstantDeclarationNode that, Scope data) {
        BaallType type = mapNodeToBaallType(that.getType(), data);
        Token identifier = that.getIdentifier().getIdentifier();
        try {
            symbolTable.registerSymbol(identifier.value(), data, type, true, identifier.lineInformation());
        } catch (SymbolAlreadyExistsException e) {
            diagnosticCollector.addError(new SemanticDiagnostic("There already exists an element with this "));
        }
        return super.visitConstantDeclaration(that, data);
    }

    @Override
    public Boolean visitVariableDeclaration(VariableDeclarationNode that, Scope data) {
        BaallType type = mapNodeToBaallType(that.getType(), data);
        Token identifier = that.getIdentifier().getIdentifier();
        try {
            symbolTable.registerSymbol(identifier.value(), data, type, false, identifier.lineInformation());
        } catch (SymbolAlreadyExistsException e) {
            diagnosticCollector.addError(new SemanticDiagnostic("There already exists an element with this "));
        }
        return super.visitVariableDeclaration(that, data);
    }

    @Override
    public Boolean visitField(FieldNode that, Scope data) {
        BaallType type = mapNodeToBaallType(that.getType(), data);
        Token identifier = that.getIdentifier().getIdentifier();
        try {
            symbolTable.registerSymbol(identifier.value(), data, type, true, identifier.lineInformation());
        } catch (SymbolAlreadyExistsException e) {
            diagnosticCollector.addError(new SemanticDiagnostic("There already exists an element with this "));
        }
        return super.visitField(that, data);
    }

    private BaallType mapNodeToBaallType(TypeNode typeNode, Scope currentScope) {
        return switch (typeNode) {
            case FunctionTypeNode node -> baallTypeFactory.createFunctionType(
                    mapNodeToBaallType(node.getInnerType(), currentScope),
                    node.getParameterTypes().stream().map(n -> mapNodeToBaallType(n, currentScope)).toList()
            );
            case IdentifierTypeNode node ->
                    baallTypeFactory.createUserDefinedType(typeTable.resolveType(node, currentScope).orElseThrow());
            case ListTypeNode node ->
                    baallTypeFactory.createListType(mapNodeToBaallType(node.getInnerType(), currentScope));
            case PrimitiveTypeNode node -> baallTypeFactory.createPrimitiveType(node.getKind());
        };
    }
}
