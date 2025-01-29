package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
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

    private List<FieldNode> fields = Collections.emptyList();

    public StructDefinitionLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull @UnmodifiableView List<FieldNode> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void setFields(@NotNull List<FieldNode> fields) {
        this.fields = new ArrayList<>(fields);
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
