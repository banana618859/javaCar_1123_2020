package com.example.demo.entity;

public class cat {
    private String functionkindOne;
    private String functionkindSon;
    private String catGroupNum;
    private String catNum;
    private String errorLevel;
    private String errorMsg;
    private String desc;
    private String result;

    public String getFunctionkindOne() {
        return functionkindOne;
    }

    public void setFunctionkindOne(String functionkindOne) {
        this.functionkindOne = functionkindOne;
    }

    public String getFunctionkindSon() {
        return functionkindSon;
    }

    public void setFunctionkindSon(String functionkindSon) {
        this.functionkindSon = functionkindSon;
    }

    public String getCatGroupNum() {
        return catGroupNum;
    }

    public void setCatGroupNum(String catGroupNum) {
        this.catGroupNum = catGroupNum;
    }

    public String getCatNum() {
        return catNum;
    }

    public void setCatNum(String catNum) {
        this.catNum = catNum;
    }

    public String getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(String errorLevel) {
        this.errorLevel = errorLevel;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "cat{" +
                "functionkindOne='" + functionkindOne + '\'' +
                ", functionkindSon='" + functionkindSon + '\'' +
                ", catGroupNum='" + catGroupNum + '\'' +
                ", catNum='" + catNum + '\'' +
                ", errorLevel='" + errorLevel + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", desc='" + desc + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
