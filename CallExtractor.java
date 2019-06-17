package jtags;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CallExtractor {

    public static CompilationUnit parseFileWithRetries (String code) throws ParseProblemException {
        final String classPrefix = "public class Test {";
        final String classSuffix = "}";
        final String methodPrefix = "SomeUnknownReturnType f() {";
        final String methodSuffix = "return noSuchReturnValue; }";

        String originalContent = code;
        String content = originalContent;
        CompilationUnit parsed = null;
        try {
            parsed = JavaParser.parse(content);
        } catch (ParseProblemException e1) {
            // Wrap with a class and method
            try {
                content = classPrefix + methodPrefix + originalContent + methodSuffix + classSuffix;
                parsed = JavaParser.parse(content);
            } catch (ParseProblemException e2) {
                // Wrap with a class only
                content = classPrefix + originalContent + classSuffix;
                parsed = JavaParser.parse(content);
            }
        }

        return parsed;
    }


    public static List loadfile(String path) throws IOException{
        FileReader file = new FileReader(path);
        BufferedReader bf = new BufferedReader(file);
        List codes = new ArrayList();
        List nl = new ArrayList();
        String str;
        List ids = new ArrayList();
        while ((str=bf.readLine())!=null){
            JSONObject object = new JSONObject(str);
            String code = (String)object.get("code");
            codes.add(code);
            String n = (String)object.get("comment");
            nl.add(n);
            int id = (int)object.get("id");
            ids.add(id);
        }
        List result = new ArrayList();
        result.add(codes);
        result.add(nl);
        result.add(ids);
        return result;
    }

    public static void main(String[] args) throws Exception {
        //读取代码片段
 //获得语法树
        String path = "/home/bohong/文档/seqmodel/TL-CodeSum/data/test/test.json";
        List data = loadfile(path);
        List codes = (ArrayList)data.get(0);
        List nls = (ArrayList)data.get(1);
        List callists = new ArrayList();
        List ids = (ArrayList)data.get(2);
        for (Object code : codes) {
            try {
                CompilationUnit cu = parseFileWithRetries((String)code);
                ArrayList calllist = new ArrayList();
                getCall(cu,calllist);
                callists.add(calllist);
            }catch (ParseProblemException e){
                ArrayList calllist = new ArrayList();
                callists.add(calllist);
            }


        }

        String out = "/home/bohong/文档/jtags/src/jtags/calloutput/";
        writefile(out, codes,nls,callists,ids);

        System.out.println();

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

    public static void writefile(String path,List codes, List nls, List calllists, List ids)throws Exception{
        String codepath = path+"code.data";
        String nlspath = path+"comment.data";
        String callpath = path + "call.data";

        int i = 0;
        for (;i<codes.size();i++){
            String code = (String)codes.get(i);
            code = code.replaceAll("\n","");
            String id = ids.get(i).toString();
            code = id + "\t" + code + "\n";
            write(codepath,code);

        }
        i = 0;
        for (;i<nls.size();i++){
            String nl = (String)nls.get(i);
            String id = ids.get(i).toString();
            nl = id + "\t" + nl + "\n";
            write(nlspath,nl);

        }

        i = 0;
        for (;i<calllists.size();i++){
            String call = "";
            for (Object o : (List)calllists.get(i)) {
                call = call + " "+ (String)o;
            }
            call = call + "\n";

            String id = ids.get(i).toString();
            call = id + "\t" + call;
            write(callpath,call);

        }
    }
    public static void getCall(Node node, List calllist){
        if(node == null)return;
        else if(node instanceof MethodCallExpr){
            String callname = ((MethodCallExpr)node).getNameAsString();
            calllist.add(callname);
        }else {
            List nodes = node.getChildNodes();
            for (Object o : nodes) {
                getCall((Node)o,calllist);
            }
        }

    }
}
