package jtags;

import com.github.javaparser.ast.Node;

import java.util.List;

public class FuncProperty {
    private String filepath;
    private String locaterange;
    public Node funcnode;


    FuncProperty(String filepath,String locaterange,Node funcnode) {
        this.filepath = filepath;
        this.locaterange = locaterange;
        this.funcnode = funcnode;
    }
    public String getFilepath(){return filepath;}
    public String getLocaterange(){return locaterange;}
}
