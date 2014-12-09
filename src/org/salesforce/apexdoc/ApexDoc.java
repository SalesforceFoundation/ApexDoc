package org.salesforce.apexdoc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;

public class ApexDoc {

        public static FileManager fm;
        public static String[] rgstrScope;
        public static String[] rgstrArgs;
        
        public ApexDoc(){
                try{
                        File file = new File("apex_doc_log.txt");
                        FileOutputStream fos = new FileOutputStream(file);
                        PrintStream ps = new PrintStream(fos);
                        System.setOut(ps);
                }catch(Exception ex){}
        }
        

        // public entry point when called from the command line.
        public static void main(String[] args) {
                try {
                        RunApexDoc(args, null);
                } catch(Exception ex) {
                        ex.printStackTrace();
                        System.out.println(ex.getMessage());
                        printHelp();
                        System.exit(-1);
                }
        }
        
        
        // public entry point when called from the Eclipse PlugIn.
        // assumes PlugIn previously sets rgstrArgs before calling run.
        public void run(IProgressMonitor monitor) throws InvocationTargetException,     InterruptedException {
                RunApexDoc(rgstrArgs, monitor);
        }       

        // public main routine which is used by both command line invocation and Eclipse PlugIn invocation
        public static void RunApexDoc(String[] args, IProgressMonitor monitor) {
                String sourceDirectory = "";
                String targetDirectory = "";
                String homefilepath = "";
                String authorfilepath = "";
                String hostedSourceURL = "";

                // parse command line parameters
                for (int i = 0; i < args.length; i++) {
                        
                        if (args[i] == null) {
                                continue;
                        } else if (args[i].equalsIgnoreCase("-s")) {
                                sourceDirectory = args[++i];
                        } else if (args[i].equalsIgnoreCase("-g")) {
                            hostedSourceURL = args[++i];
                        } else if (args[i].equalsIgnoreCase("-t")) {
                                targetDirectory = args[++i];
                        } else if (args[i].equalsIgnoreCase("-h")) {
                                homefilepath = args[++i];
                        } else if (args[i].equalsIgnoreCase("-a")) {
                                authorfilepath = args[++i];
                        } else if (args[i].equalsIgnoreCase("-p")) {
                                String strScope = args[++i];
                                rgstrScope = strScope.split(";");
                        } else {
                                printHelp();
                                System.exit(-1);                                
                        }       
                }
                
                // default scope to global and public if not specified
                if (rgstrScope == null || rgstrScope.length == 0) {
                        rgstrScope = new String[3];
                        rgstrScope[0] = "global";
                        rgstrScope[1] = "public";
                        rgstrScope[3] = "webService";
                }
                                
                // find all the files to parse
                fm = new FileManager(targetDirectory);
                ArrayList<File> files = fm.getFiles(sourceDirectory);
                ArrayList<ClassModel> cModels = new ArrayList<ClassModel>();

                if (monitor != null) {
                        // each file is parsed, html created, written to disk.
                        // but for each class file, there is an xml file we'll ignore.
                        // plus we add 2 for the author file and home file loading.
                        monitor.beginTask("ApexDoc - documenting your Apex Class files.", (files.size()/2) * 3 + 2);
                }
                // parse each file, creating a class model for it
                for (File fromFile : files) {
                        String fromFileName = fromFile.getAbsolutePath();
                        if (fromFileName.endsWith(".cls")) {
                                ClassModel cModel = parseFileContents(fromFileName);
                                if (cModel != null) {
                                        cModels.add(cModel);
                                }
                        }                               
                        if (monitor != null) monitor.worked(1);
                }
                
                // create our Groups
                Hashtable<String, ClassGroup> mapClassNameToClassGroup = createMapClassNameToClassGroup(cModels, sourceDirectory); 
                                
                // load up optional specified file templates
                String projectDetail = fm.parseHTMLFile(authorfilepath);
                if (monitor != null) monitor.worked(1);
                String homeContents = fm.parseHTMLFile(homefilepath);
                if (monitor != null) monitor.worked(1);
                
                // create our set of HTML files
                fm.createDoc(mapClassNameToClassGroup, cModels, projectDetail, homeContents, hostedSourceURL, monitor);
                if (monitor != null) monitor.done();
                
                // we are done!
            System.out.println("ApexDoc has completed!");               
        }
        
