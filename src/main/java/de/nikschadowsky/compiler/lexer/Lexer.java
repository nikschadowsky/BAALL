package de.nikschadowsky.compiler.lexer;


import de.nikschadowsky.compiler.lexer.tokens.Token;
import de.nikschadowsky.compiler.lexer.tokens.Tokenizer;
import de.nikschadowsky.compiler.util.FileLoader;
import de.nikschadowsky.compiler.util.RegexFactory;

import java.nio.file.Path;
import java.util.List;

public class Lexer {

    private final Path path;

    private String content;

    private boolean isPreprocessed;

    public Lexer(Path path) {
        this.path = path;
        readContent();
    }

    private void readContent() {
        content = FileLoader.getFileContent(path);
    }

    private void removeComments() {

        // Damn that code sux, maybe someday ill rework this properly
        boolean notInString = true;


        if (content.length() > 1) {
            int i = 1;
            while (i < content.length()) {

                if (content.charAt(i) == '\"') {
                    notInString ^= true;
                } else if (notInString && content.substring(i - 1, i + 1).equals("//")) {
                    content = content.substring(0, i - 1) + content.substring(i - 1).replaceFirst(RegexFactory.SINGLE_LINE_COMMENT_REGEX, "\n");

                } else if (notInString && content.substring(i - 1, i + 1).equals("/*")) {
                    content = content.substring(0, i - 1) + content.substring(i - 1).replaceFirst(RegexFactory.BLOCK_COMMENT_REGEX, "\n");
                }

                i++;
            }
        }

    }

    private void replaceAllWhitespace() {
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

    }

    /**
     * Preprocesses this Lexer's content by removing Comments and replacing Whitespaces with Spaces
     */
    public void preprocessCode() {
        if (!isPreprocessed) {
            removeComments();
            replaceAllWhitespace();
        }
        isPreprocessed = true;
    }

    /**
     * Tokenizes this Lexer's content. If it wasn't preprocessed, it's preprocessed before tokenizing.
     *
     * @return List of tokens in their respective order
     */
    public List<Token> tokenize() {

        Tokenizer tokenizer = new Tokenizer(getPreprocessedCode());

        return tokenizer.run();
    }

    /**
     * Get this Lexer's preprocessed content. Preprocesses it, if it wasn't already.
     *
     * @return preprocessed content
     */
    public String getPreprocessedCode() {
        if (!isPreprocessed) {
            preprocessCode();
        }
        return content;
    }
}
