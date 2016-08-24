package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrapper</code> for a boolean message.
 * A boolean message is characterized by no header defined
 * and a single data byte either 0 (false) or 1 (true). 
 * @author Pierre Beucher
 *
 */
public class BooleanResponseWrapper extends ResponseWrapper {

	private boolean booleanValue;
	
	public static BooleanResponseWrapper wrap(Message m) throws UnexpectedContentException{
		BooleanResponseWrapper mw = new BooleanResponseWrapper(m);
		mw.wrapContent();
		return mw;
	}
	
	protected BooleanResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		if(message.getDataBytes().length != 1) {
			throw new UnexpectedContentException("Boolean response cannot contain more than one data byte.");
		}
		
		if(message.getDataBytes()[0] == 1){
			this.booleanValue = true;
		} else if (message.getDataBytes()[0] == 0){
			this.booleanValue = false;
		} else {
			throw new UnexpectedContentException("Boolean response data byte must be either 0 or 1");
		}
	}
	
	public boolean booleanValue(){
		return this.booleanValue;
	}

}
