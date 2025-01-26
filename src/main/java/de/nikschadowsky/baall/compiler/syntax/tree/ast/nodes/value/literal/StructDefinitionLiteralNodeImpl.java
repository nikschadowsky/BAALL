package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @since 29.07.2024
 */
public class StructDefinitionLiteralNodeImpl extends AbstractNode implements StructDefinitionLiteralNode {

    private List<TypeNode> fieldTypes;

    private List<Token> fieldNames;

    public StructDefinitionLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<TypeNode> getFieldTypes() {
        return Collections.unmodifiableList(fieldTypes);
    }

    public void setFieldTypes(List<TypeNode> fieldTypes) {
        this.fieldTypes = new ArrayList<>(fieldTypes);
    }

    @Override
    public @UnmodifiableView List<Token> getFieldNames() {
        return Collections.unmodifiableList(fieldNames);
    }

    public void setFieldNames(List<Token> fieldNames) {
        this.fieldNames = new ArrayList<>(fieldNames);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.STRUCT_DEFINITION;
    }

    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitStructDefinitionLiteral(this, data);
    }
}
