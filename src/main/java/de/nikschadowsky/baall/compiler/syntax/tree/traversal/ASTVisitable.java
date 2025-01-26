package de.nikschadowsky.baall.compiler.syntax.tree.traversal;


import java.util.Optional;

/**
 * @since 23.01.2025
 */
public interface ASTVisitable {

    <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data);
}
