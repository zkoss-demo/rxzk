/* ZkObservable.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 30 10:26:34 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk;

import io.reactivex.Observable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;

/**
 * An util class for RxJava integration to create {@link Observable} instances
 * from the event of ZK {@link Component} or {@link EventQueue}.
 *
 * @author rudyhuang
 */
public abstract class ZkObservable {
	/**
	 * Generates an {@link Observable} source from an event of the ZK component.
	 *
	 * @param comp a ZK component.
	 * @param event an event name. Using predefined {@link Events} constants are preferred.
	 * @return An observable source.
	 */
	public static Observable<? extends Event> fromEvent(final Component comp, final String event) {
		return Observable.fromPublisher(ZkReactiveStreams.fromEvent(comp, event));
	}

	/**
	 * Generates an {@link Observable} source from the specified event queue with the specified name in the desktop scope.
	 * Create one if no such event queue.
	 * The listener is synchronous.
	 *
	 * @param queueName the queue name.
	 * @return An observable source.
	 */
	public static Observable<? extends Event> fromEventQueue(String queueName) {
		return fromEventQueue(queueName, EventQueues.DESKTOP, true, false);
	}

	/**
	 * Generates an {@link Observable} source from the specified event queue with the specified name in the specified scope.
	 * Create one if no such event queue.
	 * The listener is synchronous.
	 *
	 * @param queueName the queue name.
	 * @param scope the scope of the event queue. Like {@link EventQueues#DESKTOP} or {@link EventQueues#APPLICATION}.
	 * @return An observable source.
	 */
	public static Observable<? extends Event> fromEventQueue(String queueName, String scope) {
		return fromEventQueue(queueName, scope, true, false);
	}

	/**
	 * Generates an {@link Observable} source from the specified event queue with the specified name in the specified scope.
	 * The listener is synchronous.
	 *
	 * @param queueName the queue name.
	 * @param scope the scope of the event queue. Like {@link EventQueues#DESKTOP} or {@link EventQueues#APPLICATION}.
	 * @param autoCreate whether to create the event queue if not found.
	 * @return An observable source.
	 */
	public static Observable<? extends Event> fromEventQueue(String queueName, String scope, boolean autoCreate) {
		return fromEventQueue(queueName, scope, autoCreate, false);
	}

	/**
	 * Generates an {@link Observable} source from the specified event queue with the specified name in the specified scope.
	 *
	 * @param queueName the queue name.
	 * @param scope the scope of the event queue. Like {@link EventQueues#DESKTOP} or {@link EventQueues#APPLICATION}.
	 * @param autoCreate whether to create the event queue if not found.
	 * @param async whether the listener is asynchronous.
	 * @return An observable source.
	 */
	public static Observable<? extends Event> fromEventQueue(final String queueName, final String scope, final boolean autoCreate, final boolean async) {
		return Observable.fromPublisher(ZkReactiveStreams.fromEventQueue(queueName, scope, autoCreate, async));
	}

	/**
	 * Generates an {@link Observable} source from the specified event queue.
	 * The listener is synchronous.
	 *
	 * @param eventQueue the event queue
	 * @return An observable source.
	 */
	public static <T extends Event> Observable<T> fromEventQueue(final EventQueue<T> eventQueue) {
		return fromEventQueue(eventQueue, false);
	}

	/**
	 * Generates an {@link Observable} source from the specified event queue.
	 *
	 * @param eventQueue the event queue
	 * @param async whether the listener is asynchronous.
	 * @return An observable source.
	 */
	public static <T extends Event> Observable<T> fromEventQueue(final EventQueue<T> eventQueue, final boolean async) {
		return Observable.fromPublisher(ZkReactiveStreams.fromEventQueue(eventQueue, async));
	}
}
