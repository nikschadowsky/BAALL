package de.nikschadowsky.baall.compiler.files;

import de.nikschadowsky.baall.compiler.util.FileLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @since 11.01.2025
 */
class FileLineReaderStepTest {

    private static final Path TEST_PATH = FileLoader.getPathFromClasspath("FileLineReaderStepTestFile.txt");

    private FileLineReaderStep fileLineReaderStep;

    @BeforeEach
    void setUp() {
        fileLineReaderStep = new FileLineReaderStep(null);
    }

    @Test
    void executeStep() {
        List<String> lines = fileLineReaderStep.executeStep(TEST_PATH, (key, value) -> {
        });

        assertThat(lines).isEqualTo(List.of(
                                            "line#1",
                                            "line#2",
                                            "line#3 has more content",
                                            "line#4 but that should not be a problem"
                                    )
        );
    }
}