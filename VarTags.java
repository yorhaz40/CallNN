package jtags;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;


import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VarTags {
    public HashMap<String,List<VarProperty>> varPool;
   //  public HashMap<String, String> apiPool;
    //public List imports;
    VarTags(){
        varPool = new HashMap<>();
        //imports = new ArrayList();
        //apiPool = new HashMap<>();
    }

    public static void main(String[] args) {
        String file = "/home/bohong/文档/myapi_analyser/src/jtags/CallTags.java";
        VarTags vtag = new VarTags();
        vtag.getVars(file);
        System.out.println();
    }

    public  void getVars(String filepath){
        File file = new File(filepath);
        String source = FileIO.readStringFromFile(file.getAbsolutePath());
        try {
            CompilationUnit compilationUnit = JavaParser.parse(source);
            buildVarPool(compilationUnit);
          //  imports.addAll(compilationUnit.getImports());
           // buildApiPool(compilationUnit);
        }catch (Exception e)
        {
            System.out.println(e);

        }



    }







    public void buildVarPool(Node node){
        if(node == null) return;
        else if(node instanceof FieldDeclaration){
            List names = ((FieldDeclaration)node).getVariables();
            //Node pnode = node.getParentNode().get();
            String range = node.getRange().toString();
            for (Object name : names) {
                String vname = ((VariableDeclarator)name).getName().toString();
                String ctype = ((VariableDeclarator)name).getTypeAsString();
                VarProperty vp = new VarProperty(range,ctype);
                if(varPool.containsKey(vname)){
                    varPool.get(vname).add(vp);
                }else {
                    List<VarProperty> vl = new ArrayList<>();
                    vl.add(vp);
                    varPool.put(vname,vl);
                }
            }
        }
        else if (node instanceof VariableDeclarationExpr){
            String range = node.getRange().toString();
            List vnames = ((VariableDeclarationExpr) node).getVariables();
            for (Object vn : vnames) {
                String vname = ((VariableDeclarator)vn).getName().toString();
                String ctype = ((VariableDeclarator)vn).getTypeAsString();
                VarProperty vp = new VarProperty(range,ctype);
                if(varPool.containsKey(vname)){
                    varPool.get(vname).add(vp);
                }else {
                    List<VarProperty> vl = new ArrayList<>();
                    vl.add(vp);
                    varPool.put(vname,vl);
                }
            }
        }
        else if (node instanceof Parameter){
            String vname = ((Parameter)node).getNameAsString();
            String ctype = ((Parameter)node).getTypeAsString();
            String range = node.getRange().toString();
            VarProperty vp = new VarProperty(range,ctype);
            if(varPool.containsKey(vname)){
                varPool.get(vname).add(vp);
            }else {
                List<VarProperty> vl = new ArrayList<>();
                vl.add(vp);
                varPool.put(vname,vl);
            }
        }
        else{

            List nodes = node.getChildNodes();
            for (Object o : nodes) {
                buildVarPool((Node)o);
            }
        }

    }
}
