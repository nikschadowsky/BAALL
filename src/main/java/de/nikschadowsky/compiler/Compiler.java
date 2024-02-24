package de.nikschadowsky.compiler;

import de.nikschadowsky.compiler.lexer.Lexer;
import de.nikschadowsky.compiler.util.FileLoader;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Logger;

public class Compiler {

    private final Logger logger = Logger.getLogger("Compiler");

    public static void main(String[] args) {
        new Lexer(FileLoader.getPathFromFileSystem(getInputPath(args)));

    }

    private Compiler(Path source, Path destination) {
        logger.info("Compiling started.");

        //Program program = new Program();

    }

    private static String getInputPath(String[] args) {

        String path;

        if (args.length >= 2) {
            path = args[0];
        } else {
            System.out.println("Path of Source File: ");
            Scanner sc = new Scanner(System.in);
            path = sc.next();
        }
        return path;

    }
}
