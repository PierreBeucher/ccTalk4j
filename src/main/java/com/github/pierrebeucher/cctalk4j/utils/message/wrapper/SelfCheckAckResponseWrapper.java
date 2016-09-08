package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * A <code>SelfCheckResponseWrapper</code> accepting Ack messages as answer.
 * @author Pierre Beucher
 *
 */
public class SelfCheckAckResponseWrapper extends SelfCheckResponseWrapper {

	public static SelfCheckAckResponseWrapper wrap(Message m) throws UnexpectedContentException{
		SelfCheckAckResponseWrapper wp = new SelfCheckAckResponseWrapper(m);
		wp.wrapContent();
		return wp;
	}
	
	protected SelfCheckAckResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		if(AckWrapper.isAck(message)){
			return;
		}
		
		super.wrapContent();
	}

}
