package org.salesforce.apexdoc;

public class Constants {
        
    public static final String HEADER_OPEN = "<html><head>" + 
        "<script type='text/javascript' src='https://code.jquery.com/jquery-1.11.1.js'></script>" +
        "<script type='text/javascript' src='CollapsibleList.js'></script>" +                                                                
        "<script type='text/javascript' src='ApexDoc.js'></script>" +                                                                
        "<link rel='stylesheet' type='text/css' href='ApexDoc.css' /> " + 
        "</head>" +
        "<body>";
    
    public static final String HEADER_CLOSE =                                                                               
        "</td>" +
        "</tr>" +
        "</table>" +
        "</div>";
    
    public static final String FOOTER = "</div></div></td></tr></table><hr/>" + 
        "<center style='font-size:.8em;'><a href='http://github.com/SalesforceFoundation/ApexDoc' target='_blank'>Powered By ApexDoc</a>" + 
        "</center></body></html>";
    
    public static final String ROOT_DIRECTORY = "ApexDocumentation";
    public static final String DEFAULT_HOME_CONTENTS = "<h1>Project Home</h2>";
    public static final String PROJECT_DETAIL = 
        "<div class='topsection'>" +
        "<table>" +
        "<tr><td>" +
        "<img src='apex_doc_logo.png' style='border:1px solid #000;'/>" +
        "</td>" +
        "<td>" +                
        "<h2 style='margin:0px;'>Project Demo</h2>" +
        "Check out the gitHub project at:<br/>" + 
        "<a href='http://github.com/SalesforceFoundation/ApexDoc'>http://github.com/SalesforceFoundation/ApexDoc</a><br/>";

    public static String getHeader(String projectDetail) {
        String header;
        if (projectDetail != null && projectDetail.trim().length() > 0) {
            header = Constants.HEADER_OPEN + projectDetail;
        } else {
            header = Constants.HEADER_OPEN + Constants.PROJECT_DETAIL + Constants.HEADER_CLOSE;
        }
        return header;
    }
                
}