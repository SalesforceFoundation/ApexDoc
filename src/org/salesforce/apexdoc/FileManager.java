package org.salesforce.apexdoc;

import java.io.*;
import java.util.*;

public class FileManager {
    FileOutputStream fos; 
    DataOutputStream dos;
    String path;
    public String header;
    public String APEX_DOC_PATH = "";
    public StringBuffer infoMessages ;
    public FileManager(){
        infoMessages = new StringBuffer();
    }
    
    public static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
    
    public FileManager(String path){
                infoMessages = new StringBuffer();
                
                if(path == null || path.trim().length() == 0)
                        this.path = ".";
                else
                        this.path = path;
        }
        
        public boolean createHTML(String fileName, String contents){
                try{
                    if (fileName  == null) return false;
                        
                        
                        if(path.endsWith("/") || path.endsWith("\\")){
                                path += Constants.ROOT_DIRECTORY; // + "/" + fileName + ".html";
                        }else{
                                path += "/"  + Constants.ROOT_DIRECTORY; // + "/" + fileName + ".html";
                        }
                        
                        (new File(path)).mkdirs();
                        
                        fileName = path + "/" + fileName + ".html";
                        
                        File file= new File(fileName);
                        fos = new FileOutputStream(file);
                    dos=new DataOutputStream(fos);
                    dos.writeBytes(contents);
                    dos.close();
                    fos.close();
                     
                    //printAllFiles();
                    copy(path);
                    return true;
                }catch(Exception e){
                        e.printStackTrace();
                }
                
                return false;
        }
        
        public boolean createHTML(Hashtable<String, String> classHashTable,  IProgressMonitor monitor){
                try{
                        if(path.endsWith("/") || path.endsWith("\\")){
                                path += Constants.ROOT_DIRECTORY; // + "/" + fileName + ".html";
                        }else{
                                path += "/"  + Constants.ROOT_DIRECTORY; // + "/" + fileName + ".html";
                        }
                        
                        (new File(path)).mkdirs();
                        
                        for(String fileName : classHashTable.keySet()){                         
                                String contents = classHashTable.get(fileName);                                 
                                fileName = path + "/" + fileName + ".html";                             
                                File file= new File(fileName);
                                fos = new FileOutputStream(file);
                            dos=new DataOutputStream(fos);
                            dos.writeBytes(contents);
                            dos.close();
                            fos.close();
                            infoMessages.append(fileName + " Processed...\n");
                            System.out.println(fileName + " Processed...");
                                if (monitor != null) monitor.worked(1);                     
                        }
                        copy(path);
                    return true;
                }catch(Exception e){
                
                        e.printStackTrace();
                }
                
                return false;
        }
        
        private String strLinkfromModel(ApexModel model, String strClassName, String hostedSourceURL) {
            return "<a target=\"_blank\" class=\"hostedSourceLink\" href=\"" + hostedSourceURL + strClassName + ".cls#L" + model.getInameLine() + "\">";
        }
        
        private String strHTMLScopingPanel() {
            String str = "<tr><td colspan='2' style='text-align: center;' >";
            str += "Filter for: ";
            
            for (int i = 0; i < ApexDoc.rgstrScope.length; i++) {
                str += "<input type='checkbox' checked='checked' id='cbx" + ApexDoc.rgstrScope[i] + 
                        "' onclick='ToggleScope(\"" + ApexDoc.rgstrScope[i] + "\", this.checked );'>" + 
                        ApexDoc.rgstrScope[i] + "</input>&nbsp;&nbsp;";
            }
            str += "</td></tr>";
            return str;
        }
        
