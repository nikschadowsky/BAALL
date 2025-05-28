package de.nikschadowsky.baall.compiler.semantic.type;


import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.symbol.NoSuchFieldException;
import de.nikschadowsky.baall.compiler.symbol.NoSuchTypeException;
import de.nikschadowsky.baall.compiler.symbol.OutOfScopeException;
import de.nikschadowsky.baall.compiler.symbol.TypeAlreadyExistsException;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program.ImportsNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.IdentifierTypeNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.value.literal.StructDefinitionLiteralNode;

import java.util.*;
import java.util.function.Predicate;

/**
 * @since 30.03.2025
 */
public class TypeTable {

    private final BaallTypeFactory typeFactory;

    private final ImportsNode imports;
    private final Set<Key> types = new HashSet<>();
    private final Map<Key, StructDefinitionLiteralNode> fieldDefinitionNodes = new HashMap<>();
    private final Map<TypeReference, Map<IdentifierNode, BaallType>> fields = new HashMap<>();
    private boolean loaded = false;

    public TypeTable(ImportsNode imports) {
        this.imports = imports;
        this.typeFactory = new BaallTypeFactory(this);
    }

    /**
     * Registers a new type in the provided scope. Inside a major scope there can only be a single registered type with
     * a specific identifier.
     *
     * @param identifier identifier of the type
     * @param scope      scope of this registration
     * @param kind       type kind
     * @param definition struct definition of the type. This is used to register struct components
     * @throws TypeAlreadyExistsException if there already is a type with an equal identifier in the major scope
     *                                    specified by the scope parameter
     */
    public void registerType(
            IdentifierNode identifier,
            Scope scope,
            TypeReference.Kind kind,
            StructDefinitionLiteralNode definition
    ) throws TypeAlreadyExistsException {
        if (hasTypeRegistered(identifier, scope)) {
            throw new TypeAlreadyExistsException("Type already registered with identifier '%s'".formatted(identifier));
        }
        Key key = new Key(identifier, scope, kind);
        types.add(key);
        fieldDefinitionNodes.put(key, definition);
    }

    /**
     * Returns {@code true} when there is a type with an equal identifier in the same major scope. Two types are in the
     * same major scope if their Scope#getMajor is equal.
     *
     * @param identifier identifier
     * @param scope      scope
     * @return {@code true} if there is a type with an equal identifier in the same major scope.
     */
    public boolean hasTypeRegistered(IdentifierNode identifier, Scope scope) {
        return types.stream()
                    .anyMatch(k -> k.identifier.equals(identifier) && k.scope.getMajor().equals(scope.getMajor()));
    }

    private Map<IdentifierNode, BaallType> getFieldDefinitions(StructDefinitionLiteralNode node) {
        Map<IdentifierNode, BaallType> fields = new HashMap<>();
        node.getFields().forEach(f -> fields.put(f.getIdentifier(), typeFactory.getTypeForNode(f.getType())));
        return fields;
    }

    /**
     * @param type
     * @param field
     * @return
     */
    public boolean hasField(TypeReference type, IdentifierNode field) throws NoSuchTypeException {
        checkLoaded();
        if (!fields.containsKey(type)) {
            throw new NoSuchTypeException("No type with name '%s' found in type table!".formatted(type));
        }
        return fields.get(type).containsKey(field);
    }

    /**
     * @param type
     * @param field
     * @return
     * @throws NoSuchFieldException
     */
    public BaallType getComponentType(TypeReference type, IdentifierNode field) throws NoSuchTypeException, NoSuchFieldException {
        checkLoaded();
        if (!fields.containsKey(type)) {
            throw new NoSuchTypeException("No type with name '%s' found in type table!".formatted(type));
        }
        Map<IdentifierNode, BaallType> typeFields = fields.get(type);
        if (!typeFields.containsKey(field)) {
            throw new NoSuchFieldException("No field with name '%s' found in type '%s'".formatted(field, type));
        }

        return typeFields.get(field);
    }

    private void checkLoaded() {
        if (!loaded) {
            fieldDefinitionNodes.forEach((k, v) -> {
                fields.put(k, getFieldDefinitions(v));
            });
            loaded = true;
        }
    }

