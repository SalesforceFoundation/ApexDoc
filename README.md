ApexDoc2
=======

ApexDoc2 is a java app that you can use to document your Salesforce Apex classes.  You tell ApexDoc2 where your class files are, and it will generate a set of static HTML pages that fully document each class, including its properties and methods.  Each static HTML page will include an expandable menu on its left hand side that shows the class groups that you've defined, and the classes within those groups.  Command line parameters allow you to control many aspects of ApexDoc2, such as providing your own banner and Project Home HTML for the pages to use.

## Credits
As the name implies, ApexDoc2 is the second rendition of this project. Before finding its new home here, the [original ApexDoc](https://github.com/SalesforceFoundation/ApexDoc) was created by Aslam Bari (http://techsahre.blogspot.com/2011/01/apexdoc-salesforce-code-documentation.html).  It was then taken and extended by David Habib, at Groundwire, in 2011. It was subsequently enhanced by David Habib of the Salesforce Foundation in late 2014 for use with Nonprofit Starter Pack (https://github.com/SalesforceFoundation/Cumulus). Please see the [CHANGELOG](https://github.com/no-stack-dub-sack/ApexDoc2/blob/master/CHANGELOG.md) for a list of enhancements since ApexDoc and additional credits.

## ApexDoc2 Reasoning
As the Salesforce Foundation was [no longer able to offer direct support for reported issues or incorporate enhancement requests](https://github.com/SalesforceFoundation/ApexDoc#credits), I am attempting to revitalize the project as ApexDoc2. I believe there is still a need for this project, and pull requests and issues to the original ApexDoc continue to be submitted (however sparsely) which have, or report, some much needed bug fixes. Plus, it might even be fun! If anyone happens to notice this, pull requests, issues, and help are all welcome.

## Command Line Parameters
| parameter | description |
|-------------------------- | ---------------------|
| -s *source_directory* | The folder location which contains your apex .cls classes.|
| -t *target_directory* | The folder location where documentation will be generated to.|
| -g *source_url* | A URL where the source is hosted (so ApexDoc can provide links to your source). Optional.|
| -h *home_page* | The full path to an html file that contains the contents for the home page's content area. Optional.|
| -a *banner_page* | The full path to an html file that contains the content for the banner section of each generated page. Optional.|
| -p *scope* | A semicolon separated list of scopes to document.  Defaults to 'global;public;webService'. Optional.|
| -d *document_title* | The value for the document's &lt;title&gt; attribute.  Defaults to 'ApexDocs'. Optional.|
| -n *toc_description* | If 'true', will hide the method's description snippet in the class's table of contents. Defaults to 'false'. Optional.|
| -0 *sort_order* | The order in which class methods, properties, and inner classes are presented. Either 'logical', the order they appear in the source file, or 'alpha', alphabetically. Defaults to 'alpha'. Optional.|

## Usage
Copy apexdoc.jar file to your local machine, somewhere on your path.  Each release tag in gitHub has the matching apexdoc.jar attached to it.  Make sure that java is on your path.  Invoke ApexDoc like this example:
```
java -jar apexdoc.jar
    -s C:\Users\pweinberg\Workspaces\Force.com IDE\Cumulus3\src\classes
    -t C:\Users\pweinberg\Dropbox\Cumulus\ApexDoc
    -p global;public;private;testmethod;webService
    -h C:\Users\pweinberg\Dropbox\Cumulus\ApexDoc\homepage.htm
    -a C:\Users\pweinberg\Dropbox\Cumulus\ApexDoc\projectheader.htm
    -g http://github.com/SalesforceFoundation/Cumulus/blob/dev/src/classes/
    -d "My Docs Title"
    -o logical
    -n false
```

A favicon has been added with ApexDoc2, so if you'd like to use your own favicon, simply replace the favicon png in the output directory with your own favicon. It must be a PNG and named favicon.png.

## Documenting Class Files
ApexDoc2 scans each class file, and looks for comment blocks with special keywords to identify the documentation to include for a given class, property, or method.  The comment blocks must always begin with /** (or additional *'s) and can cover multiple lines.  Each line must start with * (or whitespace and then *).  The comment block ends with */.  Special tokens are called out with @token.

Within your ApexDoc2 comments, to indicate code inline, wrap it in backticks; e.g. \`String cool = 'cool!';\`. This will be formatted as code in the output file. See examples below.

### Class Comments
Located in the lines above the class declaration.  The special tokens are all optional.

| token | description |
|-------|-------------|
| @author | The author of the class |
| @date | The date the class was first implemented |
| @group | A group to display this class under, in the menu hierarchy|
| @group-content | A relative path to a static html file that provides content about the group|
| @description | One or more lines that provide an overview of the class|
| @deprecated | Indicates class should no longer be used; message should indicate replacement |
| @see | A fully qualified class or method name; creates a link to that class or method in the documentation. The name must be a fully qualified name, even if its a reference to another method in the same class, e.g. Class.Method, Class.InnerClass, Class.InnerClass.InnerClassMethod|

Example
```
/**
* @author Salesforce.com Foundation
* @date 2014
*
* @group Accounts
* @group-content ../../ApexDocContent/Accounts.htm
* @deprecated Replaced by AccountTriggerHandler
* @see AccountTriggerHandler
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
    /**
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
| @description | One or more lines that provide an overview of the method|
| @param *param name* | A description of what the parameter does|
| @return | A description of the return value from the method|
| @deprecated | Indicates method should no longer be used; message should indicate replacement |
| @exception | A list of exceptions a method throws and/or description of Exception(s) that might be thrown |
| @example | Example code usage. This will be wrapped in <code> tags to preserve whitespace|
| @see | A fully qualified class or method name; creates a link to that class or method in the documentation. The name must be a fully qualified name, even if its a reference to another method in the same class, e.g. Class.Method, Class.InnerClass, Class.InnerClass.InnerClassMethod|

Example
```
    /**
    * @description Returns field describe data
    * @param objectName the name of the object to look up
    * @param fieldName the name of the field to look up
    * @return the describe field result for the given field
    * @exception System.QueryException
    * @see Utils.getSObjectDescribe
    *
    * @example
    * // this is how getFieldDescribe works (the whitespace below will be preserved for complex examples)
    *
    * Schema.DescribeFieldResult result = Utils.getFieldDescribe('Account', 'Name');
    * System.debug(result);
    */
    public static Schema.DescribeFieldResult getFieldDescribe(String objectName, String fieldName) {
```
