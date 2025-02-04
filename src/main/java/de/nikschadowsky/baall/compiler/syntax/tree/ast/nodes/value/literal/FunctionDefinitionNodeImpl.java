package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 28.07.2024
 */
public class FunctionDefinitionNodeImpl extends AbstractNode implements FunctionDefinitionNode {

    private List<FieldNode> parameters = Collections.emptyList();
    private StatementsNode functionBody;

    public FunctionDefinitionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @NotNull @UnmodifiableView List<FieldNode> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public void setParameters(@NotNull List<FieldNode> parameters) {
        this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public StatementsNode getFunctionBody() {
        return functionBody;
    }

    public void setFunctionBody(StatementsNode functionBody) {
        this.functionBody = functionBody;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.FUNCTION_DEFINITION;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitFunctionDefinition(this, data);
    }
}
