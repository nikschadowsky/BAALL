package de.nikschadowsky.baall.compiler.util;

import de.nikschadowsky.baall.compiler.semantic.type.FunctionType;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * @since 15.02.2024
 */
public class CollectionUtility {

    private CollectionUtility() {
    }

    // TODO refactor
    public static String getNextThreeQueueSymbols(Queue<?> queue) {
        return queue.stream().limit(3).toList().toString();
    }

    public static <T> boolean shallowCompareCollections(Collection<? extends T> c1, Collection<? extends T> c2) {
        return shallowCompareCollections(c1, c2, Objects::equals);
    }

    public static <T> boolean shallowCompareCollections(
            Collection<? extends T> c1,
            Collection<? extends T> c2,
            BiPredicate<T, T> comparePredicate
    ) {
        if ((c1 == null && c2 != null) || (c1 != null && c2 == null)) return false;

        if (Objects.equals(c1, c2)) return true;

        if (c1.size() != c2.size()) {
            return false;
        }

        for (T o1 : c1) {
            boolean found = false;
            for (T o2 : c2) {
                if (comparePredicate.test(o1, o2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public static <S, T> List<T> duplicates(Collection<S> collection, Function<S, T> equalityMapper) {
        Set<T> set = new HashSet<>();
        List<T> duplicates = new ArrayList<>();
        collection.stream().map(equalityMapper).forEach(
                e -> {
                    if (!set.add(e)) {
                        duplicates.add(e);
                    }
                }
        );
        return duplicates;
    }

}