        public void makeFile(Hashtable<String, ClassGroup> mapClassNameToClassGroup, ArrayList<ClassModel> cModels, String projectDetail, String homeContents, String hostedSourceURL, IProgressMonitor monitor){
                //System.out.println("Class::::::::::::::::::::::::");
                String links = "<table width='100%'>" ;
                links += strHTMLScopingPanel();
                links += "<tr style='vertical-align:top;' >";
                links += getPageLinks(mapClassNameToClassGroup, cModels);
                
                if(homeContents != null && homeContents.trim().length() > 0 ){
                        homeContents = links + "<td class='contentTD'>" + "<h2 class='section-title'>Home</h2>" + homeContents + "</td>";
                        homeContents = Constants.getHeader(projectDetail) + homeContents + Constants.FOOTER;
                        //createHTML("index.php", homeContents);
                }else{
                        homeContents = Constants.DEFAULT_HOME_CONTENTS;
                        homeContents = links + "<td class='contentTD'>" + "<h2 class='section-title'>Home</h2>" + homeContents + "</td>";
                        homeContents = Constants.getHeader(projectDetail) + homeContents + Constants.FOOTER;
                        //createHTML("index.php", homeContents);
                }
                
                
                String fileName = "";
                Hashtable<String, String> classHashTable = new Hashtable<String, String>();
                classHashTable.put("index", homeContents);
                
                // create our Class Group content files
                createClassGroupContent(classHashTable, links, projectDetail, mapClassNameToClassGroup, cModels, monitor);
                
                for(ClassModel cModel : cModels){
                        String contents = links;
                        if(cModel.getNameLine() != null && cModel.getNameLine().length() > 0){
                                fileName = cModel.getClassName();
                                contents += "<td class='contentTD'>";
                                contents += "<h2 class='section-title'>" + strLinkfromModel(cModel, cModel.getClassName(), hostedSourceURL) +  
                                                                cModel.getClassName() + "</a>" +
                                                                "<span style='float:right;vertical-align:middle;'><input type='button' value='+/- all' onclick='ToggleAll();' /></span>" +
                                                        "</h2>" +
                                                        "<table class='details' rules='all' border='1' cellpadding='6'>" +
                                                                "<tr><th>Author</th><td>" + cModel.getAuthor() + "</td></tr>" +
                                                                "<tr><th>Date</th><td>" + cModel.getDate() + "</td></tr>" +
                                                                "<tr><th>Description</th><td>" + escapeHTML(cModel.getDescription()) + "</td></tr>" +
                                                        "</table>";
                                
                                contents += "<p></p>" +
                                                        "<h2 class='trigger'><input type='button' value='+' style='width:24px' />&nbsp;&nbsp;<a href='#'>Properties</a></h2>" + 
                                                        "<div class='toggle_container'> " +
                                                                "<table class='properties' border='1' rules='all' cellpadding='6'> ";
                                
                                //System.out.println("Properties::::::::::::::::::::::::");
                                for (PropertyModel prop : cModel.getProperties()) {
                                        contents += "<tr class='propertyscope" + prop.getScope() + "'><td class='clsPropertyName'>" + 
                                                strLinkfromModel(prop, cModel.getClassName(), hostedSourceURL) +
                                                prop.getPropertyName() + "</a></td>";
                                        contents += "<td><div class='clsPropertyDeclaration'>" + prop.getNameLine() + "</div>";
                                        contents += "<div class='clsPropertyDescription'>" + escapeHTML(prop.getDescription()) + "</div></tr>";
                                }
                                
                                contents += "</table></div>" 
                                                 + "<h2 class='section-title'>Methods</h2>";

                        
                                //System.out.println("Methods::::::::::::::::::::::::");
                                for (MethodModel method : cModel.getMethods()) {
                                        contents += "<div class=\"methodscope" + method.getScope() + "\" >";
                                        contents += "<h2 class='trigger'><input type='button' value='+' style='width:24px' />&nbsp;&nbsp;<a href='#'>" + method.getMethodName() + "</a></h2>" +
                                                                "<div class='toggle_container'>" +
                                                                "<div class='toggle_container_subtitle'>" + 
                                                                    strLinkfromModel(method, cModel.getClassName(), hostedSourceURL) +
                                                                    method.getNameLine() + "</a></div>" +
                                                                "<table class='details' rules='all' border='1' cellpadding='6'>" + 
                                                                (method.getAuthor() != "" ? "<tr><th>Author</th><td>" + method.getAuthor() + "</td></tr> " : "") +
                                                                (method.getDate() != "" ? "<tr><th>Date</th><td>" + method.getDate() + "</td></tr> " : "") +
                                                                (method.getDescription() != "" ? "<tr><th>Description</th><td>" + escapeHTML(method.getDescription()) + "</td></tr> " : "") +
                                                                (method.getReturns() != "" ? "<tr><th>Returns</th><td>" + method.getReturns() + "</td></tr> " : "") +
                                                                (method.getParams() != null && method.getParams().size() > 0 ? "<tr><th colspan='2' class='paramHeader'>Parameters</td></tr> " : "");
        
                                        
                                        /*System.out.println(method.getNameLine());
                                        System.out.println(method.getAuthor());
                                        System.out.println(method.getDescription());
                                        System.out.println(method.getDate());*/
                                        for (String param : method.getParams()) {
                                                param = escapeHTML(param);
                                                if(param != null && param.trim().length() > 0){
                                                        if(param.indexOf(" ") != -1){
                                                                String list[] = param.split(" ");
                                                                if(list.length >= 1){
                                                                        contents += "<tr><th class='param'>" + list[0] + "</th>";
                                                                        String val = "";
                                                                        if(list.length >= 2){
                                                                                val = "";
                                                                                for(int i = 1; i < list.length; i++){
                                                                                        val += list[i] + " ";
                                                                                }
                                                                        }
                                                                        contents += "<td>" + val + "</td></tr>";
                                                                }
                                                                
                                                        }
                                                }
                                                //System.out.println(param);
                                                
                                        }
                                        contents += "</table></div>";
                                    contents += "</div>"; //methodscope div
                                }
                        }else{
                                continue;
                        }
                        contents += "</div>";
                
                        contents = Constants.getHeader(projectDetail) + contents + Constants.FOOTER;
                        classHashTable.put(fileName, contents);
                        if (monitor != null) monitor.worked(1);
                }
                createHTML(classHashTable, monitor);
        }
        
