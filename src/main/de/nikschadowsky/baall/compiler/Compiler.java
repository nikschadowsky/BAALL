package de.nikschadowsky.baall.compiler;

import de.nikschadowsky.baall.compiler.lexer.Lexer;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Logger;

public class Compiler {

    private final Logger logger = Logger.getLogger("Compiler");

    public static void main(String[] args) {
        new Lexer(getInputPath(args));


    }

    private Compiler(Path source, Path destination){
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
