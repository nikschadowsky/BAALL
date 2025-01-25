package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal;

import de.nikschadowsky.baall.compiler.semantic.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.StatementsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
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

    private List<TypeNode> parameterTypes;

    private List<Token> parameterNames;

    private StatementsNode functionBody;

    public FunctionDefinitionNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public @UnmodifiableView List<TypeNode> getParameterTypes() {
        return Collections.unmodifiableList(parameterTypes);
    }

    public void setParameterTypes(List<TypeNode> types) {
        this.parameterTypes = new ArrayList<>(types);
    }

    @Override
    public @UnmodifiableView List<Token> getParameterNames() {
        return Collections.unmodifiableList(parameterNames);
    }

    public void setParameterNames(List<Token> names) {
        this.parameterNames = new ArrayList<>(names);
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
