package com.github.pierrebeucher.cctalk4j.device.bill.event;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BillEventBufferTest {

	@Test
	public void getBillEvents() {
		BillEvent[] events = new BillEvent[]{};
		BillEventBuffer eventBuf = new BillEventBuffer(events, (byte) 5);
		Assert.assertEquals(eventBuf.getBillEvents(), events);
	}

	@Test
	public void getEventCounter() {
		BillEvent[] events = new BillEvent[]{};
		BillEventBuffer eventBuf = new BillEventBuffer(events, (byte) 5);
		Assert.assertEquals(eventBuf.getEventCounter(), (byte) 5);
	}
}
