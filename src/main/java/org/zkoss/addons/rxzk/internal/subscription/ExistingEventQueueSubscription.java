/* ExistingEventQueueSubscription.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 06 12:48:25 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk.internal.subscription;

import org.reactivestreams.Subscriber;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;

/**
 * @author rudyhuang
 */
public class ExistingEventQueueSubscription<T extends Event> extends EventQueueSubscription<T> {
	private EventQueue<T> _eventQueue;

	public ExistingEventQueueSubscription(EventQueue<T> eventQueue, boolean async, Subscriber<? super T> subscriber) {
		super("", "", false, async, subscriber);
		this._eventQueue = eventQueue;
	}

	@Override
	protected EventQueue<T> getEventQueue() {
		return _eventQueue;
	}
}
