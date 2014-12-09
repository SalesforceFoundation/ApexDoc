ApexDoc
=======

ApexDoc is a java app that you can use to document your Salesforce Apex classes.  You tell ApexDoc where your class files are, and it will generate a set of static HTML pages that fully document each class, including its properties and methods.  Each static HTML page will include an expandable menu on its left hand side, that shows a 2-level tree structure of all of your classes.  Command line parameters allow you to control many aspects of ApexDoc, such as providing your own banner HTML for the pages to use.

## Command Line Parameters
| parameter | description |
|-------------------------- | ---------------------|
| -s *source_directory* | The folder location which contains your apex .cls classes.|
| -t *target_directory* | The folder location where documentation will be generated to.|
| -g *source_url* | A URL where the source is hosted (so ApexDoc can provide links to your source). Optional.|
| -h *home_page* | The full path to an html file that contains the contents for the home page's content area. Optional.|
| -a *banner_page* | The full path to an html file that contains the content for the banner section of each generated page. Optional.|
| -p *scope* | A semicolon separated list of scopes to document.  Defaults to 'global;public;webService'. Optional.|

## Documenting Class Files
ApexDoc scans each class file, and looks for comment blocks with special keywords to identify the documentation to include for a given class, property, or method.  The comment blocks must always begin with /** (or additional *'s) and can cover multiple lines.  Each line must start with * (or whitespace and then *).  The comment block ends with */.  Special tokens are called out with @token.
### Class Comments
Typically located near the top of the class file.  The special tokens are all optional.

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
```

### Property Comments
Located in the line above a property.  The special tokens are all optional.

| token | description |
|-------|-------------|
| @description | one or more lines that provide an overview of the class|

Example
```
    /*******************************************************************************************************
    * @description specifies whether state and country picklists are enabled in this org.
    * returns true if enabled.
    */ 
    public static Boolean isStateCountryPicklistsEnabled {
        get {
```
