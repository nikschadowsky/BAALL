package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.FieldNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.util.CollectionUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 28.07.2024
 */
public final class FunctionDefinitionNodeImpl extends AbstractNode implements FunctionDefinitionNode {

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
        checkIntegrity();
    }

    private void checkIntegrity() {
        List<IdentifierNode> duplicates = CollectionUtility.duplicates(parameters, FieldNode::getIdentifier);
        duplicates.forEach(identifierNode -> getDiagnosticCollector().report(new NodeDiagnostic(
                "There is already a parameter with this identifier.",
                identifierNode.getIdentifier(),
                NodeDiagnostic.ReportingLevel.ERROR)
        ));
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FunctionDefinitionNodeImpl that)) return false;
        return Objects.equals(getParameters(), that.getParameters())
                       && Objects.equals(getFunctionBody(), that.getFunctionBody())
                       && getNodeType().equals(that.getNodeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParameters(), getFunctionBody(), getNodeType());
    }

    @Override
    public @NotNull String getDisplayDescriptor() {
        return "(%s) {%s}".formatted(
                getParameters().stream().map(Node::getDisplayDescriptor).collect(Collectors.joining(",")),
                getFunctionBody().getDisplayDescriptor()
        );
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitFunctionDefinition(this, data);
    }
}
