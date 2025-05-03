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
        STRING(SyntaxSet.LANGUAGE_ELEMENTS.get("string"), "string"),
        NUMBER(SyntaxSet.LANGUAGE_ELEMENTS.get("number"), "number"),
        BOOLEAN(SyntaxSet.LANGUAGE_ELEMENTS.get("boolean"), "boolean"),
        STRUCT(SyntaxSet.LANGUAGE_ELEMENTS.get("struct"), "struct"),
        EXCEPTION(SyntaxSet.LANGUAGE_ELEMENTS.get("exception"), "exception");

        private final LanguageElement mapping;
        private final String displayDescriptor;

        Kind(LanguageElement mapping, String displayDescriptor) {
            this.mapping = mapping;
            this.displayDescriptor = displayDescriptor;
        }

        public static Kind findMapping(Token token) {
            return Arrays.stream(values()).filter(kind -> kind.mapping.matches(token)).findFirst().orElseThrow();
        }

        public String getDisplayDescriptor() {
            return displayDescriptor;
        }
    }
}
