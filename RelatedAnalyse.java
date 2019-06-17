package jtags;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.json.*;
import java.io.*;

import java.util.*;
import java.util.regex.Pattern;

public class RelatedAnalyse {

    HashMap<String,String> circleCheckpool;
    List relatedCalls;
    String methodName;
    String comment;
    int deepControl;
    String code;
//    List apiLists;
//    List callLists;
    RelatedAnalyse(){
        circleCheckpool = new HashMap<>();
        relatedCalls = new ArrayList();
        methodName = null;
        comment = null;
        deepControl = 0;
        code = null;

//        apiLists = new ArrayList();
//        callLists = new ArrayList();
    }
    public void setMethodName(String name){
        methodName = name;
    }
    public void setComment(String comment){
        this.comment = comment;
    }

    public static List relatedAnalyse(String filepath, FuncTags ftags, VarTags vtags){
        CompilationUnit cu = CallTags.getAst(filepath);
        if(cu == null){
            return null;
        }
        List methods = findMethods(cu);
        List ralist = new ArrayList();
        //FuncTags ftags = new FuncTags();
        //ftags.getFuncs(projectpath);



        for (Object method : methods) {
            RelatedAnalyse ra = new RelatedAnalyse();
            List rlist = ra.extractCallpool((MethodDeclaration)method,filepath,vtags,ftags);
            ra.relatedCalls = (rlist);
            String name = ((MethodDeclaration)method).getNameAsString();
            Optional ob = ((MethodDeclaration)method).getComment();
            ra.code = ((MethodDeclaration)method).toString();
            if(ob.toString().equals("Optional.empty")){}
            else{
                String commet = ob.get().toString();
                ra.setComment(commet);
            }
            ra.setMethodName(name);
            //System.out.println(name);
            ralist.add(ra);
        }
        return ralist;

    }







    public  List extractCallpool(MethodDeclaration node, String filepath,VarTags vtags,FuncTags ftags){
        List calleelist = new ArrayList();
        deepControl += 1;
        CallTags ct = new CallTags(ftags,vtags);
        ct.buildCallPool(node);
        String name = node.getName().asString();
        String range = node.getRange().toString();

        if(circleCheckpool.get(name) != null){
            deepControl = 0;
           return calleelist;
        }

        circleCheckpool.put(name,range);

        Iterator iter = ct.callPool.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            CallProperty val = (CallProperty) entry.getValue();

            if(val.getType().equals("MethodCall")){


                String callname = (String)entry.getKey();
                String locate = ((CallProperty)entry.getValue()).getLocate();
                Callee callee = new Callee(callname,locate,filepath);


                Iterator iter2 = val.funcProperties.iterator();
                while(iter2.hasNext()){
                    FuncProperty fp = (FuncProperty) iter2.next();
                    String filepath2 =fp.getFilepath();
                    Node resNode = fp.funcnode;
                    callee.setNode(fp.funcnode);
                    List res = this.extractCallpool((MethodDeclaration) resNode,filepath2,vtags,ftags);
                    if(res.size()!=0){
                        calleelist.add(res);
                    }

                }
                calleelist.add(callee);

                deepControl = 0;

            }

        }

        return calleelist;
    }



    public  static List<MethodDeclaration>  findMethods(Node node){
        if(node == null)return null;
        List<MethodDeclaration> methods = new ArrayList<>();
        if(node instanceof MethodDeclaration){
            methods.add((MethodDeclaration) node);
        }
        else{
            List childs = node.getChildNodes();
            for (Object child : childs) {
                List ms = findMethods((Node) child);
                if(ms != null){methods.addAll(ms);}
            }
        }

        return methods;
    }




    public static void catalogAnalyse(String folderpath, String outputpath)throws Exception{
        File pro = new File(folderpath);
        if (pro.exists()) {
            File[] files = pro.listFiles();
            if (null == files || files.length == 0) {
                return;
            } else
                {
                    int javanum = 0;
                    for (File file : files){
                        if(file.getName().endsWith(".java")){
                            javanum += 1;
                        }
                    }

                FuncTags ftags = new FuncTags();
                ftags.getFuncs(folderpath);


                for (File file2 : files) {

                    if(file2.isDirectory()){
                        catalogAnalyse(file2.getAbsolutePath(),outputpath);

                    }else if(file2.getName().endsWith(".java")){
                        String filepath =file2.getAbsolutePath();
                        VarTags vtags = new VarTags();
                        vtags.getVars(filepath);
                        if(vtags.varPool.size()==0)continue;

                        List ralist = relatedAnalyse(filepath,ftags,vtags);
                        if(ralist == null)continue;
                        for (Object ra : ralist) {
                            if(((RelatedAnalyse)ra).comment!=null){
                                fileWrite((RelatedAnalyse) ra,outputpath, filepath);
                            }

                        }


                    }

                }
            }
        }


    }

