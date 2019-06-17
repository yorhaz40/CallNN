package jtags;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataExtract {
    public static void main(String[] args) {
        //获取AST

    }





    public static List<MethodDeclaration> getMethods(ClassOrInterfaceDeclaration clas){

        List mlist = new ArrayList();
        List childNodes = clas.getChildNodes();
        for (Object childNode : childNodes) {
            if(childNode instanceof ClassOrInterfaceDeclaration){
                mlist.addAll(getMethods((ClassOrInterfaceDeclaration) childNode));
            }
            if(childNode instanceof MethodDeclaration){
                mlist.add((MethodDeclaration)childNode);
            }
        }
        return  mlist;
    }



    //寻找caller的class
    //查找父节点是否有该变量的定义，如果有则返回该变量的类，否则返回变量自身（string）
    public static String findType(Node node, Optional<Expression> scope){

        String[] sc = scope.toString().split("\\.");
        String vname;
        if(sc.length>1 && sc[1].equals("empty")){
             vname = "";
        }
        else {vname = scope.get().toString();}
        Optional<Node> pn = node.getParentNode();
        Node pnode = pn.get();
        List childs = pnode.getChildNodes();
        String clas = find(childs,vname);
        if(clas != null){
            return clas;
        }
        else {
            //如果到达方法层次，则检查方法的参数

            if(pnode instanceof MethodDeclaration){
                List l = Paramprocess((MethodDeclaration) pnode);
                //clas =vname;
                for (Object o : l) {
                    String[] var_type = ((String)o).split(".__.");
                    if(vname.equals(var_type[0])){
                        clas =  var_type[1];
                        return clas;
                    }

                }

            }
            // 如果到达了类层次，则检查类定义的变量
            else if(pnode instanceof ClassOrInterfaceDeclaration){
                if(vname.equals("super")){
                    List l = ((ClassOrInterfaceDeclaration)pnode).getExtendedTypes();
                    String result = "";
                    for (Object o : l) {
                        result = result + ((ClassOrInterfaceType)o).getName().asString() + '/';

                    }
                    clas = result;
                    return clas;
                }
                List l = Fieldprocess((ClassOrInterfaceDeclaration)pnode);
                //clas = vname;
                for (Object o : l) {
                    String[] var_type = ((String)o).split(".__.");
                    if(vname.equals(var_type[0])){
                        clas =  var_type[1];
                        return clas;
                    }
                }


            }
            else if(pnode instanceof CatchClause){
                Parameter param = ((CatchClause)pnode).getParameter();
                if(vname.equals(param.getName().asString())){
                    clas = param.getType().asString();
                    return clas;
                }

            }
            else if(pnode instanceof CompilationUnit){
                return vname;
            }

                clas = findType(pnode,scope);

            return clas;
        }



    }

 //查找该节点下是否有该变量的定义,有则返回变量名，无则返回null
    public static String find(List node, String vname){

        for (Object n : node) {


                if(n instanceof VariableDeclarationExpr){
                    List l = getVarClass((VariableDeclarationExpr) n);
                    for (Object o : l) {
                        String[] var_type = ((String)o).split(".__.");
                        if(vname.equals(var_type[0])){
                            return var_type[1];
                        }
                    }

                }


            else{
                List l = ((Node)n).getChildNodes();
                if(l.size()!=0){String s = find(l,vname);
                    if(s!=null){return s;}
                }

            }
        }

        return  null;

    }

    //提取varDeclaration的类名
    public static List<String> getVarClass(VariableDeclarationExpr varexp){
        NodeList vars = varexp.getVariables();
        List var_type = new ArrayList();
        for (Object var : vars) {
            VariableDeclarator v = (VariableDeclarator) var;
            String vname = v.getName().toString();
            String stype = ((VariableDeclarator) var).getTypeAsString();
            var_type.add(vname+".__."+stype);
        }
        return  var_type;
    }

    public static List<String> Fieldprocess(ClassOrInterfaceDeclaration clas){//处理类定义变量
        List var_type = new ArrayList();
        NodeList mem = clas.getMembers();
        for (Object o : mem) {
            if(o instanceof FieldDeclaration){
                NodeList vars = ((FieldDeclaration)o).getVariables();
                for (Object var : vars) {
                    VariableDeclarator v = (VariableDeclarator) var;
                    String vname = v.getName().toString();
                    String stype = ((VariableDeclarator) var).getTypeAsString();
                    var_type.add(vname+".__."+stype);
                }
            }
        }
        return var_type;
    }

    public static List<String> Paramprocess(MethodDeclaration md){//处理方法的参数
        List var_type = new ArrayList();
        NodeList params = md.getParameters();
        for (Object param : params) {
            Parameter p = (Parameter) param;
            String vname = p.getName().toString();
            String stype = p.getTypeAsString();
            var_type.add(vname+".__."+stype);
        }
        return var_type;
    }

    //获取某个节点所属的类名
    public static String getClassName(Node node){
        String s;
        if(node instanceof ClassOrInterfaceDeclaration){
           s = ((ClassOrInterfaceDeclaration) node).getNameAsString();
        }
        else
        {
            s = getClassName(node.getParentNode().get());
        }
        return s;
    }




}



