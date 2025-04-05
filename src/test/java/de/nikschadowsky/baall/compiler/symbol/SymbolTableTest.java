package de.nikschadowsky.baall.compiler.symbol;

import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.BaallType;
import de.nikschadowsky.baall.compiler.semantic.type.BaallTypeFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static de.nikschadowsky.baall.compiler._utility.BaseAssertion.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                true,
                new LineInformation(100, 4)
        )).doesNotThrowAnyException();
        // registering a symbol with the same name but with another type is valid
        assertThatCode(() -> symbolTable.registerSymbol(
                "identifier",
                Scope.create(Scope.ROOT),
                true,
                new LineInformation(100, 4)
        )).doesNotThrowAnyException();

        // empty since we did not update the type information yet
        Assertions.assertThat(symbolTable.getTypeInformation("identifier", Scope.ROOT)).isEmpty();
        // empty since there is no such entry
        Assertions.assertThat(symbolTable.getTypeInformation("invalid_identifier", Scope.ROOT)).isEmpty();

        assertThatCode(() -> symbolTable.updateTypeInformation(
                "identifier",
                Scope.ROOT,
                expectedType
        )).doesNotThrowAnyException();

        Assertions.assertThat(symbolTable.getTypeInformation("identifier", Scope.ROOT))
                  .isPresent()
                  .get()
                  .isEqualTo(expectedType);

        assertThatThrownBy(() -> symbolTable.registerSymbol(
                "identifier",
                Scope.ROOT,
                true,
                new LineInformation(0, 0)
        )).isInstanceOf(SymbolAlreadyExistsException.class);
    }

    @Test
    void hasSymbolRegisteredInScope() throws SymbolAlreadyExistsException {
        SymbolTable symbolTable = new SymbolTable();
        Scope scope = Scope.create(Scope.ROOT);
        symbolTable.registerSymbol("identifier", scope, true, new LineInformation(0, 0));

        assertThat(symbolTable).hasSymbolRegistered("identifier", scope)
                               .doesNotHaveSymbolRegistered("not_registered_identifier", scope);
    }

    @Test
    void isConstant() throws NoSymbolFoundException, SymbolAlreadyExistsException {
        SymbolTable symbolTable = new SymbolTable();
        Scope scope = Scope.create(Scope.ROOT);
        symbolTable.registerSymbol("identifier", scope, true, new LineInformation(0, 0));
        assertThat(symbolTable).isConstant("identifier", scope);
        assertThatThrownBy(() -> symbolTable.isConstant("invalid", scope)).isInstanceOf(NoSymbolFoundException.class);
    }
}