package de.nikschadowsky.baall.compiler.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLoader {

    /**
     * Reads in all Contents of a File provided by a path
     *
     * @param path
     * @return String-Representation of content
     */
    public static String loadFileContent(@NotNull String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Specified Input-File '%s' does not exist!".formatted(path), e);
        }
    }

    public static String getFileExtension(@NotNull String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }
}
