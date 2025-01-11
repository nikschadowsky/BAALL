package de.nikschadowsky.baall.compiler.files;


import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler.Step;
import de.nikschadowsky.baall.compiler.StepOptions;
import de.nikschadowsky.baall.compiler.util.FileLoader;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

/**
 * @since 11.01.2025
 */
public class FileLineReaderStep extends Step<Path, List<String>> {

    public FileLineReaderStep(@Nullable StepOptions options) {
        super(options);
    }

    @Override
    public List<String> executeStep(Path path, CompileInformation compileInformation) {
        String content = FileLoader.getFileContent(path);

        compileInformation.add("Source Code", content);

        return content.lines().toList();
    }
}
