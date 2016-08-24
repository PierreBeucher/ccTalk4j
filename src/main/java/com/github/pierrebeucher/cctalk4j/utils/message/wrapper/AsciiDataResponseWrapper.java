package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import java.nio.charset.Charset;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>MessageWrapper</code> for messages 
 * containing ASCII data as payload. 
 * @author Pierre Beucher
 *
 */
public class AsciiDataResponseWrapper extends ResponseWrapper{

	public static AsciiDataResponseWrapper wrap(Message m) throws UnexpectedContentException{
		AsciiDataResponseWrapper mw = new AsciiDataResponseWrapper(m);
		mw.wrapContent();
		return mw;
	}
	
	protected AsciiDataResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		if(message.getDataBytes().length == 0){
			throwWrappingException("At least one data byte is expected.");
		}
	}
	
	/**
	 * 
	 * @return the ASCII data payload contained in the wrapped message
	 */
	public String getAsciiData(){
		return new String(message.getDataBytes(), Charset.forName("ASCII"));
	}

}
