package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * Wrapper for an ACK message.
 * @author Pierre Beucher
 *
 */
public class AckWrapper extends ResponseWrapper {
	
	public static AckWrapper wrap(Message m) throws UnexpectedContentException{
		AckWrapper mw = new AckWrapper(m);
		mw.wrapContent();
		return mw;
	}
	
	private AckWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		if(message.getDataBytes().length > 0){
			throwWrappingException("ACK message is not supposed to contain data bytes.");
		}
		
		if(message.getHeader() != Header.NONE.getValue()){
			throwWrappingException("ACK message is not supposed to contain a header value.");
		}
	}
	
	public byte destination(){
		return message.getDestination();
	}

}
