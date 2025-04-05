package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class SymbolTable {

    private static final String NO_SYMBOL_EXCEPTION_TEMPLATE =
            "There is no symbol with the provided identifier '%s' in the provided scope '%s'";
    private static final String SYMBOL_ALREADY_REGISTERED_TEMPLATE =
            "There is already a symbol registered with identifier '%s'";

    private final Map<SymbolTableKey, SymbolTableValue> table = new HashMap<>();

    public void registerSymbol(String identifier, BaallType type, Scope scope, boolean isConstant, LineInformation lineInformation) throws SymbolAlreadyExistsException {
        if (hasSymbolRegisteredInScope(identifier, scope)) {
            throw new SymbolAlreadyExistsException(SYMBOL_ALREADY_REGISTERED_TEMPLATE.formatted(identifier));
        }
        table.put(new SymbolTableKey(identifier, scope), new SymbolTableValue(type, lineInformation, isConstant));
    }

    public boolean hasSymbolRegisteredInScope(String identifier, Scope scope) {
        return table.containsKey(new SymbolTableKey(identifier, scope));
    }

    public boolean isConstant(String identifier, Scope scope) throws NoSymbolFoundException {
        return table.keySet()
                    .stream()
                    .filter(equalsKey(identifier, scope))
                    .map(table::get)
                    .map(SymbolTableValue::isConstant)
                    .findFirst()
                    .orElseThrow(() -> new NoSymbolFoundException(
                            NO_SYMBOL_EXCEPTION_TEMPLATE.formatted(identifier, scope))
                    );
    }

    public Optional<BaallType> getTypeInformation(String identifier, Scope scope) {
        return table.keySet()
                    .stream()
                    .filter(equalsKey(identifier, scope))
                    .findFirst()
                    .map(table::get)
                    .map(SymbolTableValue::type);
    }

    private Predicate<SymbolTableKey> equalsKey(String identifier, Scope scope) {
        return k -> k.symbol.equals(identifier) && k.scope.equals(scope);
    }

    private record SymbolTableKey(String symbol, Scope scope) {
    }

    private record SymbolTableValue(BaallType type, LineInformation lineInformation, boolean isConstant) {
    }

    private final HashMap<Scope, Node> nodes = new HashMap<>();

    private static class Node {

        private HashMap<SymbolTableKey, SymbolTableValue> declaredFunctions;

        private LinkedHashMap<SymbolTableKey, SymbolTableValue> symbols;

        private final Node parent;

        private Node(Node parent) {
            this.parent = parent;
        }
    }

}
