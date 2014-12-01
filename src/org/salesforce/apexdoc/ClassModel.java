package org.salesforce.apexdoc;
import java.util.ArrayList;

public class ClassModel extends ApexModel {
        
        public ClassModel(){
                methods = new ArrayList<MethodModel>();
                properties = new ArrayList<PropertyModel>();
        }

        private ArrayList<MethodModel> methods;
        private ArrayList<PropertyModel> properties;
        
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
                        int fFound = nameLine.indexOf("class ");
                        int lFound = nameLine.indexOf(" ", fFound + 6);
                        if(lFound == -1)
                                return nameLine.substring(fFound + 6);
                        try{
                                String name = nameLine.substring(fFound + 6, lFound);
                                return name;
                        }catch(Exception ex){
                                return nameLine.substring(nameLine.lastIndexOf(" ") + 1);
                        }
                }else{
                        return "";
                }
                
        }
        
}
