package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.util.LanguageElement;
import de.nikschadowsky.baall.compiler.util.SyntaxSet;

import java.util.Arrays;

/**
 * @since 09.03.2025
 */
public sealed interface PrimitiveTypeNode extends SimpleTypeNode permits PrimitiveTypeNodeImpl {

    Kind getKind();

    enum Kind {
        STRING(SyntaxSet.LANGUAGE_ELEMENTS.get("string")),
        NUMBER(SyntaxSet.LANGUAGE_ELEMENTS.get("number")),
        BOOLEAN(SyntaxSet.LANGUAGE_ELEMENTS.get("boolean")),
        STRUCT(SyntaxSet.LANGUAGE_ELEMENTS.get("struct")),
        EXCEPTION(SyntaxSet.LANGUAGE_ELEMENTS.get("exception"));

        private final LanguageElement mapping;

        Kind(LanguageElement mapping) {
            this.mapping = mapping;
        }

        public static Kind findMapping(Token token) {
            return Arrays.stream(values()).filter(kind -> kind.mapping.matches(token)).findFirst().orElseThrow();
        }
    }
}
