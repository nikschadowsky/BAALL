package de.nikschadowsky.baall.compiler.semantic.type;


import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.symbol.TypeAlreadyExistsException;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.IdentifierTypeNode;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @since 30.03.2025
 */
public class TypeTable {

    private final ImportsNode imports;

    public TypeTable(ImportsNode imports) {
        this.imports = imports;
    }

    private final Set<Key> types = new HashSet<>();

    public void registerType(String identifier, Scope scope, TypeReference.Kind kind) throws TypeAlreadyExistsException {
        if (hasTypeRegistered(identifier, scope)) {
            throw new TypeAlreadyExistsException("Type already registered with identifier '%s'".formatted(identifier));
        }
        types.add(new Key(identifier, scope, kind));
    }

    public boolean hasTypeRegistered(String identifier, Scope scope) {
        return types.stream().anyMatch(k -> k.identifier.equals(identifier) && k.scope.equals(scope));
    }

    public Optional<TypeReference> resolveType(IdentifierTypeNode node, Scope scope) {
        return Optional.empty();
    }

    private record Key(String identifier, Scope scope, Kind kind) implements TypeReference {
        @Override
        public Kind getKind() {
            return kind;
        }
    }
}
