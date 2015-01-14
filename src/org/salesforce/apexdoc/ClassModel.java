package org.salesforce.apexdoc;
import java.util.ArrayList;
import java.util.TreeMap;

public class ClassModel extends ApexModel {
        
        public ClassModel(ClassModel cmodelParent){
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
            TreeMap<String, MethodModel> tm = new TreeMap<String, MethodModel>();
            for (MethodModel method : methods)
                tm.put(method.getMethodName().toLowerCase(), method);
            return new ArrayList<MethodModel>(tm.values());
        }

        public void setMethods(ArrayList<MethodModel> methods) {
                this.methods = methods;
        }
        
        public ArrayList<ClassModel> getChildClasses() {
            return childClasses;
        }
    
        public void addChildClass(ClassModel child) {
            childClasses.add(child);
        }
    
        public String getClassName() {
                String nameLine = getNameLine();
                String strParent = cmodelParent == null ? "" : cmodelParent.getClassName() + ".";
                if (nameLine != null) nameLine = nameLine.trim();
                if (nameLine != null && nameLine.trim().length() > 0 ) {
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
                        } catch(Exception ex) {
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
}
