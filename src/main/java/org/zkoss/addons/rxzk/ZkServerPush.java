/* ZkServerPush.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 14 17:41:19 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.addons.rxzk;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * ZK Server-push feature helper.
 *
 * @author rudyhuang
 */
public class ZkServerPush {
	/**
	 * Enables the server-push feature.
	 * Before using any server-push threads, you have to enable it for the particular desktop first by use of this method.
	 * Refer to {@link Executions#activate(Desktop)} for more details.
	 *
	 * @param desktop the desktop
	 */
	public static void enable(Desktop desktop) {
		if (!desktop.isServerPushEnabled())
			desktop.enableServerPush(true);
	}

	/**
	 * Disables the server-push feature.
	 * For better performance, it is suggested to disable the server push if it is no longer used in the give desktop.
	 *
	 * @param desktop the desktop
	 */
	public static void disable(Desktop desktop) {
		if (desktop.isServerPushEnabled())
			desktop.enableServerPush(false);
	}

	/**
	 * Runs the job to update UI.
	 *
	 * @param desktop the desktop
	 * @param job runnable job
	 * @throws IllegalStateException if the server push is not enabled.
	 * @throws DesktopUnavailableException if the desktop is removed (when activating).
	 */
	public static void updateUi(Desktop desktop, final Runnable job) throws IllegalStateException, DesktopUnavailableException {
		if (Events.inEventListener() || Executions.getCurrent() != null) {
			job.run();
		} else {
			Executions.schedule(desktop, new EventListener<Event>() {
				public void onEvent(Event event) throws Exception {
					job.run();
				}
			}, null);
		}
	}
}
