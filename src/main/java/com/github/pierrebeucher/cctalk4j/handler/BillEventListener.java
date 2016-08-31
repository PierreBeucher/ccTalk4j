package com.github.pierrebeucher.cctalk4j.handler;

import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;

/**
 * <p>
 * <code>BillEventListener</code> used by <code>BillEventHandler</code>
 * to notidy new events when reading a <code>BillValidator</code> event
 * buffer.</p>
 * <p>This interface two methods: {@link #newEvent(BillValidator, BillEvent)}
 * to notify a new event has been read, and {@link #lostEvent(BillEventBuffer, BillEventBuffer)}
 * to notify events has been lost. </p>
 * 
 * @author Pierre Beucher
 *
 */
public interface BillEventListener {
	
	/**
	 * Called for every new events read.
	 * @param handler handler which read the event
	 * @param event event read
	 */
	void newEvent(BillValidatorHandler handler, BillEvent event);
	
	/**
	 * Called when an event loss has been detected.
	 * @param lostEventCount number of event lost
	 * @param previousBuffer event buffer before event loss
	 * @param newBuffer event buffer after event loss
	 */
	void lostEvent(int lostEventCount, BillEventBuffer previousBuffer, BillEventBuffer newBuffer);
}
