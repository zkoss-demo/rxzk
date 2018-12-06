/* EventQueueSubscription.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 06 12:47:44 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk.internal.subscription;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * @author rudyhuang
 */
public class EventQueueSubscription<T extends Event> implements Subscription {
	private final String _queueName;
	private final String _scope;
	private final boolean _autoCreate;
	private final Subscriber<? super T> _subscriber;
	private final boolean _async;
	private EventListener<T> _listener;

	public EventQueueSubscription(String queueName, String scope, boolean autoCreate,
	                              boolean async, Subscriber<? super T> subscriber) {
		this._queueName = queueName;
		this._scope = scope;
		this._autoCreate = autoCreate;
		this._async = async;
		this._subscriber = subscriber;
	}

	public void request(long n) {
		if (_listener == null) {
			try {
				_listener = new EventListener<T>() {
					public void onEvent(T t) throws Exception {
						_subscriber.onNext(t);
					}
				};
				getEventQueue().subscribe(_listener, _async);
			} catch (Exception e) {
				_subscriber.onError(e);
			}
		}
	}

	public void cancel() {
		if (_listener != null) {
			getEventQueue().unsubscribe(_listener);
		}
	}

	protected EventQueue<T> getEventQueue() {
		return EventQueues.lookup(_queueName, _scope, _autoCreate);
	}
}