        // create our Class Group content files
        private void createClassGroupContent(Hashtable<String, String> classHashTable, String links, String projectDetail, Hashtable<String, ClassGroup> mapClassNameToClassGroup, 
            ArrayList<ClassModel> cModels, IProgressMonitor monitor) {
            for (String strGroup : mapClassNameToClassGroup.keySet()) {
                ClassGroup cg = mapClassNameToClassGroup.get(strGroup);
                if (cg.getContentSource() != null) {
                    String cgContent = parseHTMLFile(cg.getContentSource());
                    if (cgContent != null) {
                        String strHtml = Constants.getHeader(projectDetail) + links + "<td class='contentTD'>" + 
                                "<h2 class='section-title'>" + 
                                cg.getName() + "</h2>" + cgContent + "</td>";
                        strHtml += Constants.FOOTER;
                        classHashTable.put(cg.getContentFilename(), strHtml);
                        if (monitor != null) monitor.worked(1);
                    }
                }
            }
        }

        public String getPageLinks(Hashtable<String, ClassGroup> mapClassNameToClassGroup, ArrayList<ClassModel> cModels){
            String links = "<td width='20%' vertical-align='top' >";
            links += "<div class=\"sidebar\"><div class=\"navbar\"><nav role=\"navigation\"><ul id=\"mynavbar\">";
            links += "<li id=\"idMenuindex\"><a href=\".\" onclick=\"gotomenu('index.html');return false;\" class=\"nav-item\">Home</a></li>";
            
            mapClassNameToClassGroup.put("Miscellaneous", new ClassGroup("Miscellaneous", null));
            for (String strGroup : mapClassNameToClassGroup.keySet()) {
                ClassGroup cg = mapClassNameToClassGroup.get(strGroup);
                String strGoTo = "onclick=\"return false;\"";
                if (cg.getContentFilename() != null)
                    strGoTo = "onclick=\"gotomenu('" + cg.getContentFilename() + ".html" + "');return false;\"";
                links += "<li class=\"header\" id=\"idMenu" + cg.getContentFilename() + "\"><a class=\"nav-item nav-section-title\" href=\".\" " + strGoTo +  " class=\"nav-item\">" + strGroup + "<span class=\"caret\"></span></a></li>";
                links += "<ul>";
                
                // even though this algorithm is O(n^2), it was timed at just 12 milliseconds, so not an issue!
                for (ClassModel cModel : cModels) {
                    if (strGroup.equals(cModel.getClassGroup()) || (cModel.getClassGroup() == null && strGroup == "Miscellaneous")) {                    
                        if (cModel.getNameLine() != null && cModel.getNameLine().trim().length() > 0) {
                            String fileName = cModel.getClassName();
                            links += "<li class=\"subitem classscope" + cModel.getScope() + "\" id=\"idMenu" + fileName + 
                                    "\"><a href=\".\" onclick=\"gotomenu('" + fileName + ".html');return false;\" class=\"nav-item sub-nav-item scope" + cModel.getScope() + "\">" + 
                                    fileName + "</a></li>";
                        }
                    }
                }
                
                links += "</ul>";
            }

            links += "</ul></nav></div></div></div>";

            links += "</td>";
            return links;
        }
        
