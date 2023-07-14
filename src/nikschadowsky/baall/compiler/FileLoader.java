package nikschadowsky.baall.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLoader {

    /**
     * Reads in all Contents of a File provided by a path
     * @param path
     * @return String-Representation of content
     */
    public static String loadFileContent(String path){

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Specified Input-File does not exist!");
        }


    }
}
