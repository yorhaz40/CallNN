package jtags;

public class VarProperty {
    private String range;
    private String ctype;
    VarProperty(String range,String ctype ){
        this.range = range;
        this.ctype = ctype;
    }
    public void setRange(String range){
        this.range = range;
    }
    public void setCtype(String ctype){
        this.ctype = ctype;
    }
    public String getRange(){return range;}
    public String getCtype(){return ctype;}
}


