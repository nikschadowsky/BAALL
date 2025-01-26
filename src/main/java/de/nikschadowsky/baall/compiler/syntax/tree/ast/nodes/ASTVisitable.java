package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes;


import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;

/**
 * @since 23.01.2025
 */
public interface ASTVisitable {

    void accept(ASTVisitor visitor);
    
}
