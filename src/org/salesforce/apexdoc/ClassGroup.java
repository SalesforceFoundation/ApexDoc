package org.salesforce.apexdoc;

public class ClassGroup {
    private String strName;
    private String strContent;

    public ClassGroup(String strName, String strContent) {
        this.strName = strName;
        this.strContent = strContent;
    }
    
    public String getName() {
        return strName;
    }
    
    public void setName(String strName) {
        this.strName = strName;
    }
    
    public String getContent() {
        return strContent;
    }
    
    public void setContent(String strContent) {
        this.strContent = strContent;
    }
    
}
