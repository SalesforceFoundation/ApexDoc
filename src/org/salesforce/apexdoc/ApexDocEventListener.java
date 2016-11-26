/*
 * http://stackoverflow.com/questions/1204982/event-raise-handling-in-java
 */

package org.salesforce.apexdoc;

public class ApexDocEventListener implements GenericEventListener<GenericEventArgs> {
    private Object source = null;
    private String arg;
    public void eventFired(GenericEventArgs e) {
        source = e.getSource();
        arg = e.getArg();
        if(source!=null){
        	// Avoid compiler warning
        }
        
        // If there are no files in source directory than the warning
        // was shown, display help, and exit application
        if(arg==FileManager.NO_FILES){
        	ApexDoc.printHelp();
        	ApexDoc.IsExitOnWarning = true;
        }
    }
}