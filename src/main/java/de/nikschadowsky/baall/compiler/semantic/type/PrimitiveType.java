package de.nikschadowsky.baall.compiler.semantic.type;

import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.typing.PrimitiveTypeNode;
import org.jetbrains.annotations.NotNull;

public sealed interface PrimitiveType extends SimpleType permits PrimitiveTypeImpl {

    @NotNull Kind getKind();

    enum Kind {
        NUMBER(PrimitiveTypeNode.Kind.NUMBER),
        STRING(PrimitiveTypeNode.Kind.STRING),
        BOOLEAN(PrimitiveTypeNode.Kind.BOOLEAN),
        STRUCT(PrimitiveTypeNode.Kind.STRUCT),
        EXCEPTION(PrimitiveTypeNode.Kind.EXCEPTION);

        private final PrimitiveTypeNode.Kind kind;

        Kind(PrimitiveTypeNode.Kind kind) {
            this.kind = kind;
        }

        public PrimitiveTypeNode.Kind getMapping() {
            return kind;
        }
    }

}
