package de.nikschadowsky.baall.compiler._utility;


import de.nikschadowsky.baall.compiler.semantic.attribute.Scope;
import de.nikschadowsky.baall.compiler.semantic.type.TypeTable;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.access.IdentifierNode;

/**
 * @since 31.03.2025
 */
public class TypeTableAssertion extends BaseAssertion<TypeTableAssertion, TypeTable> {

    public TypeTableAssertion(TypeTable actual) {
        super(actual, TypeTableAssertion.class);
    }

    public TypeTableAssertion hasTypeRegistered(String identifier, Scope scope) {
        return hasTypeRegistered(AstTestBuilder.identifier(identifier), scope);
    }

    public TypeTableAssertion hasTypeRegistered(IdentifierNode identifier, Scope scope) {
        return truthinessAssert(
                t -> "TypeTable does not have type registered for identifier '%s' in scope '%s'".formatted(
                        identifier,
                        scope
                ),
                t -> t.hasTypeRegistered(identifier, scope)
        );
    }

    public TypeTableAssertion doesNotHaveTypeRegistered(String identifier, Scope scope) {
        return doesNotHaveTypeRegistered(AstTestBuilder.identifier(identifier), scope);
    }

    public TypeTableAssertion doesNotHaveTypeRegistered(IdentifierNode identifier, Scope scope) {
        return falsenessAssert(
                t -> "TypeTable does have type registered for identifier '%s' in scope '%s'".formatted(
                        identifier,
                        scope
                ),
                t -> t.hasTypeRegistered(identifier, scope)
        );
    }
}
