package com.github.pierrebeucher.cctalk4j.device.bill.event;

import java.util.Arrays;

/**
 * Representation of a Bill Event Buffer obtained using Header 159.
 * @author Pierre Beucher
 *
 */
public class BillEventBuffer {
	
	/**
	 * The number of events contained in a single <code>BillEventBuffer</code>
	 */
	public static final int EVENT_BUFFER_SIZE = 5;

	private BillEvent[] billEvents;
	private byte eventCounter;
	
	/**
	 * @param billEvents
	 * @param eventCounter
	 */
	public BillEventBuffer(BillEvent[] billEvents, byte eventCounter) {
		super();
		this.billEvents = billEvents;
		this.eventCounter = eventCounter;
	}

	public BillEvent[] getBillEvents() {
		return billEvents;
	}

	public byte getEventCounter() {
		return eventCounter;
	}

	@Override
	public String toString() {
		return "[" + eventCounter + "|" + Arrays.toString(billEvents) + "]";
	}
	
}
