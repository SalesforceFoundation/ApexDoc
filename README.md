ApexDoc
=======

ApexDoc is a java app that you can use to document your Salesforce Apex classes.  You tell ApexDoc where your class files are, and it will generate a set of static HTML pages that fully document each class, including its properties and methods.  Each static HTML page will include an expandable menu on its left hand side, that shows a 2-level tree structure of all of your classes.  Command line parameters allow you to control many aspects of ApexDoc, such as providing your own banner HTML for the pages to use.

Visit this [active project's documentation](https://billkrat.github.io/CPT/ApexDocumentation/) for a sample of what ApexDoc (GwnV1.1) produces.

## Credits
ApexDoc 


- **Originally created** by Aslam Bari ([http://techsahre.blogspot.com/2011/01/apexdoc-salesforce-code-documentation.html](http://techsahre.blogspot.com/2011/01/apexdoc-salesforce-code-documentation.html)).  
- **2011**: Extended by David Habib, at Groundwire.  
- **2014**: Enhanced by David Habib of the Salesforce Foundation for use with Nonprofit Success Pack ([https://github.com/SalesforceFoundation/Cumulus](https://github.com/SalesforceFoundation/Cumulus)). They noted that they are unable to offer direct support of reported issues or incorporate enhancement requests at this time, however pull requests are welcome.
- **2016**: Updated by Bill kratochvil, Global Webnet, LLC for use with the [Welkin Suite](https://welkinsuite.com/).
 

## Command Line Parameters
| parameter | description |
|-------------------------- | ---------------------|
| -s *source_directory* | The folder location which contains your apex .cls classes.|
| -t *target_directory* | The folder location where documentation will be generated to.|
| -g *source_url* | A URL where the source is hosted (so ApexDoc can provide links to your source). Optional.|
| -h *home_page* | The full path to an html file that contains the contents for the home page's content area. Optional.|
| -a *banner_page* | The full path to an html file that contains the content for the banner section of each generated page. Optional.|
| -p *scope* | A semicolon separated list of scopes to document.  Defaults to 'global;public;webService'. Optional.|

## Usage
Copy apexdoc.jar file to your local machine, somewhere on your path.  Each release tag in gitHub has the matching apexdoc.jar attached to it.  Make sure that java is on your path.  Invoke ApexDoc like this example:
```
java -jar apexdoc.jar
    -s '/Users/dhabib/Workspaces/Force.com IDE/Cumulus3/src/classes'
    -t '/Users/dhabib/Dropbox/Cumulus/ApexDoc'
    -p 'global;public;private;testmethod;webService'
    -h '/Users/dhabib/Dropbox/Cumulus/ApexDoc/homepage.htm'
    -a '/Users/dhabib/Dropbox/Cumulus/ApexDoc/projectheader.htm'
    -g 'http://github.com/SalesforceFoundation/Cumulus/blob/dev/src/classes/'
```

ApexDoc (GwnV1.1) contains a feature for default usage, if the apexdoc.jar application is dropped into a solution folder which contains a src/classes folder then it will automatically use it as the source (-s switch) and the default folder as the destination (-d switch).   Likewise if a folder ApexDocContent exists then it will be used for the homepage (-h switch) and header (-a switch).  

![](http://www.global-webnet.com/Adventures/image.axd?picture=SolutionFolder.png)

The ApexDocContent folder will be searched for the following files:

1. .header.html - this will be used to satisfy the -a switch
2. .home.html - this will be used to satify the -h switch.

All files, with the exception of html files, will be copied over to the ApexDocumentation folder.  Existing files with the same name will be replaced.  This provides the ability to easily update css, js, and png files for use by your documentation.  The html files are not replaced because their content has already been extracted (from between the body tags) to generate new html files of the same name in the ApexDocumentation folder.

![](http://www.global-webnet.com/Adventures/image.axd?picture=ApexDocContent.png)

If you are using the Welkin Suite for Salesforce development you will want to review the blog [Using ApexDoc with Welkin Suite](http://www.global-webnet.com/Adventures/post/2016/11/25/Using-ApexDoc-with-Welkin-Suite) as it provides details on how to create documentation seamlessly with ApexDoc (GwnV1.1)

## Documenting Class Files
ApexDoc scans each class file, and looks for comment blocks with special keywords to identify the documentation to include for a given class, property, or method.  The comment blocks must always begin with /** (or additional *'s) and can cover multiple lines.  Each line must start with * (or whitespace and then *).  The comment block ends with */.  Special tokens are called out with @token.
### Class Comments
Located in the lines above the class declaration.  The special tokens are all optional.

| token | description |
|-------|-------------|
| @author | the author of the class |
| @date | the date the class was first implemented |
| @group | a group to display this class under, in the menu hierarchy|
| @group-content | a relative path to a static html file that provides content about the group|
| @description | one or more lines that provide an overview of the class|

Example
```
/**
* @author Salesforce.com Foundation
* @date 2014
*
* @group Accounts
* @group-content ../../ApexDocContent/Accounts.htm
*
* @description Trigger Handler on Accounts that handles ensuring the correct system flags are set on
* our special accounts (Household, One-to-One), and also detects changes on Household Account that requires
* name updating.
*/
public with sharing class ACCT_Accounts_TDTM extends TDTM_Runnable {
```

### Property Comments
Located in the lines above a property.  The special tokens are all optional.

| token | description |
|-------|-------------|
| @description | one or more lines that describe the property|

Example
```
    /*******************************************************************************************************
    * @description specifies whether state and country picklists are enabled in this org.
    * returns true if enabled.
    */
    public static Boolean isStateCountryPicklistsEnabled {
        get {
```

### Method Comments
In order for ApexDoc to identify class methods, the method line must contain an explicit scope (global, public, private, testMethod, webService).  The comment block is located in the lines above a method.  The special tokens are all optional.

| token | description |
|-------|-------------|
| @description | one or more lines that provide an overview of the method|
| @param *param name* | a description of what the parameter does|
| @return | a description of the return value from the method|
| @example | Example code usage. This will be wrapped in <code> tags to preserve whitespace|
Example
```
    /*******************************************************************************************************
    * @description Returns field describe data
    * @param objectName the name of the object to look up
    * @param fieldName the name of the field to look up
    * @return the describe field result for the given field
    * @example
    * Account a = new Account();
    */
    public static Schema.DescribeFieldResult getFieldDescribe(String objectName, String fieldName) {
```
