package main;

public class ClassGroup {
    private String name;
    private String contentSource;

    public ClassGroup(String name, String strContent) {
        this.name = name;
        this.contentSource = strContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentSource() {
        return contentSource;
    }

    public void setContentSource(String strContent) {
        this.contentSource = strContent;
    }

    public String getContentFilename() {
        if (contentSource != null) {
            int idx1 = contentSource.lastIndexOf("/");
            int idx2 = contentSource.lastIndexOf(".");
            if (idx1 != -1 && idx2 != -1) {
                return contentSource.substring(idx1 + 1, idx2);
            }
        }
        return null;
    }
}
