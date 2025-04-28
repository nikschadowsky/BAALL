package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.semantic.type.FunctionType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ElementAccessNode;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.ScopeElevationNode;
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

    public void registerSymbol(
            SymbolTableIdentifier identifier,
            Scope scope,
            BaallType type,
            boolean isConstant,
            LineInformation lineInformation
    ) throws SymbolAlreadyExistsException {
        if (hasSymbolRegisteredInScope(identifier, scope)) {
            throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
        }
        findOrCreateNode(scope).symbols.add(new SymbolImpl(identifier, scope, type, isConstant, lineInformation));
    }

    public void registerFunction(
            StringIdentifier identifier,
            Scope scope,
            FunctionType type,
            boolean isConstant,
            LineInformation lineInformation
    ) throws SymbolAlreadyExistsException {
        if (hasSymbolRegisteredInScope(identifier, scope)) {
            throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
        }
        Node node = findOrCreateNode(scope);
        if (isConstant) {
            node.functionDeclarations.add(new Function(identifier, scope, type, true, lineInformation));
        } else {
            node.symbols.add(new SymbolImpl(identifier, scope, type, false, lineInformation));
        }
    }

    /**
     * Checks if a symbol with the same identifier is registered. A symbol is regarded as registered in the same scope
     * if it can be accessed directly from the passed scope. This means if you pass a minor scope and there is a symbol
     * in the scope hierarchy up to the nearest major scope (inclusive), this method will return true.
     *
     * @param identifier identifier to check
     * @param scope      scope of access
     * @return if there is a symbol accessible from this scope with the same identifier
     */
    public boolean hasSymbolRegisteredInScope(SymbolTableIdentifier identifier, Scope scope) {
        return findNode(scope).map(node -> node.stream()
                                               .filter(s -> scope.canAccess(s.scope()))
                                               .anyMatch(s -> s.identifier().equals(identifier)))
                              .orElse(false);
    }


    public boolean isConstant(SymbolTableIdentifier identifier, Scope scope) throws NoSymbolFoundException {
        Node node = findNode(scope).orElseThrow(
                () -> new NoSymbolFoundException(NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(identifier, scope))
        );

        return node.stream()
                   .filter(s -> s.identifier().equals(identifier) && s.scope().equals(scope))
                   .findFirst()
                   .map(Symbol::isConstant)
                   .orElseThrow(() -> new NoSymbolFoundException(NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(
                           identifier,
                           scope
                   )));
    }

    public Optional<BaallType> resolveSymbol(ElementAccessNode elementAccess, Scope scope) {
        /*
         * todo register fields and parameters!
         * todo change "id" from string to complex datatype. field access of structs and lists require this change
         * scope elevation restricts scope.
         * member select can either be struct access or namespace access
         * search from current scope upwards until a symbol is found.
         */


        // todo implement
        return Optional.empty();
    }

    private Scope resolveScope(Scope base, ScopeElevationNode scopeElevationNode) throws OutOfScopeException {
        return null;
    }

    private Node findOrCreateNode(Scope scope) {
        Scope major = getMajor(scope);
        return findNode(major).orElseGet(() -> {
            Node parent = findOrCreateParentNode(major);
            Node node = new Node(major, parent);
            if (parent != null) {
                parent.symbols.add(node);
            }
            nodes.put(major, node);
            return node;
        });
    }

    /**
     * Finds a node in the set of nodes. It uses the passed scope to determine the next major scope.
     *
     * @param scope major or minor scope
     * @return optional node of the passed scope's nearest major
     */
    private Optional<Node> findNode(Scope scope) {
        Scope major = getMajor(scope);
        return Optional.ofNullable(nodes.get(major));
    }

    private @Nullable Node findOrCreateParentNode(Scope scope) {
        Optional<Scope> parent = scope.getParent();
        // create or find the all parent nodes recursively
        return parent.map(this::findOrCreateNode).orElse(null);
    }

    private Scope getMajor(Scope scope) {
        if (scope.isMajor()) {
            return scope;
        }
        return getMajor(scope.getParent().orElseThrow());
    }

    private final LinkedHashMap<Scope, Node> nodes = new LinkedHashMap<>();

    private sealed interface Symbol permits Function, SymbolImpl {
        SymbolTableIdentifier identifier();

        Scope scope();

        BaallType type();

        boolean isConstant();
    }

    private sealed interface SymbolTableEntry permits SymbolImpl, Node {
    }

    private record Function(StringIdentifier identifier, Scope scope, FunctionType type, boolean isConstant,
                            LineInformation lineInformation) implements Symbol {
    }

    private record SymbolImpl(SymbolTableIdentifier identifier, Scope scope, BaallType type, boolean isConstant,
                              LineInformation lineInformation) implements SymbolTableEntry, Symbol {
    }

    private final static class Node implements SymbolTableEntry {

        /**
         * major scope. todo better typing?
         */
        private final Scope own;
        /*
        Node represents a major scope. Finding or creating a node uses a major scope.
        Has symbol registered looks at a node and to find the symbol
         */
        private final @Nullable Node parent;
        /**
         * use-before-declare
         */
        private final List<Function> functionDeclarations = new ArrayList<>();

        private final List<SymbolTableEntry> symbols = new ArrayList<>();

        private Node(Scope own, @Nullable Node parent) {
            this.own = own;
            this.parent = parent;
        }

        Stream<Symbol> stream() {
            return Stream.concat(functionDeclarations.stream(), symbols.stream())
                         .filter(Symbol.class::isInstance)
                         .map(Symbol.class::cast);
        }
    }
}
