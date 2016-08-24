package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrappr</code> for a inhibit status response, ensuring
 * the bit set received corresponds to proper inhibit mask(s).
 * @author Pierre Beucher
 *
 */
public class InhibitStatusResponseWrapper extends BitSetResponseWrapper {

	public static InhibitStatusResponseWrapper wrap(Message m) throws UnexpectedContentException{
		InhibitStatusResponseWrapper mw = new InhibitStatusResponseWrapper(m);
		mw.wrapContent();
		return mw;
	}
	
	protected InhibitStatusResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		if(message.getDataBytes().length < 2){
			throw new UnexpectedContentException("Expected at least 2 inhibit mask bytes.");
		}
		
		super.wrapContent();
	}
}
