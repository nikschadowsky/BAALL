package de.nikschadowsky.baall.compiler.output;


/**
 * @since 22.01.2025
 */
public class BaallFileReference {

    private final String fileName;

    public BaallFileReference(String fileName) {
        this.fileName = fileName;
    }

    public BaallFile resolve() {
        // locate and parse BaallFile
        return null;
    }

    public String getFileName() {
        return fileName;
    }
}
