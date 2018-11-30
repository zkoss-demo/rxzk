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
import org.reactivestreams.Subscription;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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

				subscriber.onSubscribe(new ExistedEventQueueSubscription<T>(eventQueue, async, subscriber));
			}
		};
	}

	static class ComponentEventSubscription implements Subscription {
		private final Component _comp;
		private final String _event;
		private final Subscriber<? super Event> _subscriber;
		private boolean _requested;

		ComponentEventSubscription(Component comp, String event,
		                           Subscriber<? super Event> subscriber) {
			this._comp = comp;
			this._event = event;
			this._subscriber = subscriber;
		}

		public void request(long n) {
			if (!_requested) {
				try {
					_comp.addEventListener(_event, new EventListener<Event>() {
						public void onEvent(Event t) throws Exception {
							_subscriber.onNext(t);
						}
					});
					_requested = true;
				} catch (IllegalArgumentException e) {
					_subscriber.onError(e);
				}
			}
		}

		public void cancel() {
			if (_requested) {
				_comp.removeEventListener(_event, new EventListener<Event>() {
					public void onEvent(Event t) throws Exception {
						_subscriber.onNext(t);
					}
				});
			}
		}
	}

	static class EventQueueSubscription<T extends Event> implements Subscription {
		private final String _queueName;
		private final String _scope;
		private final boolean _autoCreate;
		private final Subscriber<? super T> _subscriber;
		private final boolean _async;
		private boolean _requested;

		EventQueueSubscription(String queueName, String scope, boolean autoCreate,
		                       boolean async, Subscriber<? super T> subscriber) {
			this._queueName = queueName;
			this._scope = scope;
			this._autoCreate = autoCreate;
			this._async = async;
			this._subscriber = subscriber;
		}

		public void request(long n) {
			if (!_requested) {
				try {
					getEventQueue().subscribe(new EventListener<T>() {
						public void onEvent(T t) throws Exception {
							_subscriber.onNext(t);
						}
					}, _async);
					_requested = true;
				} catch (Exception e) {
					_subscriber.onError(e);
				}
			}
		}

		public void cancel() {
			if (_requested) {
				getEventQueue().unsubscribe(new EventListener<T>() {
					public void onEvent(T t) throws Exception {
						_subscriber.onNext(t);
					}
				});
			}
		}

		protected EventQueue<T> getEventQueue() {
			return EventQueues.lookup(_queueName, _scope, _autoCreate);
		}
	}

	static class ExistedEventQueueSubscription<T extends Event> extends EventQueueSubscription<T> {
		private EventQueue<T> _eventQueue;

		ExistedEventQueueSubscription(EventQueue<T> eventQueue, boolean async, Subscriber<? super T> subscriber) {
			super("", "", false, async, subscriber);
			this._eventQueue = eventQueue;
		}

		@Override
		protected EventQueue<T> getEventQueue() {
			return _eventQueue;
		}
	}
}
