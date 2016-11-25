/*
 * http://stackoverflow.com/questions/1204982/event-raise-handling-in-java
 */

package org.salesforce.apexdoc;

public class ApexDocListener implements GenericEventListener<GenericEventArgs> {

	private Object source = null;
    private String arg;

    public void eventFired(GenericEventArgs e) {
        source = e.getSource();
        arg = e.getArg();
        
        if(arg!=null && source !=null){
        	// Avoid compiler warning
        }
        // continue handling event...
    }
}