        private static void printHelp(){
                System.out.println("ApexDoc - a tool for generating documentation from Salesforce Apex code class files.\n");
                System.out.println("    Invalid Arguments detected.  The correct syntax is:\n");
                System.out.println("apexdoc -s <source_directory> [-t <target_directory>] [-g <source_url>] [-h <homefile>] [-a <authorfile>] [-p <scope>]\n");
                System.out.println("<source_directory> - The folder location which contains your apex .cls classes");
                System.out.println("<target_directory> - Optional. Specifies your target folder where documentation will be generated.");
                System.out.println("<source_url> - Optional. Specifies a URL where the source is hosted (so ApexDoc can provide links to your source).");                
                System.out.println("<homefile> - Optional. Specifies the html file that contains the contents for the home page\'s content area.");;
                System.out.println("<authorfile> - Optional. Specifies the text file that contains project information for the documentation header.");
                System.out.println("<scope> - Optional. Semicolon seperated list of scopes to document.  Defaults to 'global;public'. ");               
        }
        
        private static Hashtable<String, ClassGroup> createMapClassNameToClassGroup(ArrayList<ClassModel> cModels, String sourceDirectory) {
            Hashtable<String, ClassGroup> map = new Hashtable<String, ClassGroup>();
            for (ClassModel cmodel : cModels) {
                String strGroup = cmodel.getClassGroup();
                String strGroupContent = cmodel.getClassGroupContent();
                if (strGroupContent != null)
                    strGroupContent = sourceDirectory + "/" + strGroupContent;
                ClassGroup cg;
                if (strGroup != null) {
                    cg = map.get(strGroup);
                    if (cg == null)
                        cg = new ClassGroup(strGroup, strGroupContent);
                    else if (cg.getContentSource() == null)
                        cg.setContentSource(strGroupContent);
                    // put the new or potentially modified ClassGroup back in the map
                    map.put(strGroup, cg);
                }
            }
            return map;
        }
        
        public static ClassModel parseFileContents(String filePath){
                try{
                    FileInputStream fstream = new FileInputStream(filePath);
                    // Get the object of DataInputStream
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    //Read File Line By Line
                    boolean classparsed = false;
                    boolean commentsStarted = false;
                    ArrayList<String> lstComments = new ArrayList<String>();
                    ClassModel cModel = new ClassModel();
                    ArrayList<MethodModel> methods = new ArrayList<MethodModel>();
                    ArrayList<PropertyModel> properties = new ArrayList<PropertyModel>();
                    
                    // DH: Consider using java.io.StreamTokenizer to read the file a token at a time?
                    //
                    // new strategy notes:
                    //          any line with " class " is a class definition
                    //          any line with scope (global, public, private) is a class, method, or property definition.
                    //          you can detect a method vs. a property by the presence of ( )'s
                    //          you can also detect properties by get; or set;, though they may not be on the first line.
                    //          in apex, methods that start with get and take no params, or set with 1 param, are actually properties.
                    //

                    int iLine = 0;
                    while ((strLine = br.readLine()) != null)   {
                        iLine++;
                    
                        strLine = strLine.trim();
                        if (strLine.length() == 0) 
                                continue;
                        
                        // ignore anything after // style comments.  this allows hiding of tokens from ApexDoc.
                        int ich = strLine.indexOf("//");
                        if (ich > -1) {
                                strLine = strLine.substring(0, ich);
                        }
                        
                        // gather up our comments
                        if (strLine.startsWith("/**")) {
                                commentsStarted = true;
                                if (strLine.endsWith("*/"))
                                    commentsStarted = false;
                                lstComments.clear();
                                continue;
                        }
                        
                        if (commentsStarted && strLine.endsWith("*/")) {
                                commentsStarted = false;
                                continue;
                        }
                        
                        if (commentsStarted) {
                                lstComments.add(strLine);
                                continue;
                        }
                        
                        // ignore anything after an =.  this avoids confusing properties with methods.
                        ich = strLine.indexOf("=");
                        if (ich > -1) {
                                strLine = strLine.substring(0, ich);
                        }
                        
                        // ignore anything after an {.  this avoids confusing properties with methods.
                        ich = strLine.indexOf("{");
                        if (ich > -1) {
                                strLine = strLine.substring(0, ich);
                        }

                        // ignore lines not dealing with scope
                        if (!strContainsScope(strLine))
                                continue;

                        // look for a class
                        if (!classparsed && strLine.contains(" class ")) {
                                classparsed = true;
                                fillClassModel(cModel, strLine, lstComments, iLine);
                                lstComments.clear();
                                continue;
                        }
                        
                        // look for a method
                        if (strLine.contains("(")) {
                                // deal with a method over multiple lines.
                                while (!strLine.contains(")")) {
                                    strLine += br.readLine();
                                    iLine++;
                                }
                                MethodModel mModel = new MethodModel();
                                fillMethodModel(mModel, strLine, lstComments, iLine);
                                methods.add(mModel);
                                lstComments.clear();
                                continue;                               
                        }
                        
                        // TODO: need to handle nested class.  ignore it for now!
                        if (strLine.contains(" class "))
                                continue;
                        
                        // handle set & get within the property
                        if (strLine.contains(" get ") || 
                           strLine.contains(" set ") ||
                           strLine.contains(" get;") ||
                           strLine.contains(" set;") ||
                           strLine.contains(" get{") ||
                           strLine.contains(" set{"))
                            continue;
                        
                        // must be a property
                        PropertyModel propertyModel = new PropertyModel();
                        fillPropertyModel(propertyModel, strLine, lstComments, iLine);
                        properties.add(propertyModel);
                        lstComments.clear();
                        continue;
                    }
                    cModel.setMethods(methods);
                    cModel.setProperties(properties);
                    
                    //debug(cModel);
                    //Close the input stream
                    in.close();
                    return cModel;
            }catch (Exception e){//Catch exception if any
              System.err.println("Error: " + e.getMessage());
            }
            
            return null;
        }

