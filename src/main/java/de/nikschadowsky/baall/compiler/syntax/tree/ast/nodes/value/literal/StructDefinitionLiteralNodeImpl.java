package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @since 29.07.2024
 */
public class StructDefinitionLiteralNodeImpl extends AbstractNode implements StructDefinitionLiteralNode {

    private List<Map.Entry<TypeNode, Token>> fields;

    public StructDefinitionLiteralNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<Map.Entry<TypeNode, Token>> getFields() {
        return NodeUtility.toUnmodifiableList(fields);
    }

    public void setFields(List<Map.Entry<TypeNode, Token>> fields) {
        this.fields = new LinkedList<>(fields);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.STRUCT_DEFINITION;
    }
}
