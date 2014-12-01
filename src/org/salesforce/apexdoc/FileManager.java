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
        
        
        public void makeFile(ArrayList<ClassModel> cModels, String projectDetail, String homeContents, IProgressMonitor monitor){
                //System.out.println("Class::::::::::::::::::::::::");
                String links = "<table width='100%'><tr style='vertical-align:top;'>" ;
                links += getPageLinks(cModels);
                
                if(homeContents != null && homeContents.trim().length() > 0 ){
                        homeContents = links + "<td width='80%'>" + "<h2 class='section-title'>Home</h2>" + homeContents + "</td>";
                        homeContents = Constants.getHeader(projectDetail) + homeContents + Constants.FOOTER;
                        //createHTML("index.php", homeContents);
                }else{
                        homeContents = Constants.DEFAULT_HOME_CONTENTS;
                        homeContents = links + "<td width='80%'>" + "<h2 class='section-title'>Home</h2>" + homeContents + "</td>";
                        homeContents = Constants.getHeader(projectDetail) + homeContents + Constants.FOOTER;
                        //createHTML("index.php", homeContents);
                }
                
                
                String fileName = "";
                Hashtable<String, String> classHashTable = new Hashtable<String, String>();
                classHashTable.put("index", homeContents);
                for(ClassModel cModel : cModels){
                        String contents = links;
                        if(cModel.getNameLine() != null && cModel.getNameLine().length() > 0){
                                fileName = cModel.getClassName();
                                contents += "<td width='80%'>";
                                contents += "<h2 class='section-title'>" + cModel.getClassName() + 
                                                                "<span style='float:right;margin-top:-5px;'><input type='button' value='+/- all' onclick='ToggleAll();' /></span>" +
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
                                        contents += "<tr><td class='clsPropertyName'>" + prop.getPropertyName() + "</td>";
                                        contents += "<td><div class='clsPropertyDeclaration'>" + prop.getNameLine() + "</div>";
                                        contents += "<div class='clsPropertyDescription'>" + escapeHTML(prop.getDescription()) + "</div></tr>";
                                }
                                
                                contents += "</table></div>" 
                                                 + "<h2 class='section-title'>Methods</h2>";

                        
                                //System.out.println("Methods::::::::::::::::::::::::");
                                for (MethodModel method : cModel.getMethods()) {
                                        contents += "<h2 class='trigger'><input type='button' value='+' style='width:24px' />&nbsp;&nbsp;<a href='#'>" + method.getMethodName() + "</a></h2>" +
                                                                "<div class='toggle_container'>" +
                                                                "<div class='toggle_container_subtitle'>" + method.getNameLine() + "</div>" +
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
        
        public String getPageLinks(ArrayList<ClassModel> cModels){
                String links = "<td width='20%' class='leftmenus'>";
                links += "<div onclick=\"gotomenu('index.html');\">Home</div>";
                for(ClassModel cModel : cModels){
                        if(cModel.getNameLine() != null && cModel.getNameLine().trim().length() > 0){
                        String fileName = cModel.getClassName();
                        //System.out.println("### File Name: " + fileName);
                        //System.out.println("### File Path: " + cModel.getNameLine());
                        links += "<div onclick=\"gotomenu('" + fileName+ ".html');\">"+fileName+"</div>";
                        }
                }
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
        
        
        
        public void createDoc(ArrayList<ClassModel> cModels, String projectDetail, String homeContents, IProgressMonitor monitor){
                makeFile(cModels, projectDetail, homeContents, monitor);
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