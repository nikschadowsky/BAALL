package de.nikschadowsky.baall.compiler.syntaxtree.cst;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * File created on 20.01.2024
 */
public class SyntaxTreeNodeAnnotation {

    private final String value;

    private final Map<String, String> properties = HashMap.newHashMap(5);

    public SyntaxTreeNodeAnnotation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public boolean setProperty(@NotNull String key, @NotNull String value) {
        return properties.put(key, value) != null;
    }

    public @Nullable String getProperty(@NotNull String key) {
        return properties.getOrDefault(key, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SyntaxTreeNodeAnnotation s)
            return getValue().equals(s.getValue());

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
