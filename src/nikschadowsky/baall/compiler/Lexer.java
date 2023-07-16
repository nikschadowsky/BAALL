package nikschadowsky.baall.compiler;


public class Lexer {

    private final String path;

    private static final String SINGLE_LINE_COMMENT_REGEX = "(//.*\\R)";
    private static final String BLOCK_COMMENT_REGEX = "(/\\*(.|\\s)*?\\*/)";
    private static final String WHITESPACE_REGEX = "\\s+";

    private String content;

    public Lexer(String path) {
        this.path = path;
    }

    public Lexer readContent() {
        content = FileLoader.loadFileContent(path);

        return this;
    }

    public Lexer removeComments() {


        boolean notInString = true;

        if (content.length() > 1) {
            int i = 1;
            while (i < content.length()) {

                if (content.charAt(i) == '\"') {
                    notInString ^= true;
                } else if (notInString && content.substring(i - 1, i + 1).equals("//")) {
                    content = content.substring(0, i - 1) + content.substring(i - 1).replaceFirst(SINGLE_LINE_COMMENT_REGEX, "");
                } else if (notInString && content.substring(i - 1, i + 1).equals("/*")) {
                    content = content.substring(0, i - 1) + content.substring(i - 1).replaceFirst(BLOCK_COMMENT_REGEX, "");
                }

                i++;
            }
        }
        return this;
    }

    public Lexer replaceAllWhitespace() {
        boolean notInString = true;
        boolean notInCharacter = true;

        if (content.length() > 1) {
            int i = 0;
            while (i < content.length()) {

                char currentChar = content.charAt(i);

                if (notInCharacter && currentChar == '\"') {
                    notInString ^= true;
                } else if (notInString && currentChar == '\'') {
                    notInCharacter ^= true;
                }else if(notInString && notInCharacter && Character.isWhitespace(currentChar)){



                    content = content.substring(0, i) + content.substring(i).replaceFirst(WHITESPACE_REGEX, " ");
                }

                i++;
            }

        }
        content = content.trim();

        return this;
    }

    public String getContent() {
        return content;
    }
}
