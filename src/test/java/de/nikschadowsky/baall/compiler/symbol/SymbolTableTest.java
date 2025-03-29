package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.semantic.type.BaallTypeFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @since 26.03.2025
 */
class SymbolTableTest {

    private final BaallType expectedType = BaallTypeFactory.create().createUserDefinedType();

    @Test
    void registerSymbolAndRetrieveSymbol() {
        SymbolTable symbolTable = new SymbolTable();
        assertThatCode(() -> symbolTable.registerSymbol(
                "identifier",
                Scope.ROOT,
                new LineInformation(100, 4)
        )).doesNotThrowAnyException();
        // registering a symbol with the same name but with another type is valid
        assertThatCode(() -> symbolTable.registerSymbol(
                "identifier",
                Scope.create(Scope.ROOT),
                new LineInformation(100, 4)
        )).doesNotThrowAnyException();

        // empty since we did not update the type information yet
        assertThat(symbolTable.getTypeInformation("identifier", Scope.ROOT)).isEmpty();
        // empty since there is no such entry
        assertThat(symbolTable.getTypeInformation("invalid_identifier", Scope.ROOT)).isEmpty();

        assertThatCode(() -> symbolTable.updateTypeInformation(
                "identifier",
                Scope.ROOT,
                expectedType
        )).doesNotThrowAnyException();

        assertThat(symbolTable.getTypeInformation("identifier", Scope.ROOT)).isPresent().get().isEqualTo(expectedType);

        assertThatThrownBy(() -> symbolTable.registerSymbol(
                "identifier",
                Scope.ROOT,
                new LineInformation(0, 0)
        )).isInstanceOf(SymbolAlreadyExistsException.class);
    }

    @Test
    void hasSymbolRegisteredInScope() throws SymbolAlreadyExistsException {
        SymbolTable symbolTable = new SymbolTable();
        Scope scope = Scope.create(Scope.ROOT);
        symbolTable.registerSymbol("identifier", scope, new LineInformation(0, 0));

        assertThat(symbolTable.hasSymbolRegisteredInScope("identifier", scope)).isTrue();
        assertThat(symbolTable.hasSymbolRegisteredInScope("not_registered_identifier", scope)).isFalse();
    }
}