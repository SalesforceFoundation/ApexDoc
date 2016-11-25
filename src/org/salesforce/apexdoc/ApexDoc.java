package org.salesforce.apexdoc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

/*
 * @description Main class
 * @updated BillKrat.2016.11.24 GwnV1.1  
 *  - Support for event handling added
 *  - Simplified tag processing (moved code into ApexModel base class)
 *  - Added support for default ./ApexDocContent folder and header/home files
 *  - Display help information if invalid parameter is sent, i.e., source has no files  
 */
public class ApexDoc {

	// Avoid magic strings for default locations, files, and version
	static final String Version = "GwnV1.1";

	// Default settings if the folder/files exist
	static boolean isApexDocContent = false;
	static final String apexDocContent = "./ApexDocContent";
	static final String classes = "./src/classes";
	static final String header = "./ApexDocContent/.header.html";
	static final String home = "./ApexDocContent/.home.html";

	public static FileManager fm;
	public static String[] rgstrScope;
	public static String[] rgstrArgs;

	public static Boolean IsExitOnWarning = false;

	/*
	 * @description Redirects output to the apex_doc_log.txt file. The
	 * constructor will not fire unless explicitly instantiated
	 * 
	 * @example // Activates output to text file ApexDoc apexDoc = new
	 * ApexDoc();
	 */
	public ApexDoc() {
		try {
			File file = new File("apex_doc_log.txt");
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setOut(ps);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/*
	 * @description public entry point when called from the command line.
	 * 
	 * @param args containing switches
	 */
	public static void main(String[] args) {
		try {
			RunApexDoc(args, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			printHelp();
			System.exit(-1);
		}
	}

	/*
	 * @description public entry point when called from the Eclipse PlugIn.
	 * assumes PlugIn previously sets rgstrArgs before calling run.
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		RunApexDoc(rgstrArgs, monitor);
	}

	/*
	 * @description public main routine which is used by both command line
	 * invocation and Eclipse PlugIn invocation
	 */
	public static void RunApexDoc(String[] args, IProgressMonitor monitor) {
		String targetDirectory = "."; // Default to folder application resides
		String sourceDirectory = "";
		String homefilepath = "";
		String authorfilepath = "";
		String hostedSourceURL = "";

		// Check to see if folder/files are available from relative
		// location of application, i.e., project folder
		fm = new FileManager(targetDirectory);

		// if there is a classes folder available then
		// default to it - can still be overridden by
		// using switches (below)
		if (fm.exists(classes))
			sourceDirectory = classes;

		// if the apexDocContent folder exists then we'll
		// set the following paths (if html files exist)
		if (fm.exists(apexDocContent)) {
			isApexDocContent = true;
			targetDirectory = ".";
			if (fm.exists(home))
				homefilepath = home;
			if (fm.exists(header))
				authorfilepath = header;
		}

		// parse command line parameters, these can override
		// any default settings established above
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
			rgstrScope[2] = "webService";
		}

		// find all the files to parse for the configured
		// target directory
		fm = new FileManager(targetDirectory);

		// Subscribe to the FileManager events. We'll attach the
		// ApexDocEventListener which is tightly coupled to this class
		// so that the FileManager does not have to be (the listener
		// calls the printHelp method and accesses fields)
		ApexDocEventListener listener = new ApexDocEventListener();
		fm.FileManagerEvent.addListener(listener);

		// Get list of files from source directory.
		ArrayList<File> files = fm.getFiles(sourceDirectory);

		// If we experienced a warning from the FileManager via event
		// notification and this flag is set then we're done
		if (ApexDoc.IsExitOnWarning)
			return;

		ArrayList<ClassModel> cModels = new ArrayList<ClassModel>();
		if (monitor != null) {
			// each file is parsed, html created, written to disk.
			// but for each class file, there is an xml file we'll ignore.
			// plus we add 2 for the author file and home file loading.
			monitor.beginTask("ApexDoc - documenting your Apex Class files.", (files.size() / 2) * 3 + 2);
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
			if (monitor != null)
				monitor.worked(1);
		}

		// create our Groups
		TreeMap<String, ClassGroup> mapGroupNameToClassGroup = createMapGroupNameToClassGroup(cModels, sourceDirectory);

		// load up optional specified file templates
		String projectDetail = fm.parseHTMLFile(authorfilepath);
		if (monitor != null)
			monitor.worked(1);

		String homeContents = fm.parseHTMLFile(homefilepath);
		if (monitor != null)
			monitor.worked(1);

		// Create our set of HTML files
		fm.createDoc(mapGroupNameToClassGroup, cModels, projectDetail, homeContents, hostedSourceURL, monitor);

		// If there is an "ApexDocContent" folder then we want to ensure that any
		// images and modified css/js files make it into the final ApexDocumentation
		// folder, particularly since the logo.png file is pulled from embedded resource
		// (this provides a hook to overwrite).
		if (isApexDocContent) {
			// Get a list of files within the ApexDocContent folder
			files = fm.getFiles(apexDocContent);
			
			// Iterate through each file
			for (File file : files) {

				//Construct a destination folder using the root and current filename 
				File destination = new File(Constants.ROOT_DIRECTORY + "/"+ file.getName());
				try {
					// Do not overwrite any html files as they have been processed; we're 
					// interested in in images and modified css/js files that may be in folder
					if(file.getName().toLowerCase().contains("html"))
						continue;
					
					// Copy the file to the destination - if it exists it will be deleted first
					fm.copyFile(file, destination);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}

		if (monitor != null)
			monitor.done();

		// we are done!
		System.out.println("ApexDoc (" + Version + ") has completed!  Documentation at https://github.com/BillKrat/ApexDoc");
	}

	/*
	 * @description display the help screen
	 */
	public static void printHelp() {
		System.out.println("");
		System.out.println("ApexDoc (" + Version + ") "+
				"- a tool for generating documentation from Salesforce Apex code class files.\n");
		System.out.println("    Invalid Arguments detected.  The correct syntax is:\n");
		System.out.println(
				"apexdoc -s <source_directory> [-t <target_directory>] [-g <source_url>] [-h <homefile>] [-a <authorfile>] [-p <scope>]\n");
		System.out.println("<source_directory> - The folder location which contains your apex .cls classes");
		System.out.println(
				"<target_directory> - Optional. Specifies your target folder where documentation will be generated.");
		System.out.println(
				"<source_url> - Optional. Specifies a URL where the source is hosted (so ApexDoc can provide links to your source).");
		System.out.println(
				"<homefile> - Optional. Specifies the html file that contains the contents for the home page\'s content area.");
		System.out.println(
				"<authorfile> - Optional. Specifies the text file that contains project information for the documentation header.");
		System.out.println(
				"<scope> - Optional. Semicolon seperated list of scopes to document.  Defaults to 'global;public'. ");
		System.out.println("");
		System.out.println("Documentation at https://github.com/BillKrat/ApexDoc");
		
	}

	private static TreeMap<String, ClassGroup> createMapGroupNameToClassGroup(ArrayList<ClassModel> cModels,
			String sourceDirectory) {
		TreeMap<String, ClassGroup> map = new TreeMap<String, ClassGroup>();
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
				// put the new or potentially modified ClassGroup back in the
				// map
				map.put(strGroup, cg);
			}
		}
		return map;
	}

	/*
	 * @description Parse comments from provided filename
	 */
	public static ClassModel parseFileContents(String filePath) {
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			boolean commentsStarted = false;
			boolean docBlockStarted = false;
			int nestedCurlyBraceDepth = 0;
			ArrayList<String> lstComments = new ArrayList<String>();
			ClassModel cModel = null;
			ClassModel cModelParent = null;
			Stack<ClassModel> cModels = new Stack<ClassModel>();

			// Read in each line and process it
			int iLine = 0;
			while ((strLine = br.readLine()) != null) {
				iLine++;

				strLine = strLine.trim();
				if (strLine.length() == 0)
					continue;

				// ignore anything after // style comments. this allows hiding
				// of tokens from ApexDoc.
				int ich = strLine.indexOf("//");
				if (ich > -1) {
					strLine = strLine.substring(0, ich);
				}

				// gather up our comments
				if (strLine.startsWith("/*")) {
					commentsStarted = true;
					boolean commentEnded = false;
					if (strLine.startsWith("/**")) {
						if (strLine.endsWith("*/")) {
							strLine = strLine.replace("*/", "");
							commentEnded = true;
						}
						lstComments.add(strLine);
						docBlockStarted = true;
					}
					if (strLine.endsWith("*/") || commentEnded) {
						commentsStarted = false;
						docBlockStarted = false;
					}
					continue;
				}

				if (commentsStarted && strLine.endsWith("*/")) {
					strLine = strLine.replace("*/", "");
					if (docBlockStarted) {
						lstComments.add(strLine);
						docBlockStarted = false;
					}
					commentsStarted = false;
					continue;
				}

				if (commentsStarted) {
					if (docBlockStarted) {
						lstComments.add(strLine);
					}
					continue;
				}

				// keep track of our nesting so we know which class we are in
				int openCurlies = countChars(strLine, '{');
				int closeCurlies = countChars(strLine, '}');
				nestedCurlyBraceDepth += openCurlies;
				nestedCurlyBraceDepth -= closeCurlies;

				// if we are in a nested class, and we just got back to nesting
				// level 1,
				// then we are done with the nested class, and should set its
				// props and methods.
				if (nestedCurlyBraceDepth == 1 && openCurlies != closeCurlies && cModels.size() > 1 && cModel != null) {
					cModels.pop();
					cModel = cModels.peek();
					continue;
				}

				// ignore anything after an =. this avoids confusing properties
				// with methods.
				ich = strLine.indexOf("=");
				if (ich > -1) {
					strLine = strLine.substring(0, ich);
				}

				// ignore anything after an {. this avoids confusing properties
				// with methods.
				ich = strLine.indexOf("{");
				if (ich > -1) {
					strLine = strLine.substring(0, ich);
				}

				// ignore lines not dealing with scope
				if (strContainsScope(strLine) == null &&
				// interface methods don't have scope
						!(cModel != null && cModel.getIsInterface() && strLine.contains("("))) {
					continue;
				}

				// look for a class
				if ((strLine.toLowerCase().contains(" class ") || strLine.toLowerCase().contains(" interface "))) {

					// create the new class
					ClassModel cModelNew = new ClassModel(cModelParent);
					fillClassModel(cModelParent, cModelNew, strLine, lstComments, iLine);
					lstComments.clear();

					// keep track of the new class, as long as it wasn't a
					// single liner {}
					// but handle not having any curlies on the class line!
					if (openCurlies == 0 || openCurlies != closeCurlies) {
						cModels.push(cModelNew);
						cModel = cModelNew;
					}

					// add it to its parent (or track the parent)
					if (cModelParent != null)
						cModelParent.addChildClass(cModelNew);
					else
						cModelParent = cModelNew;
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
					cModel.getMethods().add(mModel);
					lstComments.clear();
					continue;
				}

				// handle set & get within the property
				if (strLine.contains(" get ") || strLine.contains(" set ") || strLine.contains(" get;")
						|| strLine.contains(" set;") || strLine.contains(" get{") || strLine.contains(" set{"))
					continue;

				// must be a property
				PropertyModel propertyModel = new PropertyModel();
				fillPropertyModel(propertyModel, strLine, lstComments, iLine);
				cModel.getProperties().add(propertyModel);
				lstComments.clear();
				continue;

			}

			// Close the input stream
			in.close();
			// we only want to return the parent class
			return cModelParent;
		} catch (Exception e) { // Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		return null;
	}

	/*
	 * @description
	 */
	public static String strContainsScope(String str) {
		str = str.toLowerCase();
		for (int i = 0; i < rgstrScope.length; i++) {
			if (str.toLowerCase().contains(rgstrScope[i].toLowerCase() + " ")) {
				return rgstrScope[i];
			}
		}
		return null;
	}

	/*
	 * @description Property only supports description tag
	 * 
	 * @updated BillKrat.2016.11.24 GwnV1.1 - Moved processing logic for tag
	 * into ApexModel
	 */
	private static void fillPropertyModel(PropertyModel propertyModel, String name, ArrayList<String> lstComments,
			int iLine) {
		propertyModel.setNameLine(name, iLine);
		boolean inDescription = false;
		int i = 0;
		for (String comment : lstComments) {
			i++;
			if (propertyModel.SetField("@description", comment, i)) {
				inDescription = true;
				continue;
			}
			// handle multiple lines for @description (example does not
			// apply-false)
			if (propertyModel.AppendContent(inDescription, false, comment))
				continue;
		}
	}

	/*
	 * @description Method supports author, date, return, description, and param
	 * tags
	 * 
	 * @updated BillKrat.2016.11.24 GwnV1.1 - Moved processing logic for tag
	 * into ApexModel
	 */
	private static void fillMethodModel(MethodModel mModel, String name, ArrayList<String> lstComments, int iLine) {
		mModel.setNameLine(name, iLine);
		boolean inDescription = false;
		boolean inExample = false;
		int i = 0;
		for (String comment : lstComments) {
			i++;
			if (mModel.SetField("@author", comment, i) || mModel.SetField("@date", comment, i)
					|| mModel.SetField("@return", comment, i) || mModel.SetField("@param", comment, i)) {
				inDescription = false;
				inExample = false;
				continue;
			}
			if (mModel.SetField("@description", comment, i)) {
				inDescription = true;
				inExample = false;
				continue;
			}
			if (mModel.SetField("@example", comment, i)) {
				inDescription = false;
				inExample = true;
				continue;
			}

			// handle multiple lines for @description and @example.
			if (mModel.AppendContent(inDescription, inExample, comment))
				continue;

		}
	}

	/*
	 * @description Classes support author, date, group, group-content, and
	 * description tags
	 * 
	 * @updated BillKrat.2016.11.24 GwnV1.1 - Moved processing logic for tag
	 * into ApexModel
	 */
	private static void fillClassModel(ClassModel cModelParent, ClassModel cModel, String name,
			ArrayList<String> lstComments, int iLine) {
		cModel.setNameLine(name, iLine);
		if (name.toLowerCase().contains(" interface "))
			cModel.setIsInterface(true);
		boolean inDescription = false;
		int i = 0;
		for (String comment : lstComments) {
			i++;
			comment = comment.trim();

			if (cModel.SetField("@author", comment, i) || cModel.SetField("@date", comment, i)
					|| cModel.SetField("@group ", comment, i) || cModel.SetField("@group-content", comment, i)) {
				inDescription = false;
				continue;
			}

			if (cModel.SetField("@description", comment, i)) {
				inDescription = true;
				continue;
			}

			// handle multiple lines for @description (no example - false)
			if (cModel.AppendContent(inDescription, false, comment))
				continue;
		}
	}

	/*************************************************************************
	 * strPrevWord
	 *
	 * @param str
	 *            - string to search
	 * @param iSearch
	 *            - where to start searching backwards from
	 * @return - the previous word, or null if none found.
	 */
	public static String strPrevWord(String str, int iSearch) {
		if (str == null)
			return null;
		if (iSearch >= str.length())
			return null;

		int iStart;
		int iEnd;
		for (iStart = iSearch - 1, iEnd = 0; iStart >= 0; iStart--) {
			if (iEnd == 0) {
				if (str.charAt(iStart) == ' ')
					continue;
				iEnd = iStart + 1;
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

	/*************************************************************************
	 * @description Count the number of occurrences of character in the string
	 * @param str
	 * @param ch
	 * @return int
	 */
	private static int countChars(String str, char ch) {
		int count = 0;
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) == ch) {
				++count;
			}
		}
		return count;
	}

	/*
	 * private static void debug(ClassModel cModel){ try{
	 * System.out.println("Class::::::::::::::::::::::::");
	 * if(cModel.getClassName() != null)
	 * System.out.println(cModel.getClassName()); if(cModel.getNameLine() !=
	 * null) System.out.println(cModel.getNameLine());
	 * System.out.println(cModel.getAuthor());
	 * System.out.println(cModel.getDescription());
	 * System.out.println(cModel.getDate());
	 *
	 * System.out.println("Properties::::::::::::::::::::::::"); for
	 * (PropertyModel property : cModel.getProperties()) {
	 * System.out.println(property.getNameLine());
	 * System.out.println(property.getDescription()); }
	 *
	 * System.out.println("Methods::::::::::::::::::::::::"); for (MethodModel
	 * method : cModel.getMethods()) {
	 * System.out.println(method.getMethodName());
	 * System.out.println(method.getAuthor());
	 * System.out.println(method.getDescription());
	 * System.out.println(method.getDate()); for (String param :
	 * method.getParams()) { System.out.println(param); }
	 *
	 * }
	 *
	 * }catch (Exception e){ e.printStackTrace(); } }
	 */

}
