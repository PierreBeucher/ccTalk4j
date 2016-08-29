package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrapper</code> used to wrap a self check response (Header 232).
 * @author Pierre Beucher
 *
 */
public class SelfCheckResponseWrapper extends ResponseWrapper {

	public static SelfCheckResponseWrapper wrap(Message m) throws UnexpectedContentException{
		SelfCheckResponseWrapper wp = new SelfCheckResponseWrapper(m);
		wp.wrapContent();
		return wp;
	}
	
	/**
	 * Fault code indicating no fault detected.
	 */
	public static final byte NO_FAULT_DETECTED = 0;
	
	private byte faultCode;
	private Byte optionalExtraInfo;
	
	protected SelfCheckResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		if(message.getDataBytes().length < 1 || message.getDataBytes().length > 2){
			throw new UnexpectedContentException("Expected 1 or 2 bytes (fault and optional extra info)"
					+ ", got " + message.getDataBytes().length + " bytes.");
		}
		
		this.faultCode = message.getDataBytes()[0];
		if(message.getDataBytes().length > 1){
			this.optionalExtraInfo = message.getDataBytes()[1];
		}
	}
	
	/**
	 * 
	 * @return the wrapped message's fault code
	 */
	public byte faultCode(){
		return this.faultCode;
	}
	
	/**
	 * 
	 * @return true if the wrapped message's fault code is not equals to {@link #NO_FAULT_DETECTED}
	 */
	public boolean hasFault(){
		return this.faultCode != NO_FAULT_DETECTED;
	}
	
	/**
	 * 
	 * @return the wrapped message's optional extra info, or null if there is not extra info.
	 */
	public Byte optionalExtraInfo(){
		return this.optionalExtraInfo;
	}
}
