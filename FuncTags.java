package jtags;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;


import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuncTags {

    HashMap<String,List<FuncProperty>> funcPool;
    //HashMap<String,String> apiPool;
    FuncTags(){
        funcPool = new HashMap<>();
       // apiPool = new HashMap<>();
    }

    public void getFuncs(String projectpath){
        File pro = new File(projectpath);
        if (pro.exists()) {
            List imports = new ArrayList();
            File[] files = pro.listFiles();
            if (null == files || files.length == 0) {
                //System.out.println("文件夹是空的!");
                return;
            } else {

                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        continue;
                        //String subfolder = file2.getAbsolutePath();
                        //getFuncs(subfolder);
                    }
                    else{
                        if(file2.getName().endsWith(".java")){
                            try{
                                String source = FileIO.readStringFromFile(file2.getAbsolutePath());
                                CompilationUnit compilationUnit = JavaParser.parse(source);
                                buildFuncPool(compilationUnit, file2.getAbsolutePath());
                               // imports.addAll(getImports(compilationUnit));

                            }catch (Exception e ){
                                System.out.println(e);
                                return;
                            }

                        }

                    }
                }
//                if(imports.size()!=0)
//                    buildApiPool(imports);
            }

        }

    }










    public void buildFuncPool(Node node, String filepath){



        if(node == null) return;
//        if(node instanceof ClassOrInterfaceDeclaration){
//            String name = ((ClassOrInterfaceDeclaration)node).getNameAsString();
//            String range = node.getRange().toString();
//            FuncProperty fp = new FuncProperty(filepath,range);
//            if(funcPool.containsKey(name)){
//                funcPool.get(name).add(fp);
//            }else {
//                List<FuncProperty> fl = new ArrayList<>();
//                fl.add(fp);
//                funcPool.put(name,fl);
//            }
//
//        }
        if (node instanceof MethodDeclaration){
            String name = ((MethodDeclaration)node).getNameAsString();
            String range = node.getRange().toString();
            FuncProperty fp = new FuncProperty(filepath,range,node);
            if(funcPool.containsKey(name)){
                funcPool.get(name).add(fp);
            }else {
                List<FuncProperty> fl = new ArrayList<>();
                fl.add(fp);
                funcPool.put(name,fl);
            }

        }

            List nodes = node.getChildNodes();
            for (Object o : nodes) {
                buildFuncPool((Node)o, filepath);
            }

    }



    public static void main(String[] args) {
            String projectpath = "/home/bohong/文档/myapi_analyser/src/jtags";
            FuncTags ftags = new FuncTags();
            ftags.getFuncs(projectpath);
            System.out.println();
    }
}
