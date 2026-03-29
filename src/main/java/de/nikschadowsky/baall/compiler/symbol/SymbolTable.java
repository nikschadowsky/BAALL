package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.*;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class SymbolTable {

    private static final String NO_SYMBOL_EXCEPTION_TEMPLATE =
            "There is no symbol with the provided identifier '%s' in the provided scope '%s'";
    private static final String SYMBOL_ALREADY_REGISTERED_TEMPLATE =
            "There is already a symbol registered with identifier '%s'";

    private final BaallTypeFactory typeFactory;

    private final LinkedHashMap<Scope, Page> allTables = new LinkedHashMap<>();

    public SymbolTable(BaallTypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /**
     * @param identifier      identifier of the variable
     * @param scope           declaration scope of the variable
     * @param type            type of the variable
     * @param lineInformation line information for the variable
     * @throws SymbolAlreadyExistsException if a symbol with a clashing identifier already exists in the scope
     */
    public void registerVariable(
            IdentifierNode identifier,
            Scope scope,
            BaallType type,
            LineInformation lineInformation
    ) throws SymbolAlreadyExistsException {
        if (!canDeclareVariableInScope(identifier, scope)) {
            throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
        }
        findOrCreateTable(scope).symbols.add(new VariableSymbol(identifier, scope, type, lineInformation));
    }

    /**
     * @param identifier      identifier of the constant
     * @param scope           declaration scope of the constant
     * @param type            type of the constant
     * @param lineInformation line information for the constant
     * @throws SymbolAlreadyExistsException if a symbol with a clashing identifier already exists in the scope
     */
    public void registerConstant(
            IdentifierNode identifier,
            Scope scope,
            BaallType type,
            LineInformation lineInformation
    ) throws SymbolAlreadyExistsException {
        Page page = findOrCreateTable(scope);
        if (!canDeclareConstantInScope(identifier, scope)) {
            throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
        }
        page.constantSymbols.add(new ConstantSymbol(identifier, scope, type, lineInformation));
    }

    public void registerField(
            IdentifierNode identifier,
            Scope scope,
            BaallType type,
            LineInformation lineInformation
    ) throws SymbolAlreadyExistsException {
        if (!canDeclareVariableInScope(identifier, scope)) {
            throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
        }
        findOrCreateTable(scope).symbols.add(new ConstantSymbol(identifier, scope, type, lineInformation));
    }

    /**
     * Tests if a symbol with the given identifier and symbol can be declared in the given scope, as specified in the
     * BAALL specification.
     *
     * @param identifier identifier of the symbol declaration under test
     * @param scope      scope of the symbol declaration under test
     * @return true if a symbol with the specified identifier can be declared in the provided scope
     */
    public boolean canDeclareVariableInScope(IdentifierNode identifier, Scope scope) {
        return streamInScope(scope, false).noneMatch(s -> Objects.equals(s.identifier(), identifier));
    }

    /**
     * Tests if a function with the given identifier can be declared in the given scope. If {@code isConstant == false}
     * this method behaves exactly like {@link #canDeclareVariableInScope(IdentifierNode, Scope)}, as specified in the
     * BAALL specification.
     *
     * @param identifier identifier of the function declaration under test
     * @param scope      scope of the function declaration under test
     * @param isConstant flag if the function under test is constant
     * @return true if a function with the specified identifier can be declared in the provided scope
     */
    public boolean canDeclareFunctionInScope(IdentifierNode identifier, Scope scope, boolean isConstant) {
        return isConstant ? canDeclareConstantInScope(identifier, scope) : canDeclareVariableInScope(identifier, scope);
    }

    private boolean canDeclareConstantInScope(IdentifierNode identifier, Scope scope) {
        return streamInScope(scope, true)
                       .noneMatch(s -> Objects.equals(s.identifier(), identifier));
    }

    /**
     * @param identifier
     * @param scope
     * @return
     * @throws NoSymbolFoundException
     */
    public boolean isConstant(IdentifierNode identifier, Scope scope) throws NoSymbolFoundException {
        Page page = findExactTable(scope).orElseThrow(
                () -> new NoSymbolFoundException(NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(identifier, scope))
        );

        return page.stream()
                   .filter(s -> Objects.equals(s.identifier(), identifier))
                   .findFirst()
                   .map(Symbol::isValueConstant)
                   .orElseThrow(() -> new NoSymbolFoundException(NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(
                           identifier,
                           scope
                   )));
    }

    /**
     * Resolves a symbol based on the provided element access node accessible from the given scope.
     *
     * @param elementAccess the element access node for which the symbol should be resolved
     * @param scope         the scope in which the symbol resolution occurs
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

        return new Component(
                node.getSelected(),
                inner,
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
        return new Component(null, inner, inner.scope(), listType.elementType(), false, inner.lineInformation());
    }

    private Symbol resolveIdentifier(IdentifierNode identifierNode, Scope scope, @Nullable Scope enforcedMajor) {
        boolean enforceScope = enforcedMajor != null;
        Optional<Page> optionalNode = enforceScope ? findExactTable(enforcedMajor) : findFirstTable(scope, false);
        if (optionalNode.isEmpty()) {
            return new UnknownSymbol(
                    enforceScope ? enforcedMajor : scope,
                    typeFactory.createUserDefinedType(TypeReference.unknown("unknown symbol"), false),
                    "symbol could not be resolved"
            );
        }
        Page page = optionalNode.get();
        List<NamedSymbol> symbols = streamScopeHierarchy(page, false)
                                            .filter(s -> Objects.equals(s.identifier(), identifierNode))
                                            .toList();
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
            return new UnknownSymbol(scope, typeFactory.createUserDefinedType(TypeReference.unknown("unknown symbol"), false), "symbol could not be resolved");
        }
        // take the lat symbol found. the list of symbols
        return symbols.getLast();
    }

    /**
     * Finds the table associated with the passed scope. If no table exists, this method will create an empty table and
     * insert it in the next position of its parent scope's table. This method will recursively create tables until
     * either a parent scope has a table or the root scope is reached.
     *
     * @param scope scope to look up the table
     * @return the found or created table
     */
    private Page findOrCreateTable(Scope scope) {
        if (scope == null) {
            return null;
        }
        if (allTables.containsKey(scope)) {
            return allTables.get(scope);
        }
        Page parent = findOrCreateTable(scope.getParent().orElse(null));
        Page page = new Page(scope, parent);

        if (parent != null) {
            // add entry to parent
            parent.symbols.add(page);
        }
        allTables.put(scope, page);
        return page;
    }

    /**
     * Looks up the first table to the provided scope. This method will exit early, when {@code stopAtMajor == true} and
     * a major scope is found.
     *
     * @param scope       scope to look up the table for
     * @param stopAtMajor when true, this method will stop at the first major scope, when false, this method will try to
     *                    find a table to a scope until it reaches the root of the scope tree
     * @return an empty optional if no table was found, otherwise the found table wrapped in an optional
     */
    private Optional<Page> findFirstTable(Scope scope, boolean stopAtMajor) {
        while (!allTables.containsKey(scope)) {
            Optional<Scope> parent = scope.getParent();
            // either at the root of the global scope tree or at the root of the major scope tree
            if (stopAtMajor && scope.isMajor() || parent.isEmpty()) {
                return Optional.empty();
            }
            scope = parent.get();
        }

        return Optional.of(allTables.get(scope));
    }

    /**
     * Tries to find the table associated with the provided scope. If there is no table associated with the provided
     * scope, this method will return an empty optional.
     *
     * @param scope scope to look up the table for
     * @return the found table wrapped in an optional, or an empty optional if no table was found
     */
    private Optional<Page> findExactTable(Scope scope) {
        return Optional.ofNullable(allTables.get(scope));
    }

    /**
     * Looks up the first table to the provided scope and proceeds to create a stream of all accessible declared symbols
     * and functions from this scope up until it hits the first major scope. The second argument controls whether all
     * declarations of inner scopes should be included.
     *
     * @param scope              scope to look up
     * @param flattenInnerTables when true, all inner scopes will be flattened into the stream of the current scope;
     *                           when false, all nested tables will be skipped
     * @return stream of all accessible declared symbols and functions from the provided scope up until it hits a major;
     * the order of the symbols is descending, starting from the first major scope
     */
    private Stream<NamedSymbol> streamInScope(Scope scope, boolean flattenInnerTables) {
        Optional<Page> firstTable = findFirstTable(scope, true);

        return firstTable.map(t -> {
            if (t.own.equals(scope)) {
                // ... there is a table associated with this scope
                Stream<NamedSymbol> all = flattenInnerTables ? t.streamFlattened() : t.stream();
                if (scope.isMajor()) {
                    return all;
                }
                return Stream.concat(streamTableParent(t, t.parent, true), all);
            }
            return streamScopeHierarchy(t, true);
        }).orElse(Stream.empty());
    }

    /**
     * Creates a stream of all accessible declared symbols and functions from the provided table up until it either hits
     * a major scope when {@code stopAtMajor == true} or the root of the table tree.
     *
     * @param page        table from which the hierarchy should be analyzed
     * @param stopAtMajor when true, this method will stop at the first major scope; when false, this method will
     *                    continue until the root of the table tree is reached
     * @return a stream of all accessible declared symbols and functions from the provided table up until it either hits
     * a major scope or the root of the table tree; the order of the symbols is descending, starting from either the
     * table root or the first major scope
     */
    private Stream<NamedSymbol> streamScopeHierarchy(Page page, boolean stopAtMajor) {
        if (stopAtMajor && page.own.isMajor()) {
            return page.stream();
        }
        return Stream.concat(streamTableParent(page, page.parent, stopAtMajor), page.stream());
    }

    /**
     * Creates a stream of all accessible declared symbols and functions from the provided parent up until it either
     * hits a major scope when {@code stopAtMajor == true} or the root of the table tree.
     *
     * @param target      the table which is targeted when traversing the parent tree
     * @param parent      the parent table to traverse upwards from the target table
     * @param stopAtMajor when true, this method will stop at the first major scope, when false, this method will
     *                    continue to traverse upwards until the root of the table tree is reached.
     * @return a stream of all accessible declared symbols and functions from the provided parent up until it either
     * hits a major scope or the root of the table tree; the order of the symbols is descending, starting from either
     * the table root or the first major scope
     */
    private Stream<NamedSymbol> streamTableParent(Page target, Page parent, boolean stopAtMajor) {
        if (parent == null) {
            return Stream.empty();
        }
        Stream<NamedSymbol> stream = Stream.concat(
                parent.constantDeclarations(),
                parent.symbolDeclarations()
                      .takeWhile(e -> !e.equals(target))
                      .filter(NamedSymbol.class::isInstance)
                      .map(NamedSymbol.class::cast)
        );
        if (stopAtMajor && parent.own.isMajor()) {
            return stream;
        }
        return Stream.concat(streamTableParent(target, parent.parent, stopAtMajor), stream);
    }

    /**
     * Interface of a symbol that can have a name and, therefore, isn't unknown. This interface is purposefully exposed
     * within its package.
     */
    sealed interface NamedSymbol extends Symbol permits Component, ConstantSymbol, FieldSymbol, VariableSymbol {
    }

    /**
     * Interface marking a valid entry in the symbol list of a symbol table.
     */
    private sealed interface SymbolTableEntry permits ConstantSymbol, FieldSymbol, Page, VariableSymbol {
    }

    /**
     * Record depicting a component of a composite type.
     *
     * @param parent          symbol of which this component is a part of
     * @param scope           scope of the parent
     * @param type            type of the component
     * @param isValueConstant flag if the component is constant or not
     * @param lineInformation line information of the parent declaration
     */
    private record Component(
            @Nullable IdentifierNode identifier,
            @NotNull Symbol parent,
            @NotNull Scope scope,
            @NotNull BaallType type,
            boolean isValueConstant,
            @NotNull LineInformation lineInformation
    ) implements NamedSymbol {

        @Override
        public @NotNull String description() {
            StringBuilder builder = new StringBuilder();
            builder.append("Component");
            if (identifier != null) {
                builder.append(" '");
                builder.append(identifier.getDisplayDescriptor());
                builder.append("'");
            }
            builder.append(" of ");
            builder.append(parent.description());
            builder.append(" with type ");
            builder.append(type);
            builder.append(" at line ");
            builder.append(lineInformation);
            return builder.toString();
        }

    }

    /**
     * Symbol for any constant declared. Constants support use-before declaration and, therefore, are handled
     * separately.
     *
     * @param identifier      identifier of the constant
     * @param scope           declaration scope of the constant
     * @param type            type of the constant
     * @param lineInformation line information about the constant
     */
    private record ConstantSymbol(
            @NotNull IdentifierNode identifier,
            @NotNull Scope scope,
            @NotNull BaallType type,
            @NotNull LineInformation lineInformation
    ) implements SymbolTableEntry, NamedSymbol {

        @Override
        public boolean isValueConstant() {
            return true;
        }

        @Override
        public @NotNull String description() {
            return "constant symbol '%s' with type '%s' at line %s".formatted(
                    identifier.getDisplayDescriptor(),
                    type,
                    lineInformation
            );
        }

        @Override
        public @NotNull String toString() {
            return description();
        }
    }

    /**
     * Symbol for any variable declared. Variables are handled like sub-scopes.
     *
     * @param identifier      identifier of the variable
     * @param scope           declaration scope of the variable
     * @param type            type of the variable
     * @param lineInformation line information about the variable
     */
    private record VariableSymbol(
            @NotNull IdentifierNode identifier,
            @NotNull Scope scope,
            @NotNull BaallType type,
            @NotNull LineInformation lineInformation
    ) implements SymbolTableEntry, NamedSymbol {

        @Override
        public boolean isValueConstant() {
            return false;
        }

        @Override
        public @NotNull String description() {
            return "variable symbol '%s' with type '%s' at line %s".formatted(
                    identifier.getDisplayDescriptor(),
                    type,
                    lineInformation
            );
        }

        @Override
        public @NotNull String toString() {
            return description();
        }
    }

    private record FieldSymbol(
            @NotNull IdentifierNode identifier,
            @NotNull Scope scope,
            @NotNull BaallType type,
            @NotNull LineInformation lineInformation
    ) implements SymbolTableEntry, NamedSymbol {
        @Override
        public boolean isValueConstant() {
            return true;
        }

        @Override
        public @NotNull String description() {
            return "variable symbol '%s' with type '%s' at line %s".formatted(
                    identifier.getDisplayDescriptor(),
                    type,
                    lineInformation
            );
        }

        @Override
        public @NotNull String toString() {
            return description();
        }
    }

    /**
     * Record depicting an unknown symbol. This record should only be used when a symbol cannot be resolved.
     *
     * @param scope  scope in which the symbol could not be resolved
     * @param type   always a user-defined type with an unknown reference
     * @param reason reason for the unknown symbol; can differ from the reason provided in the unknown type reference
     */
    record UnknownSymbol(
            @NotNull Scope scope,
            @NotNull BaallType type,
            @NotNull String reason
    ) implements Symbol {

        @Override
        public IdentifierNode identifier() {
            return null;
        }

        @Override
        public boolean isValueConstant() {
            return true;
        }

        @Override
        public @NotNull LineInformation lineInformation() {
            return LineInformation.UNKNOWN;
        }

        @Override
        public @NotNull String description() {
            return "Unknown symbol in scope '%s' because: %s".formatted(scope, reason);
        }

        @Override
        public @NotNull String toString() {
            return description();
        }
    }

    /**
     * Table where symbols and constant function declarations are stored. A table is always associated with a scope. It
     * should be ensured that there is a one-to-one mapping between scopes and tables within a symbol table instance.
     */
    private final static class Page implements SymbolTableEntry {

        /**
         * major scope. todo better typing?
         */
        private final Scope own;

        /**
         * null when root
         */
        private final @Nullable SymbolTable.Page parent;
        /**
         * use-before-declare
         */
        private final List<ConstantSymbol> constantSymbols = new ArrayList<>();
        private final List<SymbolTableEntry> symbols = new ArrayList<>();

        private Page(Scope own, @Nullable SymbolTable.Page parent) {
            this.own = own;
            this.parent = parent;
        }

        /**
         * Creates a stream of all declared symbols and functions from this table. Excludes nested tables.
         *
         * @return stream of all declared symbols and functions from this table; the order of the returned stream is
         * always the order of registration with constant function declarations leading because of use-before-declare
         */
        Stream<NamedSymbol> stream() {
            return Stream.concat(
                    constantSymbols.stream(),
                    symbols.stream()
                           .filter(NamedSymbol.class::isInstance)
                           .map(NamedSymbol.class::cast)
            );
        }

        /**
         * Creates a stream of all declared symbols from this table.
         *
         * @return stream of all declared symbols from this table; the order of the symbols is equal to the order of
         * registration
         */
        Stream<SymbolTableEntry> symbolDeclarations() {
            return symbols.stream();
        }

        /**
         * Creates a stream of all declared function declarations from this table.
         *
         * @return stream of all declared function declarations from this table; the order of the symbols is equal to
         * the order of registration
         */
        Stream<ConstantSymbol> constantDeclarations() {
            return constantSymbols.stream();
        }

        /**
         * Creates a stream of all declared symbols and functions from this table and all nested tables.
         *
         * @return stream of all declared symbols and functions from this table and all nested tables; the order of the
         * returned stream is always the order of registration with constant function declarations leading because of
         * use-before-declare; each flattened table's symbols are inserted at their position in the declaration order
         */
        Stream<NamedSymbol> streamFlattened() {
            Stream<NamedSymbol> flattenedChildren = symbolDeclarations().flatMap(e -> switch (e) {
                case Page page -> page.streamFlattened();
                case ConstantSymbol constantSymbol -> Stream.of(constantSymbol);
                case VariableSymbol variableSymbol -> Stream.of(variableSymbol);
                case FieldSymbol fieldSymbol -> Stream.of(fieldSymbol);
            });
            return Stream.concat(constantDeclarations(), flattenedChildren);
        }
    }
}