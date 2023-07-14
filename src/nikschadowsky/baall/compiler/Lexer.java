package nikschadowsky.baall.compiler;

public class Lexer {

    private final String path;

    private String content;

    public Lexer(String path){
        this.path = path;
    }

    public void readContent(){
        content = FileLoader.loadFileContent(path);


    }

    private void removeComments(String content){

    }

}
