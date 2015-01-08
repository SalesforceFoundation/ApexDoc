package org.salesforce.apexdoc;
import java.util.ArrayList;

public class ClassModel extends ApexModel {
        
        public ClassModel(){
                methods = new ArrayList<MethodModel>();
                properties = new ArrayList<PropertyModel>();
        }

        private ArrayList<MethodModel> methods;
        private ArrayList<PropertyModel> properties;
        private String strClassGroup;
        private String strClassGroupContent;
        
        public ArrayList<PropertyModel> getProperties() {
                return properties;
        }

        public void setProperties(ArrayList<PropertyModel> properties) {
                this.properties = properties;
        }

        public ArrayList<MethodModel> getMethods() {
                return methods;
        }

        public void setMethods(ArrayList<MethodModel> methods) {
                this.methods = methods;
        }
        
        public String getClassName(){
                String nameLine = getNameLine();
                if (nameLine != null) nameLine = nameLine.trim();
                //System.out.println("@@ File Name = " + nameLine);
                if(nameLine != null && nameLine.trim().length() > 0 ){
                        //System.out.println("## File Name = " + nameLine.trim().lastIndexOf(" "));
                        int fFound = nameLine.toLowerCase().indexOf("class ");
                        int cch = 6;
                        if (fFound == -1) {
                            fFound = nameLine.toLowerCase().indexOf("interface ");
                            cch = 10;
                        }
                        if (fFound > -1)
                            nameLine = nameLine.substring(fFound + cch).trim();
                        int lFound = nameLine.indexOf(" ");
                        if(lFound == -1)
                                return nameLine;
                        try{
                                String name = nameLine.substring(0, lFound);
                                return name;
                        }catch(Exception ex){
                                return nameLine.substring(nameLine.lastIndexOf(" ") + 1);
                        }
                }else{
                        return "";
                }
                
        }
        
        public String getClassGroup() {
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
