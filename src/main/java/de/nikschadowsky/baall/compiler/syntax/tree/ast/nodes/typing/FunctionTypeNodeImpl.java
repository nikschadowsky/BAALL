package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @since 04.03.2025
 */
public final class FunctionTypeNodeImpl extends AbstractNode implements FunctionTypeNode {

    private List<TypeNode> parameterTypes = Collections.emptyList();
    private TypeNode innerType;

    public FunctionTypeNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public TypeNode getInnerType() {
        return innerType;
    }

    public void setInnerType(TypeNode innerType) {
        this.innerType = innerType;
    }

    @Override
    public boolean isNoneSafe() {
        return true;
    }

    @Override
    public @NotNull @UnmodifiableView List<TypeNode> getParameterTypes() {
        return Collections.unmodifiableList(parameterTypes);
    }

    public void setParameterTypes(List<TypeNode> parameterTypes) {
        this.parameterTypes = new ArrayList<>(parameterTypes);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.FUNCTION_TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FunctionTypeNodeImpl that)) return false;
        return Objects.equals(getInnerType(), that.getInnerType())
                       && Objects.equals(getParameterTypes(), that.getParameterTypes())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInnerType(), getParameterTypes(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "%s<%s>".formatted(
                getInnerType().getDisplayDescriptor(),
                getParameterTypes().stream().map(Node::getDisplayDescriptor).collect(Collectors.joining(","))
        );
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitFunctionType(this, data);
    }
}
