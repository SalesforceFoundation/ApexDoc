/*
 * http://stackoverflow.com/questions/1204982/event-raise-handling-in-java
 */

package org.salesforce.apexdoc;

import java.util.EventObject;

public class GenericEventArgs extends EventObject {
	public static final long serialVersionUID = 10;
	
    private String arg;
    
    public GenericEventArgs(Object source, String arg) {
        super(source);
        this.arg = arg;
    }
    public String getArg() {
        return arg;
    }
}
