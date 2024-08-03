package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * File created on 28.07.2024
 */
public class FunctionDefinitionNodeImpl extends AbstractNode implements FunctionDefinitionNode {

    private List<Map.Entry<TypeNode, Token>> parameters;

    private StatementsNode functionBody;

    public FunctionDefinitionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    public void setParameters(List<Map.Entry<TypeNode, Token>> parameters) {
        this.parameters = parameters;
    }

    @Override
    public @UnmodifiableView List<Map.Entry<TypeNode, Token>> getParameters() {
        return Collections.unmodifiableList(parameters);
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
}
