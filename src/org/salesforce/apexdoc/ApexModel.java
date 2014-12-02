package org.salesforce.apexdoc;

public class ApexModel {
    public String getNameLine() {
            return nameLine;
    }
    public int getInameLine() {
        return inameLine;
}
    public void setNameLine(String nameLine, int iLine) {
            this.nameLine = nameLine.trim();
            this.inameLine = iLine;
    }
    public String getDescription() {
            return description == null ? "" : description;
    }
    public void setDescription(String description) {
            this.description = description;
    }
    public String getAuthor() {
            return author == null ? "" : author;
    }
    public void setAuthor(String author) {
            this.author = author;
    }
    public String getDate() {
            return date == null ? "" : date;
    }
    public void setDate(String date) {
            this.date = date;
    }       
    public String getReturns() {
            return returns == null ? "" : returns;
    }
    public void setReturns(String returns) {
            this.returns = returns;
    }
    
    private String nameLine;
    private int inameLine;
    private String description;
    private String author;
    private String date;
    private String returns;
    
}