        private void docopy(String source, String target) throws Exception{
                
                
                InputStream is = this.getClass().getResourceAsStream(source);
                //InputStreamReader isr = new InputStreamReader(is);
                //BufferedReader reader = new BufferedReader(isr);
                FileOutputStream to = new FileOutputStream(target + "/" + source);
                
                
           
                byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1){
                to.write(buffer, 0, bytesRead); // write
                }
            
            to.flush();
            to.close();
            is.close();
        }
        
        public  void copy(String toFileName) throws IOException,Exception {
                docopy("apex_doc_logo.png", toFileName);
                docopy("ApexDoc.css", toFileName);
                docopy("ApexDoc.js", toFileName);
                docopy("CollapsibleList.js", toFileName);
                docopy("h2_trigger_a.gif", toFileName);
                docopy("jquery-latest.js", toFileName);
                docopy("toggle_block_btm.gif", toFileName);
                docopy("toggle_block_stretch.gif", toFileName);

        }
        
        public ArrayList<File> getFiles(String path){
                File folder = new File(path);
                ArrayList<File> listOfFilesToCopy = new ArrayList<File>();
                if(folder != null){
                        File[] listOfFiles = folder.listFiles();
                        if(listOfFiles != null && listOfFiles.length > 0){ 
                                for(int i = 0; i < listOfFiles.length; i++){
                                        if (listOfFiles[i].isFile()){
                                                listOfFilesToCopy.add(listOfFiles[i]);
                                        }
                                }
                        }
                  }
                return listOfFilesToCopy;
        }
        
        
        
        public void createDoc(Hashtable<String, ClassGroup> mapClassNameToClassGroup, ArrayList<ClassModel> cModels, String projectDetail, String homeContents, String hostedSourceURL, IProgressMonitor monitor){
                makeFile(mapClassNameToClassGroup, cModels, projectDetail, homeContents, hostedSourceURL, monitor);
        }
        
        public String parseProjectDetail(String filePath){
                try{
                        if(filePath != null && filePath.trim().length() > 0){
                                FileInputStream fstream = new FileInputStream(filePath);
                            // Get the object of DataInputStream
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String contents = "";
                            String strLine;
                            while ((strLine = br.readLine()) != null)   {
                                      // Print the content on the console
                                        strLine = strLine.trim();
                                        if(strLine != null && strLine.trim().length() > 0 ){
                                                String list[] = strLine.split("=");
                                                if(list.length > 1 && list[0] != null && list[1].trim().length() > 0){
                                                        if(list[0].equalsIgnoreCase("projectname")){
                                                                if(list[1] != null && list[1].trim().length() > 0)
                                                                        contents += "<h2 style='margin:0px;'>" + list[1] + "</h2>";
                                                        }else{
                                                                if(list[1] != null && list[1].trim().length() > 0)
                                                                        contents += list[1] + "<br>";
                                                        }
                                                }
                                        }
                                        
                            }
                            br.close();
                            return contents;
                        }
                }catch(Exception e){
                        e.printStackTrace();
                }
            
                return "";
        }
        
        public String parseFile(String filePath){
                try{
                        if(filePath != null && filePath.trim().length() > 0){
                                FileInputStream fstream = new FileInputStream(filePath);
                            // Get the object of DataInputStream
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String contents = "";
                            String strLine;
                            
                            while ((strLine = br.readLine()) != null)   {
                                    //Print the content on the console
                                strLine = strLine.trim();
                                if(strLine != null && strLine.length() > 0){
                                        contents += strLine;
                                }
                            }
                            //System.out.println("Contents = " + contents);
                            br.close();
                            return contents;
                        }
                }catch(Exception e){
                        e.printStackTrace();
                }
            
                return "";
        }
        
        public String parseHTMLFile(String filePath){
                
                String contents = (parseFile(filePath)).trim();
                if(contents != null && contents.length() > 0){
                        int startIndex = contents.indexOf("<body>");
                        int endIndex = contents.indexOf("</body>");
                        if(startIndex != -1){
                                if(contents.indexOf("</body>") != -1){
                                        contents = contents.substring(startIndex,endIndex);
                                        return contents;
                                }
                        }
                }
                return "";
        }
        
}