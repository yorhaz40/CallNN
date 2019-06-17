package jtags;

import java.util.ArrayList;
import java.util.List;

public class CallProperty {
    String locate;
    String type;
    List<VarProperty> varProperties;
    List<FuncProperty> funcProperties;

    CallProperty(String locate,String type){
        this.locate = locate;
        this.type = type;
        this.varProperties = new ArrayList<>();
        this.funcProperties = new ArrayList<>();
        this.varProperties.addAll(varProperties);
        this.funcProperties.addAll(funcProperties);
    }

    public String getLocate(){return locate;}
    public String getType(){return type;}
    public void setVarProperties(List<VarProperty> varProperties){
        this.varProperties.addAll(varProperties);
    }
    public void setFuncProperties(List<FuncProperty> funcProperties){
        this.funcProperties.addAll(funcProperties);
    }
}
