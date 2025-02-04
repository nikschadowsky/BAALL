package de.nikschadowsky.baall.compiler.syntax.tree.traversal;


/**
 * @since 23.01.2025
 */
public interface ASTVisitable {

    <D, R> R accept(ASTVisitor<D, R> visitor, D data);
}
