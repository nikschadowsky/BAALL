package de.nikschadowsky.baall.compiler.symbol;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LineInformationTest {

    @Test
    void compareTo() {
        LineInformation lineInformation1 = new LineInformation(1, 1);
        LineInformation lineInformation2 = new LineInformation(1, 1);
        LineInformation lineInformation3 = new LineInformation(1, 2);
        LineInformation lineInformation4 = new LineInformation(2, 1);
        LineInformation lineInformation5 = new LineInformation(2, 2);

        assertThat(lineInformation1).isEqualByComparingTo(lineInformation2)
                                    .isLessThan(lineInformation3)
                                    .isLessThan(lineInformation4)
                                    .isLessThan(lineInformation5);

        assertThat(lineInformation2).isEqualByComparingTo(lineInformation1).isLessThan(lineInformation3);
        assertThat(lineInformation3).isGreaterThan(lineInformation1)
                                    .isGreaterThan(lineInformation2)
                                    .isLessThan(lineInformation4);
    }
}