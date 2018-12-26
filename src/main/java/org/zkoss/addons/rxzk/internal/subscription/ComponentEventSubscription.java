/* ComponentEventSubscription.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 06 12:48:13 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk.internal.subscription;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * @author rudyhuang
 */
public class ComponentEventSubscription implements Subscription {
	private final Component _comp;
	private final String _event;
	private final Subscriber<? super Event> _subscriber;
	private EventListener<Event> _listener;

	public ComponentEventSubscription(Component comp, String event,
	                                  Subscriber<? super Event> subscriber) {
		this._comp = comp;
		this._event = event;
		this._subscriber = subscriber;
	}

	public void request(long n) {
		if (_listener == null) {
			try {
				_listener = new EventListener<Event>() {
					public void onEvent(Event t) throws Exception {
						_subscriber.onNext(t);
					}
				};
				_comp.addEventListener(_event, _listener);
			} catch (IllegalArgumentException e) {
				_subscriber.onError(e);
			}
		}
	}

	public void cancel() {
		if (_listener != null) {
			if (Events.inEventListener() || Executions.getCurrent() != null) {
				_comp.removeEventListener(_event, _listener);
			} else {
				Executions.schedule(_comp.getDesktop(), new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						_comp.removeEventListener(_event, _listener);
					}
				}, null);
			}
		}
	}
}
