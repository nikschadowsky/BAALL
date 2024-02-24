package de.nikschadowsky.baall.compiler._utility;

import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * File created on 09.02.2024
 */
public class Assertions {

    /**
     * assert whether both collections have the same number of elements and all collection elements from c1 have a
     * partner equal to them in collection c2. This means that for every element o1 from c1 there should be an element
     * o2 from c2 with o1.equals(o2).
     *
     * @param c1 expected
     * @param c2 actual
     */
    public static <T> void assertCollectionShallowEquals(Collection<T> c1, Collection<T> c2) {
        if ((c1 == null && c2 != null) || (c1 != null && c2 == null)) fail();

        if (Objects.equals(c1, c2)) return;

        if (c1.size() != c2.size()) {
            fail("Collections have different sizes! %n Expected %s %n%n but got %s".formatted(c1.size(), c2.size()));
        }

        for (T o1 : c1) {
            boolean found = false;
            for (T o2 : c2) {
                if (Objects.equals(o1, o2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("No matching element found for %s! %n Expected %s %n%n but got %s".formatted(o1, c1, c2));
            }
        }
    }
}
