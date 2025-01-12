package de.nikschadowsky.baall.compiler.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLoader {

    public static String getFileContent(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

    public static String getFileContentFromClasspath(String path) throws IOException {
        return getFileContent(getPathFromClasspath(path));
    }

    public static String getFileContentFromFileSystem(String path) throws IOException {
        return getFileContent(getPathFromFileSystem(path));
    }

    public static Path getPathFromClasspath(String path) {
        try {
            return Paths.get(ClassLoader.getSystemResource(path).toURI());
        } catch (InvalidPathException | URISyntaxException e) {
            throw new RuntimeException("Specified File '%s' does not exist in the class path!".formatted(path), e);
        }
    }

    public static Path getPathFromFileSystem(String path) {
        Path resolvedPath = Paths.get(path);

        if (!Files.exists(resolvedPath)) {
            throw new RuntimeException("Specified File '%s' does not exist in the class path!".formatted(path));
        }
        return resolvedPath;
    }

    public static String getFileExtension(@NotNull Path path) {
        String filename = path.getFileName().toString();
        return filename.substring(filename.lastIndexOf("."));
    }
}
