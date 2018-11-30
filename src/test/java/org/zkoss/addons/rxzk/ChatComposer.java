/* ChatComposer.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 30 12:25:59 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Layout;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * @author rudyhuang
 */
public class ChatComposer extends SelectorComposer<Window> {
	private EventQueue<Event> que = EventQueues.lookup("chat", EventQueues.APPLICATION, true);

	@Wire
	private Layout inf;
	@Wire
	private Textbox msg;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		ZkObservable.fromEventQueue(que)
				.map(new Function<Event, String>() {
					public String apply(Event e) {
						return String.valueOf(e.getData());
					}
				})
				.subscribe(new Consumer<String>() {
					public void accept(String s) {
						new Label(s).setParent(inf);
					}
				});

		ZkObservable.fromEvent(msg, Events.ON_CHANGE)
				.map(new Function<Event, String>() {
					public String apply(Event e) {
						return ((InputEvent) e).getValue();
					}
				})
				.distinctUntilChanged()
				.filter(new Predicate<String>() {
					public boolean test(String s) {
						return !Strings.isEmpty(s);
					}
				}).subscribe(new Consumer<String>() {
					public void accept(String text) {
						msg.setValue("");
						que.publish(new Event("onChat", null, text));
					}
				});
	}
}
