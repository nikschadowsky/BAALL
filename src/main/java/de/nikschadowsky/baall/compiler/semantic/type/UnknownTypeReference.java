package de.nikschadowsky.baall.compiler.semantic.type;

public final class UnknownTypeReference implements TypeReference {
    private final String message;

    UnknownTypeReference(String message) {
        this.message = message;
    }

    @Override
    public Kind getKind() {
        return Kind.UNKNOWN;
    }

    public String getReason() {
        return message;
    }

    @Override
    public String toString() {
        return "UnknownTypeReference[" + "reason=" + message + ']';
    }

}
