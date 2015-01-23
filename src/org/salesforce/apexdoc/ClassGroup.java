package org.salesforce.apexdoc;

public class ClassGroup {
    private String strName;
    private String strContentSource;

    public ClassGroup(String strName, String strContent) {
        this.strName = strName;
        this.strContentSource = strContent;
    }

    public String getName() {
        return strName;
    }

    public void setName(String strName) {
        this.strName = strName;
    }

    public String getContentSource() {
        return strContentSource;
    }

    public void setContentSource(String strContent) {
        this.strContentSource = strContent;
    }

    public String getContentFilename() {
        if (strContentSource != null) {
            int idx1 = strContentSource.lastIndexOf("/");
            int idx2 = strContentSource.lastIndexOf(".");
            if (idx1 != -1 && idx2 != -1) {
                return strContentSource.substring(idx1 + 1, idx2);
            }
        }
        return null;
    }
}
