package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class SymbolTable {

    private final Map<SymbolTableEntry, BaallType> table = new HashMap<>();

    public void registerSymbol(String identifier, Scope scope, LineInformation lineInformation) throws SymbolAlreadyExistsException {
        if (table.keySet().stream().anyMatch(containsFilter(identifier, scope))) {
            throw new SymbolAlreadyExistsException("There is already a symbol registered with identifier '%s'".formatted(
                    identifier));
        }
        table.put(new SymbolTableEntry(identifier, scope, lineInformation), null);
    }

    public boolean hasSymbolRegisteredInScope(String identifier, Scope scope) {
        return table.keySet().stream().anyMatch(containsFilter(identifier, scope));
    }

    public void updateTypeInformation(String identifier, Scope scope, BaallType type) throws NoSymbolFoundException {
        SymbolTableEntry key = table.keySet()
                                    .stream()
                                    .filter(containsFilter(identifier, scope))
                                    .findFirst()
                                    .orElseThrow(() -> new NoSymbolFoundException(
                                            "There is no symbol with the provided identifier '%s' in the provided scope '%s'".formatted(
                                                    identifier,
                                                    scope
                                            )));

        table.replace(key, type);
    }

    public Optional<BaallType> getTypeInformation(String identifier, Scope scope) {
        return table.keySet()
                    .stream()
                    .filter(containsFilter(identifier, scope))
                    .findFirst()
                    .map(table::get);
    }

    private Predicate<SymbolTableEntry> containsFilter(String identifier, Scope scope) {
        return e -> e.scope().equals(scope) && e.symbol.equals(identifier);
    }

    private record SymbolTableEntry(String symbol, Scope scope, LineInformation lineInformation) {
    }

}
