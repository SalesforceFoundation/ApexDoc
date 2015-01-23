package org.salesforce.apexdoc;

import java.util.ArrayList;

public class MethodModel extends ApexModel {

    public MethodModel() {
        params = new ArrayList<String>();
    }

    public void setNameLine(String nameLine, int iLine) {
        // remove anything after the parameter list
        if (nameLine != null) {
            int i = nameLine.lastIndexOf(")");
            if (i >= 0)
                nameLine = nameLine.substring(0, i + 1);
        }
        super.setNameLine(nameLine, iLine);
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getMethodName() {
        String nameLine = getNameLine().trim();
        if (nameLine != null && nameLine.length() > 0) {
            int lastindex = nameLine.indexOf("(");
            if (lastindex >= 0) {
                String methodName = ApexDoc.strPrevWord(nameLine, lastindex);
                return methodName;
            }
        }
        return "";
    }

    private ArrayList<String> params;
    private String returnType;
}
