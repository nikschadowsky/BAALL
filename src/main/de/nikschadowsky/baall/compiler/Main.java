package de.nikschadowsky.baall.compiler;

import de.nikschadowsky.baall.compiler.lexer.Lexer;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        new Lexer(getInputPath(args));
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
