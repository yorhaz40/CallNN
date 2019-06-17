package jtags;
import com.github.javaparser.ast.Node;
public class Callee {
    private String name;
    private String locate;
    private String filepath;
    public Node callnode;
    Callee(String name, String locate, String filepath){
        this.name = name;
        this.locate = locate;
        this.filepath = filepath;
    }
    public void setNode(Node node){callnode = node;}
    public String getName(){return name;}
    public String getLocate(){return locate;}
    public String getFilepath(){return filepath;}

}
