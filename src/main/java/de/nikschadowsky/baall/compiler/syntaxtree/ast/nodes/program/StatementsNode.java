package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.Node;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.statement.StatementNode;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * File created on 29.07.2024
 */
public interface StatementsNode extends Node {

    @UnmodifiableView
    List<StatementNode> getStatements();

}
