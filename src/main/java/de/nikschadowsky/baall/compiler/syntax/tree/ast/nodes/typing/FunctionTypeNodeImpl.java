package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;


import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 04.03.2025
 */
public class FunctionTypeNodeImpl extends AbstractNode implements FunctionTypeNode {

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
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitFunctionType(this, data);
    }
}
