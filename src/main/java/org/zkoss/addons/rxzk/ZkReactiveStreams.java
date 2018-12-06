/* ZkReactiveStreams.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 30 10:11:53 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import org.zkoss.addons.rxzk.internal.subscription.ComponentEventSubscription;
import org.zkoss.addons.rxzk.internal.subscription.EventQueueSubscription;
import org.zkoss.addons.rxzk.internal.subscription.ExistingEventQueueSubscription;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;

/**
 * An util class that provides Reactive Streams support.
 *
 * @author rudyhuang
 */
public abstract class ZkReactiveStreams {
	/**
	 * Generates a {@link Publisher} from an event of the ZK component.
	 *
	 * @param comp a ZK component.
	 * @param event an event name. Using predefined {@link Events} constants are preferred.
	 * @return A publisher instance.
	 */
	public static Publisher<? extends Event> fromEvent(final Component comp, final String event) {
		return new Publisher<Event>() {
			public void subscribe(Subscriber<? super Event> subscriber) {
				if (subscriber == null)
					throw new NullPointerException();

				subscriber.onSubscribe(new ComponentEventSubscription(comp, event, subscriber));
			}
		};
	}

	/**
	 * Generates a {@link Publisher} from the specified event queue with the specified name in the desktop scope.
	 * Create one if no such event queue.
	 * The listener is synchronous.
	 *
	 * @param queueName the queue name.
	 * @return A publisher instance.
	 */
	public static Publisher<? extends Event> fromEventQueue(String queueName) {
		return fromEventQueue(queueName, EventQueues.DESKTOP, true, false);
	}

	/**
	 * Generates a {@link Publisher} from the specified event queue with the specified name in the specified scope.
	 * Create one if no such event queue.
	 * The listener is synchronous.
	 *
	 * @param queueName the queue name.
	 * @param scope the scope of the event queue. Like {@link EventQueues#DESKTOP} or {@link EventQueues#APPLICATION}.
	 * @return A publisher instance.
	 */
	public static Publisher<? extends Event> fromEventQueue(String queueName, String scope) {
		return fromEventQueue(queueName, scope, true, false);
	}

	/**
	 * Generates a {@link Publisher} from the specified event queue with the specified name in the specified scope.
	 * The listener is synchronous.
	 *
	 * @param queueName the queue name.
	 * @param scope the scope of the event queue. Like {@link EventQueues#DESKTOP} or {@link EventQueues#APPLICATION}.
	 * @param autoCreate whether to create the event queue if not found.
	 * @return A publisher instance.
	 */
	public static Publisher<? extends Event> fromEventQueue(String queueName, String scope, boolean autoCreate) {
		return fromEventQueue(queueName, scope, autoCreate, false);
	}

	/**
	 * Generates a {@link Publisher} from the specified event queue with the specified name in the specified scope.
	 *
	 * @param queueName the queue name.
	 * @param scope the scope of the event queue. Like {@link EventQueues#DESKTOP} or {@link EventQueues#APPLICATION}.
	 * @param autoCreate whether to create the event queue if not found.
	 * @param async whether the listener is asynchronous.
	 * @param <T> the type of event
	 * @return A publisher instance.
	 */
	public static <T extends Event> Publisher<? extends Event> fromEventQueue(final String queueName, final String scope, final boolean autoCreate, final boolean async) {
		return new Publisher<Event>() {
			public void subscribe(Subscriber<? super Event> subscriber) {
				if (subscriber == null)
					throw new NullPointerException();

				subscriber.onSubscribe(new EventQueueSubscription<T>(queueName, scope, autoCreate, async, subscriber));
			}
		};
	}

	/**
	 * Generates a {@link Publisher} from the specified event queue.
	 * The listener is synchronous.
	 *
	 * @param eventQueue the event queue
	 * @param <T> the type of event
	 * @return A publisher instance.
	 */
	public static <T extends Event> Publisher<T> fromEventQueue(final EventQueue<T> eventQueue) {
		return fromEventQueue(eventQueue, false);
	}

	/**
	 * Generates a {@link Publisher} from the specified event queue.
	 *
	 * @param eventQueue the event queue
	 * @param async whether the listener is asynchronous.
	 * @param <T> the type of event
	 * @return A publisher instance.
	 */
	public static <T extends Event> Publisher<T> fromEventQueue(final EventQueue<T> eventQueue, final boolean async) {
		return new Publisher<T>() {
			public void subscribe(Subscriber<? super T> subscriber) {
				if (subscriber == null)
					throw new NullPointerException();

				subscriber.onSubscribe(new ExistingEventQueueSubscription<T>(eventQueue, async, subscriber));
			}
		};
	}

}
