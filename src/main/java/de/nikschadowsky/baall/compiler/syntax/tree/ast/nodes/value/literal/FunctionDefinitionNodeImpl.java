package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.semantic.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @since 28.07.2024
 */
public class FunctionDefinitionNodeImpl extends AbstractNode implements FunctionDefinitionNode {

    private List<Map.Entry<TypeNode, Token>> parameters;

    private StatementsNode functionBody;

    public FunctionDefinitionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<Map.Entry<TypeNode, Token>> getParameters() {
        return NodeUtility.toUnmodifiableList(parameters);
    }

    public void setParameters(List<Map.Entry<TypeNode, Token>> parameters) {
        this.parameters = new LinkedList<>(parameters);
    }

    public void setFunctionBody(StatementsNode functionBody) {
        this.functionBody = functionBody;
    }

    @Override
    public StatementsNode getFunctionBody() {
        return functionBody;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.FUNCTION_DEFINITION;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitFunctionDefinition(this);
    }
}
