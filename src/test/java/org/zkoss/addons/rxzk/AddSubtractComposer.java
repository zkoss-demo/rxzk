/* AddSubtractComposer.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 30 11:47:46 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class AddSubtractComposer extends SelectorComposer<Div> {
	@Wire
	private Label number;

	@Wire
	private Button btnAdd;

	@Wire
	private Button btnSubtract;

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		ZkObservable.fromEvent(btnAdd, Events.ON_CLICK)
				.map(new Function<Event, Integer>() {
					public Integer apply(Event e) {
						return 1;
					}
				})
				.mergeWith(
						ZkObservable.fromEvent(btnSubtract, Events.ON_CLICK)
								.map(new Function<Event, Integer>() {
									public Integer apply(Event e) {
										return -1;
									}
								}))
				.scan(0, new BiFunction<Integer, Integer, Integer>() {
					public Integer apply(Integer a, Integer b) {
						return a + b;
					}
				})
				.subscribe(new Consumer<Integer>() {
					public void accept(Integer v) {
						number.setValue(String.valueOf(v));
					}
				});
	}
}
