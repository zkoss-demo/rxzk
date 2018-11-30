package org.zkoss.addons.rxzk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Layout;

/**
 * @author rudyhuang
 */
class ZkObservableTest {
	@BeforeAll
	static void init() {
		Zats.init("./src/test/webapp");
	}

	@Test
	void testAddSubtract() {
		DesktopAgent desktop = Zats.newClient().connect("/add-subtract-test.zul");

		ComponentAgent btnAdd = desktop.query("#btnAdd");
		ComponentAgent btnSubtract = desktop.query("#btnSubtract");
		Label number = desktop.query("#number").as(Label.class);
		btnAdd.click();
		btnSubtract.click();
		btnAdd.click();
		btnAdd.click();
		btnSubtract.click();
		btnAdd.click();

		assertEquals("2", number.getValue());
	}

	@Test
	void testChatRoom() {
		DesktopAgent desktop = Zats.newClient().connect("/chat-room-test.zul");

		ComponentAgent msg = desktop.query("#msg");
		msg.type("Hello World!");
		msg.stroke("#enter");

		Layout inf = desktop.query("#inf").as(Layout.class);
		assertFalse(inf.getChildren().isEmpty());
	}

	@AfterEach
	void tearDown() {
		Zats.cleanup();
	}

	@AfterAll
	static void end() {
		Zats.end();
	}
}