    /**
     * Resolves a type using an identifier type. Delegates to methods specializing in resolving a subtype of identifier
     * type node.
     *
     * @param node  identifier type node
     * @param scope scope of this node
     * @return type reference. The returned type will be UNKNOWN if nothing was found.
     */
    public TypeReference resolveType(IdentifierTypeNode node, Scope scope) {
        return switch (node.getType()) {
            case IdentifierNode i -> resolveType(i, scope, false);
            case IndexedAccessNode i -> resolveType(i, scope);
            case ComponentAccessNode i -> resolveType(i); // member reference can only be namespace access
            case ScopeElevationNode i -> resolveType(i, scope); // ensure that inner cannot be IAN
        };
    }

    /**
     * Resolves a type using an identifier node. This method can be used with an enforced scope.
     *
     * @param node         identifier node
     * @param majorScope   scope of this node
     * @param enforceScope enforces lookup in the same major scope
     * @return type reference. The returned type will be UNKNOWN if nothing was found.
     */
    private TypeReference resolveType(IdentifierNode node, Scope majorScope, boolean enforceScope) {
        Predicate<Key> scopePredicate = enforceScope ? k -> majorScope.equals(k.scope) : k -> majorScope.canAccess(k.scope);

        return types.stream()
                    .filter(scopePredicate)
                    .filter(k -> k.identifier.equals(node))
                    .findFirst()
                    .map(TypeReference.class::cast)
                    // todo if empty load imported types
                    .orElse(TypeReference.unknown("No type found with identifier '%s'!".formatted(node.getDisplayDescriptor())));
    }

    /**
     * Tries to resolve a type using an indexed access node. This method always throws an
     * IllegalTypeResolutionException, since indexed accesses to types produce dynamic types, which are disallowed.
     *
     * @param node  index access node
     * @param scope scope of this node
     * @return never returns a type reference since this method always throws an exception.
     * @throws IllegalTypeResolutionException always thrown since indexed accesses to types produce dynamic types,
     */
    private TypeReference resolveType(IndexedAccessNode node, Scope scope) {
        return TypeReference.unknown("Cannot resolve type from indexed access. Type is dynamic!");
    }

    /**
     * Resolves a type using a member reference. This member's parent can only be an identifier of a namespace.
     * Otherwise, this is a dynamic type. <b>Currently, always returns Optional.empty()!</b>
     *
     * @param node member reference node
     * @return type reference of the resolved imported type. The returned type will be UNKNOWN if nothing was found.
     */
    private TypeReference resolveType(ComponentAccessNode node) {
        // this has to be a namespace reference since dynamic types within e.g., struct fields are disallowed
        // implicitly enforces scope to IMPORTS
        return TypeReference.unknown("The use of imported types is not supported yet.");
    }

    /**
     * Resolves a type using a scope elevation node. The enforced lookup scope is calculated and is the only scope types
     * can be resolved.
     *
     * @param node  scope elevation node
     * @param scope scope of the node
     * @return type reference of a type in a specific scope. The returned type will be UNKNOWN if nothing was found.
     */
    private TypeReference resolveType(ScopeElevationNode node, Scope scope) {
        ScopeElevationNode currentScopeElevationNode = node;
        Scope enforcedScope = scope.getMajor();
        while (currentScopeElevationNode.getInner() instanceof ScopeElevationNode inner) {
            currentScopeElevationNode = inner;
            enforcedScope = enforcedScope.getParent()
                                         .orElseThrow(() -> new OutOfScopeException("No parent scope found."))
                                         .getMajor();
        }

        return switch (currentScopeElevationNode.getInner()) {
            case IdentifierNode i -> resolveType(i, enforcedScope, true);
            case IndexedAccessNode i -> resolveType(i, enforcedScope);
            case ComponentAccessNode i -> {
                if (!enforcedScope.equals(Scope.IMPORTS)) {
                    throw new IllegalTypeResolutionException(
                            "You can only reference a type with a namespace from the imports scope."
                    );
                }
                yield resolveType(i);
            }
            case ScopeElevationNode ignored ->
                    throw new IllegalStateException("Node should not be an instance of this type. Impossible state.");
        };
    }

    private record Key(IdentifierNode identifier, Scope scope, Kind kind) implements TypeReference {
        @Override
        public Kind getKind() {
            return kind;
        }
    }
}
