package de.nikschadowsky.baall.compiler.semantic.type;

import org.jetbrains.annotations.NotNull;

enum PrimitiveTypeImpl implements PrimitiveType {

    NUMBER_TYPE {
        @Override
        public @NotNull Kind getKind() {
            return Kind.NUMBER;
        }
    },

    STRING_TYPE {
        @Override
        public @NotNull Kind getKind() {
            return Kind.STRING;
        }
    },

    BOOLEAN_TYPE {
        @Override
        public @NotNull Kind getKind() {
            return Kind.BOOLEAN;
        }
    },

    EXCEPTION_TYPE {
        @Override
        public @NotNull Kind getKind() {
            return Kind.EXCEPTION;
        }
    },

    STRUCT_TYPE {
        @Override
        public @NotNull Kind getKind() {
            return Kind.STRUCT;
        }
    };

}
