package de.nikschadowsky.baall.compiler.lexer;


import de.nikschadowsky.baall.compiler.util.FileLoader;
import de.nikschadowsky.baall.compiler.util.RegexFactory;

public class Lexer {

    private final String path;

    private String content;

    public Lexer(String path) {
        this.path = path;
    }

    public Lexer readContent() {
        content = FileLoader.loadFileContent(path);

        return this;
    }

    public Lexer removeComments() {

        // Damn that code sux, maybe someday ill rework this properly
        boolean notInString = true;

        if (content.length() > 1) {
            int i = 1;
            while (i < content.length()) {

                if (content.charAt(i) == '\"') {
                    notInString ^= true;
                } else if (notInString && content.substring(i - 1, i + 1).equals("//")) {
                    content = content.substring(0, i - 1) + content.substring(i - 1).replaceFirst(RegexFactory.SINGLE_LINE_COMMENT_REGEX, "");
                } else if (notInString && content.substring(i - 1, i + 1).equals("/*")) {
                    content = content.substring(0, i - 1) + content.substring(i - 1).replaceFirst(RegexFactory.BLOCK_COMMENT_REGEX, "");
                }

                i++;
            }
        }
        return this;
    }

    public Lexer replaceAllWhitespace() {
        boolean notInString = true;
        boolean notInCharacter = true;

        // same goes for this
        if (content.length() > 1) {
            int i = 0;
            while (i < content.length()) {

                char currentChar = content.charAt(i);

                if (notInCharacter && currentChar == '\"') {
                    notInString ^= true;
                } else if (notInString && currentChar == '\'') {
                    notInCharacter ^= true;
                } else if (notInString && notInCharacter && Character.isWhitespace(currentChar)) {


                    content = content.substring(0, i) + content.substring(i).replaceFirst(RegexFactory.WHITESPACE_REGEX, " ");
                }

                i++;
            }

        }
        content = content.trim();

        return this;
    }

    public Lexer tokenize() {


        return this;
    }

    public String getContent() {
        return content;
    }
}
