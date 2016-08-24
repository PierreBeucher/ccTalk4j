package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.UnrecognizedEventException;

/**
 * <code>MessageWrapper</code> for a bill event buffer.
 * @author Pierre Beucher
 *
 */
public class BillEventBufferResponseWrapper extends ResponseWrapper{
	
	public static BillEventBufferResponseWrapper wrap(Message m) throws UnexpectedContentException{
		BillEventBufferResponseWrapper mw = new BillEventBufferResponseWrapper(m);
		mw.wrapContent();
		return mw;
	}
	
	/**
	 * Length of the expected event buffer: 1 event counter byte + 10 event bytes
	 */
	public static final int EVENT_BUFFER_LENGTH = 11;
	
	private BillEvent[] billEvents;
	private byte eventCounter;
	
	protected BillEventBufferResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		byte[] data = message.getDataBytes();
		
		if(data.length != EVENT_BUFFER_LENGTH){
			throw new UnexpectedContentException("Event buffer data length must be equals to " + EVENT_BUFFER_LENGTH);
		}
		
		eventCounter = data[0];
		billEvents = new BillEvent[5];
		
		//parse the bill event buffer byte array
		int billEventIndex = 0; //index in billEvents array
		for(int i=1; i<data.length; i+=2){
			byte resultA = data[i];
			byte resultB = data[i+1];
			try {
				billEvents[billEventIndex] = BillEvent.event(resultA, resultB);
				billEventIndex++;
			} catch (UnrecognizedEventException e) {
				throw new UnexpectedContentException(e);
			}
		}
	}

	public BillEvent[] getBillEvents() {
		return billEvents;
	}

	public byte getEventCounter() {
		return eventCounter;
	}

}
