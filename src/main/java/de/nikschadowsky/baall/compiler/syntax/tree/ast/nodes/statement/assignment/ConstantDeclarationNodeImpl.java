package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.assignment;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.expression.ExpressionNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.TypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @since 30.07.2024
 */
public class ConstantDeclarationNodeImpl extends AbstractNode implements ConstantDeclarationNode {

    private TypeNode type;
    private IdentifierNode identifier;
    private ExpressionNode initializationValue;

    public ConstantDeclarationNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    @Override
    public IdentifierNode getIdentifier() {
        return identifier;
    }

    public void setIdentifier(IdentifierNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public ExpressionNode getInitializationValue() {
        return initializationValue;
    }

    public void setInitializationValue(ExpressionNode initializationValue) {
        this.initializationValue = initializationValue;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.CONSTANT_DECLARATION;
    }

    @Override
    public <D, R> R accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitConstantDeclaration(this, data);
    }
}
