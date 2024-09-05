package de.nikschadowsky.baall.compiler.syntaxtree.util;


import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @since 04.09.2024
 */
public class NodeUtility {

    private NodeUtility() {
    }

    public static @Nullable <T> List<T> toUnmodifiableList(@Nullable List<T> list) {
        if (list == null) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

}
