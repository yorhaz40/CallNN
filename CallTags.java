package jtags;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;


import java.io.File;
import java.util.*;

public class CallTags {
    public HashMap<String, CallProperty> callPool;
    public FuncTags ftags;
    public VarTags vtags;
    //public List apiList;
    //public List callist;
    CallTags(FuncTags ftags, VarTags vtags){
        callPool = new HashMap<>();
       this.ftags = ftags;
        this.vtags = vtags;
//        apiList = new ArrayList();
//        callist = new ArrayList();
    }



    public void buildCallPool(Node node){
        if(node == null) return ;

        else if(node instanceof MethodCallExpr){
            Optional<Expression> fExp = ((MethodCallExpr)node).getScope();
            String fieldname = ((MethodCallExpr)node).getNameAsString();
            List<FuncProperty> fps = ftags.funcPool.get(fieldname);
            List<FuncProperty> fps2 = new ArrayList<>();

//            CompilationUnit cu = ApiExtractor.jumptoCu(node);
//            List imports = cu.getImports();



            if(fps != null){
                for (FuncProperty fp : fps) {
                    String filepath2 =fp.getFilepath();
                    Node resNode = fp.funcnode;

//                    }
                    if(sameFuncProcess((MethodCallExpr)node,(MethodDeclaration) resNode)){
                        fps2.add(fp);
                    }
                }
            }


            if((fExp.toString()).equals("Optional.empty")){
                String type = "MethodCall";
                String locate = node.getRange().toString();


                CallProperty cp = new CallProperty(locate,type);
                callPool.put(fieldname,cp);
                if(fps2.size()!=0){

                    cp.setFuncProperties(fps2);
                }

              
            }
            else{
                Expression formerExp = fExp.get();
                if(formerExp instanceof NameExpr){
                    String clasname = ((NameExpr)formerExp).getNameAsString();
                    String type = "MethodCall";
                    String locate = formerExp.getRange().toString();
                    List<VarProperty> vps = vtags.varPool.get(clasname);
                    CallProperty cp = new CallProperty(locate,type);
                    if(fps2.size()!=0){

                        cp.setFuncProperties(fps2);
                    }
                    if(vps!=null){
                        cp.setVarProperties(vps);
                    }
                    //callPool.put(clasname+"."+fieldname,cp);
                    callPool.put(fieldname,cp);
                }
                else{
                    String type = "MethodCall";
                    String locate = formerExp.getRange().toString();
                    CallProperty cp = new CallProperty(locate,type);
                   // callPool.put(node.toString(),cp);
                    callPool.put(fieldname,cp);
                    buildCallPool(formerExp);
                }
            }


        }
        else{
            List nodes = node.getChildNodes();
            for (Object o : nodes) {
                buildCallPool((Node)o);
            }
        }

    }


    public static Node locateNode(Node node,String range){//给定根节点和代码块的位置，定位出代码块在AST中的位置

        String nodeRange = node.getRange().toString();
        if(nodeRange.equals(range)){
            return node;
        }
        else{
            List childs = node.getChildNodes();
            for (Object child : childs) {
                Node resNode = locateNode((Node)child,range);
                if(resNode != null){
                    return resNode;
                }
            }
            return null;
        }




    }

    public static CompilationUnit getAst(String filepath){
        File file = new File(filepath);

        try{
            String source = FileIO.readStringFromFile(file.getAbsolutePath());
            CompilationUnit compilationUnit = JavaParser.parse(source);
            return compilationUnit;
        }
        catch (Exception e){

            System.out.println(e);
            System.out.println(filepath);
            return null;
        }

    }



    public  List<String> getCallerParam(MethodCallExpr caller){

        List params = caller.getArguments();
        List paramtypes = new ArrayList();
        for (Object param : params) {
            if(param instanceof CastExpr){
                paramtypes.add(((CastExpr)param).getType().asString());
            }
            else if(param instanceof NameExpr){
                String pname = ((NameExpr) param).getName().asString();
                //暂时默认取该变量名的第一个元素，之后再实现变量的确认方法。
                if(vtags.varPool.size() == 0)return paramtypes;
                if(vtags.varPool.get(pname) == null) return paramtypes;
                VarProperty vp = vtags.varPool.get(pname).get(0);
                String ptype = vp.getCtype();
                paramtypes.add(ptype);

            }
        }
        return paramtypes;
    }

    public  List<String> getCalleeParam(MethodDeclaration callee){

        List params = callee.getParameters();
        List paramtypes = new ArrayList();
        for (Object param : params) {
            String ptype = ((Parameter)param).getType().asString();
            paramtypes.add(ptype);
        }
        return paramtypes;
    }


    public boolean sameFuncProcess(MethodCallExpr caller, MethodDeclaration callee){

        List callerparams = getCallerParam(caller);
        List calleeparams = getCalleeParam(callee);
        boolean flag = false;
        if(calleeparams.size() == callerparams.size()){
            flag = true;
            for(int i = 0; i < calleeparams.size(); i++){
                if(!(calleeparams.get(i).equals(callerparams.get(i)))){
                    //如果类型不一致，则flag为false,注意这里的！
                    flag = false;
                }
            }
        }

        return flag;

    }

    public static void main(String[] args) {
//        String filepath = "/home/bohong/文档/myapi_analyser/src/jtags/CallTags.java";
//        String projectpath = "/home/bohong/文档/myapi_analyser/src/jtags";
//        FuncTags ftags = new FuncTags();
//        ftags.getFuncs(projectpath);
//        VarTags varTags = new VarTags();
//        varTags.getVars(filepath);
//        CallTags ct = new CallTags(ftags,varTags);
//
//        CompilationUnit cu = getAst(filepath);
//        ct.buildCallPool(cu);
//        Iterator iter = ct.callPool.entrySet().iterator();
//        while (iter.hasNext()){
//            Map.Entry entry = (Map.Entry) iter.next();
//            Object key = entry.getKey();
//            CallProperty val = (CallProperty) entry.getValue();
//            Iterator iter2 = val.funcProperties.iterator();
//            while (iter2.hasNext()){
//                System.out.println(key);
//                FuncProperty fp = (FuncProperty) iter2.next();
//                String filepath2 =fp.getFilepath();
//                CompilationUnit compilationUnit = getAst(filepath2);
//                Node resNode = locateNode(compilationUnit,fp.getLocaterange());
//                System.out.println(key);
//            }
//            Iterator iter3 = val.varProperties.iterator();
//            while (iter3.hasNext()){
//                System.out.println(key);
//                VarProperty vp = (VarProperty) iter3.next();
//                CompilationUnit compilationUnit = getAst(filepath);
//                Node resNode = locateNode(compilationUnit,vp.getRange());
//                System.out.println(key);
//            }
//
//        }
//        System.out.println();
    }
}
