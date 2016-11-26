package org.salesforce.apexdoc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @description base class for the Class, Method, and Property models
 * @updated BillKrat.2016.11.24  GwnV1.1
 *  - Simplified tag processing (moved code out of ApexDoc into this base class)
 */
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
        parseScope();
    }
    
    public boolean AppendContent(boolean inDescription, boolean inExample, String comment){
    	if (inDescription || inExample) {
			int j;
			for (j = 0; j < comment.length(); j++) {
				char ch = comment.charAt(j);
				if (ch != '*' && ch != ' ')
					break;
			}
			if (j < comment.length()) {
				if (inDescription) {
					setDescription(getDescription() + ' ' + comment.substring(j));
				} 
				else if (inExample) {
					// Lets's not include the tag
					if (j == 0 && comment.contains("@example")) {
						comment = comment.replace("@example","");
					}
					setExample(getExample() + (getExample().trim().length() == 0 ? "" : "\n")
							+ comment.substring(2));
				}
			}
			return true;
		}
    	return false;
    }
    
    /*
     * @author BillKrat
     * @description encapsulated all tag processing in SetField to 
     * simplify tag processing in derived classes
     */
    public boolean SetField(String fieldName, String value, int line){
    	String parsedValue = null;
    	
    	// Is the fieldName within the value
    	int idxStart = value.trim().toLowerCase().indexOf(fieldName);
    	
    	// Was the fieldName found -OR- this is the first line 
    	if(idxStart != -1 || line == 1){
        	int fieldLen = fieldName.length();
        	int offset = idxStart+fieldLen;

        	// If this is the first line then grab the information
        	// after the first space and set it as the description
        	// Note: it will only display if there is NOT a subsequent
        	//       description because it will be overwritten by it
        	//       below.
        	if(line==1){
				Pattern p = Pattern.compile("\\s");
				Matcher m = p.matcher(value);
				if (m.find()) 
					setDescription(value.substring(m.start()).trim());
				return true;
        	}
        	
        	// Ensure our offset is in bounds 
        	if(offset > value.length()){
        		System.out.println(fieldName +" => " + value +" NOT PROCESS (out of bounds)");
        		return false;
        	}
        	
        	// Get the value following our fieldName
        	parsedValue = value.substring(offset).trim();
    		
    		if(fieldName=="@author")		setAuthor(parsedValue);
    		if(fieldName=="@date")			setDate(parsedValue);
    		if(fieldName=="@return")		setReturns(parsedValue);
    		if(fieldName=="@param")	    	params.add(parsedValue);
    		if(fieldName=="@description")	setDescription(parsedValue);
    		if(fieldName=="@example")		setExample(parsedValue);
    		if(fieldName=="@group ")  		strClassGroup = parsedValue;
    		if(fieldName=="@group-content") strClassGroupContent = parsedValue;
    		
    		return true;
    	}
    	return false;
    }
    
    public boolean SetFieldCheck(String fieldName, String value){
    	return false;
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

    public String getExample() {
        return example == null ? "" : example;
    }

    public void setExample(String example) {
        this.example = example;
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

    private String nameLine;
    private int inameLine;
    private String description;
    private String author;
    private String date;
    private String returns;
    private String scope;
    private String example;
    
    /* @updated BillKrat.2016.11.24 GwnV1.1
     * Breaking some design rules so we can enforce others, e.g., 
     * we'll sacrifice separation of concerns by allowing params, 
     * ClassGroup, and ClassGroupContent to reside in a generic 
     * base class so that we can encapsulate all of the business logic
     * that handles these properties in the SetField method above 
     * (preventing us from duplicating it for each model)
     */
    protected ArrayList<String> params;
    protected String strClassGroup;
    protected String strClassGroupContent;


}
