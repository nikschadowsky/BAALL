package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface Symbol extends Exportable permits SymbolTable.NamedSymbol, SymbolTable.UnknownSymbol {

    @Nullable IdentifierNode identifier();

    @NotNull Scope scope();

    @NotNull BaallType type();

    boolean isValueConstant();

    @NotNull LineInformation lineInformation();

    default boolean isUnknown() {
        return this instanceof SymbolTable.UnknownSymbol;
    }

    @NotNull String description();
}