//    public static String readLastLine(File file, String charset) throws IOException {
//        if (!file.exists() || file.isDirectory() || !file.canRead()) {
//            return null;
//        }
//        RandomAccessFile raf = null;
//        try {
//            raf = new RandomAccessFile(file, "r");
//            long len = raf.length();
//            if (len == 0L) {
//                return "";
//            } else {
//                long pos = len - 1;
//                while (pos > 0) {
//                    pos--;
//                    raf.seek(pos);
//                    if (raf.readByte() == '\n') {
//                        break;
//                    }
//                }
//                if (pos == 0) {
//                    raf.seek(0);
//                }
//                byte[] bytes = new byte[(int) (len - pos)];
//                raf.read(bytes);
//                if (charset == null) {
//                    return new String(bytes);
//                } else {
//                    return new String(bytes, charset);
//                }
//            }
//        } catch (FileNotFoundException e) {
//        } finally {
//            if (raf != null) {
//                try {
//                    raf.close();
//                } catch (Exception e2) {
//                }
//            }
//        }
//        return null;
//    }




    public static void dealRelatedcode(List ralist,List result){
        for (Object relatedCall : ralist) {

            if(relatedCall instanceof Callee){
                if(((Callee)relatedCall).callnode != null){
                    result.add(((Callee)relatedCall).callnode.toString().replaceAll("\n",""));
                }
            }else{
                dealRelatedcode((ArrayList)relatedCall,result);
            }

        }
    }


    public  static  int getTotalLines(File file) throws IOException {
        long startTime = System.currentTimeMillis();
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        reader.skip(Long.MAX_VALUE);
        int lines = reader.getLineNumber();
        reader.close();
        long endTime = System.currentTimeMillis();
        return lines;
    }

    public static void fileWrite (RelatedAnalyse ra, String outputpath,String fpath)throws Exception{
        String code = ra.code;
        String comment = ra.comment;
        comment = comment.replaceAll("\n","");
        code = code.replaceAll("\n","");

        List related_codes = new ArrayList();
        dealRelatedcode(ra.relatedCalls,related_codes);
        String file = outputpath + "tuple.json";
        int count = 0;
        File cfile = new File(outputpath + "code.data");
        if(cfile.exists()){
            count = getTotalLines(cfile);
        }


        if(related_codes.size()!=0){
            JSONObject object = new JSONObject();
            object.put("id", count);
            object.put("code", code);
            object.put("comment", comment);
            object.put("related_codes",related_codes);

            write(file, object.toString() + "\n");


        }
        String relatedCall = dealCalllist(ra.relatedCalls, ra.methodName);
        String seqfile = outputpath + "seq.data";
        String codefile = outputpath + "code.data";
        String commentfile = outputpath + "comment.data";


        code = count + "\t" + code + "\n";
        comment = count + "\t" + comment + "\n";
        relatedCall = count + "\t" + relatedCall + "\n";


        write(codefile, code);
        write(commentfile,comment);
        write(seqfile,relatedCall);
        count += 1;
        if(count%1000==0){
            System.out.println(count);
            System.out.println(fpath);
        }




    }

    public static void write(String path, String content)throws Exception{
        File file = new File(path);
        if(!(file.exists())){
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file,true);
        out.write(content.getBytes());
        out.close();

    }

    public static String dealCalllist(List calls, String methodName){
        String relatedCall = methodName + ":";
        String tempcall = "";
        String temp = "";
        ListIterator it = calls.listIterator(calls.size());
        while (it.hasPrevious()){
            Object call = it.previous();
            if(call instanceof Callee){
                String addcall = ((Callee) call).getName();
                relatedCall = relatedCall + addcall + ",";
                temp = addcall;
            }
            else{
                tempcall = tempcall + " " + dealCalllist((List)call, temp) + " ";
            }
        }

        relatedCall = relatedCall + " " + tempcall;
        return relatedCall;
    }



    public static void main(String[] args)throws Exception {

        if(args.length!=2)
            System.out.println("The parameter number must be 2");

        String filepath = args[0];

        String outputpath = args[1];


        catalogAnalyse(filepath,outputpath);
        System.out.println("finished");

    }
}
