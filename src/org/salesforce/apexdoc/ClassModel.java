package org.salesforce.apexdoc;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;

public class ClassModel extends ApexModel {

    public ClassModel(ClassModel cmodelParent) {
        methods = new ArrayList<MethodModel>();
        properties = new ArrayList<PropertyModel>();
        this.cmodelParent = cmodelParent;
        childClasses = new ArrayList<ClassModel>();
    }

    private ArrayList<MethodModel> methods;
    private ArrayList<PropertyModel> properties;
    private String strClassGroup;
    private String strClassGroupContent;
    private ClassModel cmodelParent;
    private ArrayList<ClassModel> childClasses;
    private boolean isInterface;

    public ArrayList<PropertyModel> getProperties() {
        return properties;
    }

    public ArrayList<PropertyModel> getPropertiesSorted() {
        TreeMap<String, PropertyModel> tm = new TreeMap<String, PropertyModel>();
        for (PropertyModel prop : properties)
            tm.put(prop.getPropertyName().toLowerCase(), prop);
        return new ArrayList<PropertyModel>(tm.values());
    }

    public void setProperties(ArrayList<PropertyModel> properties) {
        this.properties = properties;
    }

    public ArrayList<MethodModel> getMethods() {
        return methods;
    }

    public ArrayList<MethodModel> getMethodsSorted() {
        @SuppressWarnings("unchecked")
		List<MethodModel> sorted = (List<MethodModel>)methods.clone();
        Collections.sort(sorted, new Comparator<MethodModel>(){
            @Override
            public int compare(MethodModel o1, MethodModel o2) {
                String methodName1 = o1.getMethodName();
                String methodName2 = o2.getMethodName();
                String className = getClassName();
                
                if(methodName1.equals(className)){
                    return Integer.MIN_VALUE;
                } else if(methodName2.equals(className)){
                    return Integer.MAX_VALUE;
                }
                return (methodName1.toLowerCase().compareTo(methodName2.toLowerCase()));
            }
        });
        return new ArrayList<MethodModel>(sorted);
    }

    public void setMethods(ArrayList<MethodModel> methods) {
        this.methods = methods;
    }

    public ArrayList<ClassModel> getChildClassesSorted() {
        TreeMap<String, ClassModel> tm = new TreeMap<String, ClassModel>();
        for (ClassModel cm : childClasses)
            tm.put(cm.getClassName().toLowerCase(), cm);
        return new ArrayList<ClassModel>(tm.values());
    }

    public void addChildClass(ClassModel child) {
        childClasses.add(child);
    }

    public String getClassName() {
        String nameLine = getNameLine();
        String strParent = cmodelParent == null ? "" : cmodelParent.getClassName() + ".";
        if (nameLine != null)
            nameLine = nameLine.trim();
        if (nameLine != null && nameLine.trim().length() > 0) {
            int fFound = nameLine.toLowerCase().indexOf("class ");
            int cch = 6;
            if (fFound == -1) {
                fFound = nameLine.toLowerCase().indexOf("interface ");
                cch = 10;
            }
            if (fFound > -1)
                nameLine = nameLine.substring(fFound + cch).trim();
            int lFound = nameLine.indexOf(" ");
            if (lFound == -1)
                return strParent + nameLine;
            try {
                String name = nameLine.substring(0, lFound);
                return strParent + name;
            } catch (Exception ex) {
                return strParent + nameLine.substring(nameLine.lastIndexOf(" ") + 1);
            }
        } else {
            return "";
        }

    }

    public String getTopmostClassName() {
        if (cmodelParent != null)
            return cmodelParent.getClassName();
        else
            return getClassName();
    }

    public String getClassGroup() {
        if (this.cmodelParent != null)
            return cmodelParent.getClassGroup();
        else
            return strClassGroup;
    }

    public void setClassGroup(String strGroup) {
        strClassGroup = strGroup;
    }

    public String getClassGroupContent() {
        return strClassGroupContent;
    }

    public void setClassGroupContent(String strGroupContent) {
        strClassGroupContent = strGroupContent;
    }

    public boolean getIsInterface() {
        return isInterface;
    }

    public void setIsInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }
}
