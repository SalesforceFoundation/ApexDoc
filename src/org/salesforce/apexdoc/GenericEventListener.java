/*
 * http://stackoverflow.com/questions/1204982/event-raise-handling-in-java
 */

package org.salesforce.apexdoc;

import java.util.EventListener;
import java.util.EventObject;

public interface GenericEventListener<EventArgsType extends EventObject>

extends EventListener {
	public void eventFired(EventArgsType e);
}