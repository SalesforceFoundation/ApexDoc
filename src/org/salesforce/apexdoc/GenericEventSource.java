/*
 * http://stackoverflow.com/questions/1204982/event-raise-handling-in-java
 */

package org.salesforce.apexdoc;

import java.util.EventObject;
import java.util.Vector;

public class GenericEventSource<EventArgsType extends EventObject> {
    private Vector<GenericEventListener<EventArgsType>> listenerList =
        new Vector<GenericEventListener<EventArgsType>>();

    public void addListener(GenericEventListener<EventArgsType> listener) {
        listenerList.add(listener);
    }

    public void raise(EventArgsType e) {
        for (GenericEventListener<EventArgsType> listener : listenerList) {
            listener.eventFired(e);
        }
    }
}