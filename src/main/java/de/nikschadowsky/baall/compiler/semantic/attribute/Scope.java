package de.nikschadowsky.baall.compiler.semantic.attribute;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @since 15.01.2025
 */
public class Scope {

    // todo does this need to be a function to create a new root? it would seem reasonable to have a new root for each baall file
    public static final Scope ROOT = new Scope();

    private final @Nullable Scope parent;

    private Scope() {
        parent = null;
    }

    public Scope(@NotNull Scope parent) {
        this.parent = parent;
    }

    public Optional<Scope> getParent() {
        return Optional.ofNullable(parent);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean canAccess(@NotNull Scope scope) {
        Scope current = this;

        do {
            if (current == scope) return true;
            current = current.parent;
        } while (current != null);

        return false;
    }
}
