package org.salesforce.apexdoc;

public class Constants {
        
        public static final String HEADER_OPEN = "<html><head><script type='text/javascript' src='jquery-latest.js'></script>" +
                                                                                "<link rel='stylesheet' type='text/css' href='ApexDoc.css' /> " + 
                                                                                "<script>" + 
                                                                                        "$(document).ready(function(){" +
                                                                                        
                                                                                        "$('.toggle_container').hide();\n" + 
                                                                                        
                                                                                        "$('h2.trigger').click(function(){\n" +
                                                                                                "$(this).toggleClass('active').next().slideToggle('fast');" +
                                                                                                "ToggleBtnLabel(this.firstChild);" +
                                                                                                "return false; " +
                                                                                        "});" +
                                                                                        "});\n" +  

                                                                                        "function gotomenu(url){" +
                                                                                                "document.location.href = url;" +
                                                                                        "}\n" +
                                                                                        
                                                                                        "function ToggleBtnLabel(ctrl){" +
                                                                                                "ctrl.value = (ctrl.value=='+' ? '-' : '+');" +
                                                                                        "}\n" +
                                                                                        
                                                                                        "function IsExpanded(ctrl) {" +
                                                                                                "return (ctrl.value == '-');" +
                                                                                        "}\n" +
                                                                                                
                                                                                        "function ToggleAll() {" +
                                                                                                "var cExpanded = 0;" +
                                                                                                "$('h2.trigger').each(function(){" +
                                                                                                        "if (!IsExpanded(this.firstChild)) {" +
                                                                                                                "$(this).toggleClass('active').next().slideToggle('fast');" +
                                                                                                                "ToggleBtnLabel(this.firstChild);" +
                                                                                                                "cExpanded++;" +
                                                                                                        "}" +
                                                                                                "});" +
                                                                                                "if (cExpanded == 0) {" +
                                                                                                        "$('h2.trigger').each(function(){" +
                                                                                                                "$(this).toggleClass('active').next().slideToggle('fast');" +
                                                                                                                "ToggleBtnLabel(this.firstChild);" +
                                                                                                        "});" +
                                                                                                "}" +
                                                                                        "}\n" +  
                                                                                        
                                                                                "</script>" +
                                                                                "</head>" +
                                                                                "<body>" +
                                                                                "<div class='topsection'>" +
                                                                                "<table>" +
                                                                                "<tr><td>" +
                                                                                "<img src='apex_doc_logo.png' style='border:1px solid #000;'/>" +
                                                                                "</td>" +
                                                                                "<td>"; 
        public static final String HEADER_CLOSE =                                                                               
                                                                                "</td>" +
                                                                                "</tr>" +
                                                                                "</table>" +
                                                                                "</div>";
        
        public static final String FOOTER = "</div></div></td></tr></table><hr/><center style='font-size:10px;'><a href='http://code.google.com/p/apexdoc/'>Powered By ApexDoc </a>" + 
                                                                                "</center></body></html>";
        public static final String ROOT_DIRECTORY = "ApexDocumentation";
        public static final String DEFAULT_HOME_CONTENTS = "<h1>Project Home</h2>";
        public static final String PROJECT_DETAIL = "<h2 style='margin:0px;'>Project Demo</h2>" +
                                                                                                "Check out the Google code project at:<br/>" + 
                                                                                                "<a href='http://code.google.com/p/apexdoc/'>http://code.google.com/p/apexdoc/</a><br/>";
        public static String getHeader(String projectDetail){
                String header;
                if(projectDetail != null && projectDetail.trim().length() > 0){
                        header = Constants.HEADER_OPEN + projectDetail +  Constants.HEADER_CLOSE;
                }else{
                        header = Constants.HEADER_OPEN + Constants.PROJECT_DETAIL +  Constants.HEADER_CLOSE;
                }
                return header;
        }
        
        
}