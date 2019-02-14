package main;

public class ApexModel {

    private String deprecated;
    private String nameLine;
    private int inameLine;
    private String description;
    private String author;
    private String date;
    private String returns;
    private String scope;
    private String example;
    private String see;

    public String getNameLine() {
        return nameLine;
    }

    public int getInameLine() {
        return inameLine;
    }

    public void setNameLine(String nameLine, int iLine) {
        this.nameLine = nameLine.trim();
        this.inameLine = iLine;
        parseScope();
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

    public void setDeprecated(String dep) {
        this.deprecated = dep;
    }

    public String getDeprecated() {
        return deprecated == null ? "" : deprecated;
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

    public String getExample() {
        return example == null ? "" : example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getSee() {
        return see == null ? "" : see;
    }

    public void setSee(String see) {
        this.see = see;
    }

    public String getScope() {
        return scope == null ? "" : scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    private void parseScope() {
        scope = null;
        if (nameLine != null) {
            String str = ApexDoc.strContainsScope(nameLine);
            if (str != null)
                scope = str;
        }
    }
}
