package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.statement.StatementNode;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * @since 29.07.2024
 */
public interface StatementsNode extends Node {

    @UnmodifiableView
    List<StatementNode> getStatements();

}
