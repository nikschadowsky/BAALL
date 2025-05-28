package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SymbolTable {

    private static final String NO_SYMBOL_EXCEPTION_TEMPLATE =
            "There is no symbol with the provided identifier '%s' in the provided scope '%s'";
    private static final String SYMBOL_ALREADY_REGISTERED_TEMPLATE =
            "There is already a symbol registered with identifier '%s'";

    private final BaallTypeFactory typeFactory;

    public SymbolTable(BaallTypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /**
     * @param identifier
     * @param scope
     * @param type
     * @param isConstant
     * @param lineInformation
     * @throws SymbolAlreadyExistsException
     */
    public void registerSymbol(
            IdentifierNode identifier,
            Scope scope,
            BaallType type,
            boolean isConstant,
            LineInformation lineInformation
    ) throws SymbolAlreadyExistsException {
        if (!canDeclareSymbolInScope(identifier, scope)) {
            throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
        }
        findOrCreateNode(scope).symbols.add(new SymbolImpl(identifier, scope, type, isConstant, lineInformation));
    }

    /**
     * @param identifier
     * @param scope
     * @param type
     * @param isConstant
     * @param lineInformation
     * @throws SymbolAlreadyExistsException
     */
    public void registerFunction(
            IdentifierNode identifier,
            Scope scope,
            FunctionType type,
            boolean isConstant,
            LineInformation lineInformation
    ) throws SymbolAlreadyExistsException {
        Table table = findOrCreateNode(scope);
        if (isConstant) {
            if (!canDeclareConstantFunction(identifier, scope)) {
                throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
            }
            table.functionDeclarations.add(new Function(identifier, scope, type, true, lineInformation));
        } else {
            registerSymbol(identifier, scope, type, false, lineInformation);
            //table.symbols.add(new SymbolImpl(identifier, scope, type, false, lineInformation));
        }
    }

    /**
     * Tests if a symbol with the given identifier and symbol can be declared in the given scope, as specified in the
     * BAALL specification.
     *
     * @param identifier identifier of the symbol declaration under test
     * @param scope      scope of the symbol declaration under test
     * @return true if a symbol with the specified identifier can be declared in the provided scope
     */
    public boolean canDeclareSymbolInScope(IdentifierNode identifier, Scope scope) {
        return findExactTable(scope).stream()
                                    .flatMap(Table::stream)
                                    .filter(s -> scope.canAccess(s.scope()))
                                    .noneMatch(s -> s.identifier().equals(identifier));
    }

    /**
     * Tests if a function with the given identifier can be declared in the given scope. If {@code isConstant == false}
     * this method behaves exactly like {@link #canDeclareSymbolInScope(IdentifierNode, Scope)}, as specified in the
     * BAALL specification.
     *
     * @param identifier identifier of the function declaration under test
     * @param scope      scope of the function declaration under test
     * @param isConstant flag if the function under test is constant
     * @return true if a function with the specified identifier can be declared in the provided scope
     */
    public boolean canDeclareFunctionInScope(IdentifierNode identifier, Scope scope, boolean isConstant) {
        return isConstant ? canDeclareConstantFunction(identifier, scope) : canDeclareSymbolInScope(identifier, scope);
    }

    private boolean canDeclareConstantFunction(IdentifierNode identifier, Scope scope) {
        return findExactTable(scope).stream()
                                    .flatMap(Table::stream)
                                    .filter(s -> s.scope().canAccess(scope) || scope.canAccess(s.scope()))
                                    .noneMatch(s -> s.identifier().equals(identifier));
    }

    public boolean isConstant(IdentifierNode identifier, Scope scope) throws NoSymbolFoundException {
        Table table = findExactTable(scope).orElseThrow(
                () -> new NoSymbolFoundException(NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(identifier, scope))
        );

        return table.stream()
                    .filter(s -> s.scope().equals(scope) && s.identifier().equals(identifier))
                    .findFirst()
                    .map(Symbol::isConstant)
                    .orElseThrow(() -> new NoSymbolFoundException(NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(
                            identifier,
                            scope
                    )));
    }

    /**
     * Resolves a symbol based on the provided element access node accessible from the given scope.
     *
     * @param elementAccess the element access node for which the symbol should be resolved
     * @param scope the scope in which the symbol resolution occurs
     * @return the resolved symbol
     */
    public Symbol resolveElement(ElementAccessNode elementAccess, Scope scope) {
        return resolveElement(elementAccess, scope, null);
    }

    private Symbol resolveElement(ElementAccessNode elementAccess, Scope scope, @Nullable Scope enforcedMajor) {
        return switch (elementAccess) {
            case ScopeElevationNode i -> resolveScopeElevation(i, scope);
            case ComponentAccessNode i -> resolveComponentAccess(i, scope, enforcedMajor);
            case IndexedAccessNode i -> resolveIndexedAccess(i, scope, enforcedMajor);
            case IdentifierNode i -> resolveIdentifier(i, scope, enforcedMajor);
        };
    }

    private Symbol resolveScopeElevation(ScopeElevationNode node, Scope scope) {
        ScopeElevationNode currentScopeElevationNode = node;
        Scope enforcedMajor = scope.getMajor();
        while (currentScopeElevationNode.getInner() instanceof ScopeElevationNode inner) {
            currentScopeElevationNode = inner;
            enforcedMajor = enforcedMajor.getParent()
                                         .orElseThrow(() -> new OutOfScopeException("No parent scope found."))
                                         .getMajor();
        }

        return switch (currentScopeElevationNode.getInner()) {
            case IdentifierNode i -> resolveIdentifier(i, scope, enforcedMajor);
            case IndexedAccessNode i -> resolveIndexedAccess(i, scope, enforcedMajor);
            case ComponentAccessNode i -> resolveComponentAccess(i, scope, enforcedMajor);
            case ScopeElevationNode ignored ->
                    throw new IllegalStateException("Node should not be an instance of this type. Impossible state.");
        };
    }

    private Symbol resolveComponentAccess(ComponentAccessNode node, Scope scope, @Nullable Scope enforcedMajor) {
        // todo namespace access!!!
        boolean canBeNamespace = node.getInner() instanceof IdentifierNode;

        Symbol inner = resolveElement(node.getInner(), scope, enforcedMajor);

        if (inner.isUnknown()) {
            if (canBeNamespace) {
                // todo check for namespace
                return new UnknownSymbol(scope, inner.type(), "namespace resolution is not yet implemented");
            } else {
                return new UnknownSymbol(scope, inner.type(), "cannot access member of unknown symbol");
            }
        }

        if (!(inner.type() instanceof UserDefinedType userDefinedType)) {
            return new UnknownSymbol(
                    scope,
                    typeFactory.createUserDefinedType(TypeReference.unknown("illegal referencing"), false),
                    "cannot access member of non-user defined type"
            );
        }

        if (!typeFactory.getTypeTable().hasField(userDefinedType.reference(), node.getSelected())) {
            return new UnknownSymbol(
                    scope,
                    typeFactory.createUserDefinedType(TypeReference.unknown("unknown component"), false),
                    "component does not exist in type"
            );
        }

        return new SymbolImpl(
                node.getSelected(),
                scope,
                typeFactory.getTypeTable()
                           .getComponentType(userDefinedType.reference(), node.getSelected()), false, inner.lineInformation());
    }

    private Symbol resolveIndexedAccess(IndexedAccessNode node, Scope scope, @Nullable Scope enforcedMajor) {
        Symbol inner = resolveElement(node.getInner(), scope, enforcedMajor);

        if (inner.isUnknown()) {
            return new UnknownSymbol(scope, inner.type(), "cannot access indexed value of unknown symbol");
        }

        if (!(inner.type() instanceof ListType listType)) {
            return new UnknownSymbol(
                    scope,
                    typeFactory.createUserDefinedType(TypeReference.unknown("illegal referencing"), false),
                    "cannot access indexed value of non-list type"
            );
        }
        return new Member(inner, inner.scope(), listType.elementType(), false, inner.lineInformation());
    }

    private Symbol resolveIdentifier(IdentifierNode identifierNode, Scope scope, @Nullable Scope enforcedMajor) {
        boolean enforceScope = enforcedMajor != null;
        Optional<Table> optionalNode = enforceScope ? findExactTable(enforcedMajor) : findFirstTable(scope);
        if (optionalNode.isEmpty()) {
            return new UnknownSymbol(
                    enforceScope ? enforcedMajor : scope,
                    typeFactory.createUserDefinedType(TypeReference.unknown("unknown symbol"), false),
                    "symbol could not be resolved"
            );
        }
        Table table = optionalNode.get();
        // there should only be a single hit, but let's just assert that for testing
        List<NamedSymbol> symbols = table.stream()
                                         .filter(s -> scope.canAccess(s.scope()))
                                         .filter(s -> s.identifier().equals(identifierNode))
                                         .toList();
        assert symbols.size() <= 1;

        if (symbols.isEmpty()) {
            if (enforceScope) {
                return new UnknownSymbol(
                        enforcedMajor,
                        typeFactory.createUserDefinedType(TypeReference.unknown("unknown symbol"), false),
                        "symbol could not be resolved"
                );
            }
            if (scope.isImport()) {
                // todo resolve imports
                return new UnknownSymbol(
                        scope,
                        typeFactory.createUserDefinedType(TypeReference.unknown("unknown symbol"), false),
                        "importing of symbols is not yet implemented"
                );
            }
            return resolveIdentifier(identifierNode, scope.getParent().orElseThrow(), null);
        }
        return symbols.getFirst();
    }

    /**
     * @param scope
     * @return
     */
    private Table findOrCreateNode(Scope scope) {
        Scope major = scope.getMajor();
        return findExactTable(major).orElseGet(() -> {
            Table parent = findOrCreateParentTable(major);
            Table table = new Table(major, parent);
            if (parent != null) {
                parent.symbols.add(table);
            }
            nodes.put(major, table);
            return table;
        });
    }

    /**
     * Finds a node in the set of nodes. It uses the passed scope to determine the nearest major scope.
     *
     * @param scope major or minor scope
     * @return optional node of the passed scope's nearest major
     */
    private Optional<Table> findExactTable(Scope scope) {
        Scope major = scope.getMajor();
        return Optional.ofNullable(nodes.get(major));
    }

    private Optional<Table> findFirstTable(Scope scope) {
        Scope major = scope.getMajor();
        while (!nodes.containsKey(major)) {
            Optional<Scope> parent = scope.getParent();
            if (parent.isEmpty()) {
                return Optional.empty();
            }
            major = parent.get().getMajor();
        }
        return Optional.of(nodes.get(major));
    }

    private @Nullable Table findOrCreateParentTable(Scope scope) {
        Optional<Scope> parent = scope.getParent();
        // create or find the all parent nodes recursively
        return parent.map(this::findOrCreateNode).orElse(null);
    }

    private final LinkedHashMap<Scope, Table> nodes = new LinkedHashMap<>();

    private sealed interface SymbolTableEntry permits SymbolImpl, Table {
    }

    public sealed interface Symbol extends Exportable permits Member, NamedSymbol, UnknownSymbol {
        Scope scope();

        BaallType type();

        boolean isConstant();

        LineInformation lineInformation();

        default boolean isUnknown() {
            return this instanceof UnknownSymbol;
        }
    }

    private record Member(Symbol parent, Scope scope, BaallType type, boolean isConstant,
                          LineInformation lineInformation) implements Symbol {

    }

    private sealed interface NamedSymbol extends Symbol permits Function, SymbolImpl {
        IdentifierNode identifier();
    }

    private record Function(IdentifierNode identifier, Scope scope, FunctionType type, boolean isConstant,
                            LineInformation lineInformation) implements NamedSymbol {
    }

    private record SymbolImpl(IdentifierNode identifier, Scope scope, BaallType type, boolean isConstant,
                              LineInformation lineInformation) implements SymbolTableEntry, NamedSymbol {
    }

    private record UnknownSymbol(Scope scope, BaallType type, String reason) implements Symbol {

        @Override
        public boolean isConstant() {
            return true;
        }

        @Override
        public LineInformation lineInformation() {
            return new LineInformation(-1, -1);
        }

        @Override
        public @NotNull String toString() {
            return "Unknown symbol in scope '%s' because: %s".formatted(scope, reason);
        }
    }

    private final static class Table implements SymbolTableEntry {

        /**
         * major scope. todo better typing?
         */
        private final Scope own;
        /*
        Node represents a major scope. Finding or creating a node uses a major scope.
        Has symbol registered looks at a node and to find the symbol
         */
        private final @Nullable SymbolTable.Table parent;
        /**
         * use-before-declare
         */
        private final List<Function> functionDeclarations = new ArrayList<>();

        private final List<SymbolTableEntry> symbols = new ArrayList<>();

        private Table(Scope own, @Nullable SymbolTable.Table parent) {
            this.own = own;
            this.parent = parent;
        }

        Stream<NamedSymbol> stream() {
            return Stream.concat(functionDeclarations.stream(), symbols.stream())
                         .filter(NamedSymbol.class::isInstance)
                         .map(NamedSymbol.class::cast);
        }
    }
}
