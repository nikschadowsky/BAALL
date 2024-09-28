package de.nikschadowsky.baall.compiler.symbol;


import java.util.Objects;

/**
 * @since 28.09.2024
 */
public class TokenQueueId {

    public static TokenQueueId of(String id) {
        return new TokenQueueId(id);
    }

    private final String id;

    private TokenQueueId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenQueueId that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return id;
    }
}
