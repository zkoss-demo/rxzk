/* AsyncUpdateUiComposer.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 27 11:16:43 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class AsyncUpdateUiComposer extends GenericForwardComposer<Component> {
	@WireVariable
	private Desktop desktop;

	@Wire
	private Textbox term;

	@Wire
	private Label result;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		ZkServerPush.enable(desktop);
		ZkObservable.fromEvent(term, Events.ON_CHANGING)
				.debounce(300, TimeUnit.MILLISECONDS)
				.map(new Function<Event, String>() {
					public String apply(Event event) throws Exception {
						return ((InputEvent) event).getValue();
					}
				})
				.filter(new Predicate<String>() {
					public boolean test(String s) throws Exception {
						return s.length() >= 3;
					}
				})
				.distinctUntilChanged()
				.observeOn(Schedulers.io())
				.map(new Function<String, String>() {
					public String apply(String e) throws Exception {
						Threads.sleep(2000); // simulate heavy load
						return e + "!";
					}
				})
				.subscribe(ZkServerPush.updateUi(desktop, new Consumer<String>() {
					public void accept(String s) throws Exception {
						result.setValue("You want to search: " + s);
					}
				}));
	}
}
