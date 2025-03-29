package de.nikschadowsky.baall.compiler.semantic.attribute;


import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @since 15.01.2025
 */
public class Scope {

    /**
     * Constant to be used for all symbols that are imported from other sources. In the scope hierarchy it is placed
     * above the program root.
     */
    public static final Scope IMPORTS = new Scope(null);

    public static final Scope ROOT = new Scope(IMPORTS);

    private final Scope parent;

    public static Scope create(@NotNull Scope parent) {
        return new Scope(parent);
    }

    private Scope(Scope parent) {
        this.parent = parent;
    }

    public Optional<Scope> getParent() {
        return Optional.ofNullable(parent);
    }

    public boolean isRoot() {
        return this == ROOT;
    }

    public boolean isImport() {
        return this == IMPORTS;
    }

    public boolean canAccess(@NotNull Scope scope) {
        Scope current = this;

        do {
            if (current == scope) return true;
            current = current.parent;
        } while (current != null);

        return false;
    }

    @Override
    public String toString() {
        if (isRoot()) {
            return "ROOT";
        }
        if (isImport()) {
            return "IMPORTS";
        }

        return super.toString();
    }
}
