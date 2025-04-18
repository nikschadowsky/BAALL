package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.semantic.type.FunctionType;
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
            String identifier,
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
            String identifier,
            Scope scope,
            FunctionType type,
            boolean isConstant,
            LineInformation lineInformation
    ) throws SymbolAlreadyExistsException {
        if (hasSymbolRegisteredInScope(identifier, scope)) {
            throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
        }
        findOrCreateNode(scope).functionDeclarations.add(new Function(
                identifier,
                scope,
                type,
                isConstant,
                lineInformation
        ));
    }

    public boolean hasSymbolRegisteredInScope(String identifier, Scope scope) {
        Node node = nodes.get(scope);
        if (node == null) {
            return false;
        }
        if (node.functionDeclarations.stream().anyMatch(symbol -> symbol.identifier().equals(identifier))) {
            return true;
        }
        return node.symbols.stream()
                           .filter(e -> e instanceof Symbol)
                           .anyMatch(e -> ((Symbol) e).identifier().equals(identifier));
    }

    public boolean isConstant(String identifier, Scope scope) throws NoSymbolFoundException {
        Node node = nodes.get(scope);
        if (node == null) {
            throw new NoSymbolFoundException(NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(identifier, scope));
        }

        return node.stream()
                   .filter(s -> s.identifier().equals(identifier) && s.scope().equals(scope))
                   .findFirst()
                   .map(Symbol::isConstant)
                   .orElseThrow(() -> new NoSymbolFoundException(NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(
                           identifier,
                           scope
                   )));
    }

    public Optional<BaallType> getTypeInformation(String identifier, Scope scope) {
        // todo implement
        return Optional.empty();
    }

    private Node findOrCreateNode(Scope scope) {
        return Optional.ofNullable(nodes.get(scope)).orElseGet(() -> {
            Node parent = findOrCreateParentNode(scope);
            Node node = new Node(scope, parent);
            if (parent != null) {
                parent.symbols.add(node);
            }
            nodes.put(scope, node);
            return node;
        });
    }

    private @Nullable Node findOrCreateParentNode(Scope scope) {
        Optional<Scope> parent = scope.getParent();
        // create or find the all parent nodes recursively
        return parent.map(this::findOrCreateNode).orElse(null);
    }

    private final LinkedHashMap<Scope, Node> nodes = new LinkedHashMap<>();

    private sealed interface Symbol permits Function, SymbolImpl {
        String identifier();

        Scope scope();

        BaallType type();

        boolean isConstant();
    }

    private sealed interface SymbolTableEntry permits SymbolImpl, Node {
    }

    private record Function(String identifier, Scope scope, FunctionType type, boolean isConstant,
                            LineInformation lineInformation) implements Symbol {
    }

    private record SymbolImpl(String identifier, Scope scope, BaallType type, boolean isConstant,
                              LineInformation lineInformation) implements SymbolTableEntry, Symbol {
    }

    private final static class Node implements SymbolTableEntry {

        private final Scope own;
        private final @Nullable Node parent;
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
