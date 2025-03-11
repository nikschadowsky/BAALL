package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents the imports of a program.
 *
 * @since 30.07.2024
 */
public interface ImportsNode extends Node {

    /**
     * Imported elements referenced by BAALL file references.
     *
     * @return imported elements
     */
    @NotNull
    @UnmodifiableView
    List<Token> getImports();

}