        private static boolean strContainsScope(String str) {
            str = str.toLowerCase();
            for (int i = 0; i < rgstrScope.length; i++) {
                if (str.contains(rgstrScope[i].toLowerCase() + " ")) {
                    return true;
                }
            }
            return false;
        }
        
        private static void fillPropertyModel(PropertyModel propertyModel, String name, ArrayList<String> lstComments, int iLine) {
                propertyModel.setNameLine(name, iLine);
                boolean inDescription = false;
                for (String comment : lstComments) {
                        comment = comment.trim();
                        int idxStart = comment.toLowerCase().indexOf("* @description");
                        if (idxStart != -1) {
                                propertyModel.setDescription(comment.substring(idxStart + 15).trim());
                                inDescription = true;
                                continue;
                        }
                        
                        // handle multiple lines for description.
                        if (inDescription) {
                                int i;
                                for (i = 0; i < comment.length(); i++) {
                                        char ch = comment.charAt(i);
                                        if (ch != '*' && ch != ' ')
                                                break;                          
                                }
                                if (i < comment.length()) {
                                        propertyModel.setDescription(propertyModel.getDescription() + ' ' + comment.substring(i));
                                }
                                continue;
                        }
                }
        }
        
        private static void fillMethodModel(MethodModel mModel, String name, ArrayList<String> lstComments, int iLine){
                mModel.setNameLine(name, iLine);
                boolean inDescription = false;
                for (String comment : lstComments) {
                        comment = comment.trim();
                        int idxStart = comment.toLowerCase().indexOf("* @description");
                        if(idxStart != -1){
                                if (comment.length() > idxStart + 15)
                                    mModel.setDescription(comment.substring(idxStart + 15).trim());
                                inDescription = true;
                                continue;
                        }
                        
                        idxStart = comment.toLowerCase().indexOf("* @author");
                        if(idxStart != -1){
                                mModel.setAuthor(comment.substring(idxStart + 10).trim());
                                inDescription = false;
                                continue;
                        }
                        
                        idxStart = comment.toLowerCase().indexOf("* @date");
                        if(idxStart != -1){
                                mModel.setDate(comment.substring(idxStart + 7).trim());
                                inDescription = false;
                                continue;
                        }
                        
                        idxStart = comment.toLowerCase().indexOf("* @return");
                        if(idxStart != -1){
                                mModel.setReturns(comment.substring(idxStart + 10).trim());
                                inDescription = false;
                                continue;
                        }
                        
                        idxStart = comment.toLowerCase().indexOf("* @param");
                        if(idxStart != -1){
                                mModel.getParams().add(comment.substring(idxStart + 8).trim());
                                inDescription = false;
                                continue;
                        }

                        // handle multiple lines for description.
                        if (inDescription) {
                                int i;
                                for (i = 0; i < comment.length(); i++) {
                                        char ch = comment.charAt(i);
                                        if (ch != '*' && ch != ' ')
                                                break;                          
                                }
                                if (i < comment.length()) {
                                        mModel.setDescription(mModel.getDescription() + ' ' + comment.substring(i));
                                }
                                continue;
                        }
                        //System.out.println("#### ::" + comment);
                }
        }
        private static void fillClassModel(ClassModel cModel, String name, ArrayList<String> lstComments, int iLine){
                //System.out.println("@@@@ " + name);
                cModel.setNameLine(name, iLine);
                boolean inDescription = false;
                for (String comment : lstComments) {
                        comment = comment.trim();
                        int idxStart = comment.toLowerCase().indexOf("* @description");
                        if(idxStart != -1){
                                cModel.setDescription(comment.substring(idxStart + 14).trim());
                                inDescription = true;
                                continue;
                        }
                        
                        idxStart = comment.toLowerCase().indexOf("* @author");
                        if(idxStart != -1){
                                cModel.setAuthor(comment.substring(idxStart + 9).trim());
                                inDescription = false;
                                continue;
                        }
                        
                        idxStart = comment.toLowerCase().indexOf("* @date");
                        if(idxStart != -1){
                                cModel.setDate(comment.substring(idxStart + 7).trim());
                                inDescription = false;
                                continue;
                        }
                        
                        idxStart = comment.toLowerCase().indexOf("* @group "); // needed to include space to not match group-content.
                        if(idxStart != -1){
                                cModel.setClassGroup(comment.substring(idxStart + 8).trim());
                                inDescription = false;
                                continue;
                        }
                        
                        idxStart = comment.toLowerCase().indexOf("* @group-content");
                        if(idxStart != -1){
                                cModel.setClassGroupContent(comment.substring(idxStart + 16).trim());
                                inDescription = false;
                                continue;
                        }
                        
                        // handle multiple lines for description.
                        if (inDescription) {
                                int i;
                                for (i = 0; i < comment.length(); i++) {
                                        char ch = comment.charAt(i);
                                        if (ch != '*' && ch != ' ')
                                                break;                          
                                }
                                if (i < comment.length()) {
                                        cModel.setDescription(cModel.getDescription() + ' ' + comment.substring(i));
                                }
                                continue;
                        }
                        //System.out.println("#### ::" + comment);
                }
        }

        
        /*************************************************************************
         * strPrevWord
         * @param str - string to search
         * @param iSearch - where to start searching backwards from
         * @return - the previous word, or null if none found.
         */
        public static String strPrevWord (String str, int iSearch) {
                if (str == null) 
                        return null;
                if (iSearch >= str.length())
                        return null;
                
                int iStart;
                int iEnd;
                for (iStart = iSearch-1, iEnd = 0; iStart >= 0; iStart--) {
                        if (iEnd == 0) {
                                if (str.charAt(iStart) == ' ')
                                        continue;
                                iEnd = iStart+1;
                        } else if (str.charAt(iStart) == ' ') {
                                iStart++;
                                break;
                        }                       
                }
                
                if (iStart == -1) 
                        return null;
                else
                        return str.substring(iStart, iEnd);
        }
        
        
        private static void debug(ClassModel cModel){
                try{
                        System.out.println("Class::::::::::::::::::::::::");
                        if(cModel.getClassName() != null) System.out.println(cModel.getClassName());                    
                        if(cModel.getNameLine() != null) System.out.println(cModel.getNameLine());
                        System.out.println(cModel.getAuthor());
                        System.out.println(cModel.getDescription());
                        System.out.println(cModel.getDate());
                        
                        System.out.println("Properties::::::::::::::::::::::::");
                        for (PropertyModel property : cModel.getProperties()) {
                                System.out.println(property.getNameLine());
                                System.out.println(property.getDescription());
                        }
                        
                        System.out.println("Methods::::::::::::::::::::::::");
                        for (MethodModel method : cModel.getMethods()) {
                                System.out.println(method.getMethodName());
                                System.out.println(method.getAuthor());
                                System.out.println(method.getDescription());
                                System.out.println(method.getDate());
                                for (String param : method.getParams()) {
                                        System.out.println(param);
                                }
                                
                        }
                        
                }catch (Exception e){
                        e.printStackTrace();
                }
        }

}