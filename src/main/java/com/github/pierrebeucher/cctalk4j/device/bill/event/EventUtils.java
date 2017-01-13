package com.github.pierrebeucher.cctalk4j.device.bill.event;

public class EventUtils {
	
	private EventUtils(){}

	/**
	 * Return the bill type of the bill associated to the given event.
	 * @param event event for which a bill type is to be identified
	 * @return event bill event type byte
	 * @throws BadEventException if the event is neither CREDIT nor PENDING_CREDIT
	 */
	public static byte getBillTypeForEvent(BillEvent event) throws BadEventException{
		if(event.getEventType() != EventType.CREDIT && event.getEventType() != EventType.PENDING_CREDIT){
			throw new BadEventException("Cannot match event " + event + " to a bill: not recognized as a credit or pending credit event type.");
		}
		
		return event.getResultA();
	}
}
