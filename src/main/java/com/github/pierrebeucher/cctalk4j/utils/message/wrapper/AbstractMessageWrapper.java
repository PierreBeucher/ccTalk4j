package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

public abstract class AbstractMessageWrapper implements MessageWrapper {
	
	protected Message message;
	
	/**
	 * 
	 * @param message
	 * @throws UnexpectedContentException 
	 */
	protected AbstractMessageWrapper(Message message) throws UnexpectedContentException {
		super();
		if(message == null){
			throw new NullPointerException("Message cannot be null.");
		}
		this.message = message;
	}

	public Message getWrappedMessage() {
		return message;
	}
	
	/**
	 * Wrap the message content to ensure it corresponds
	 * to this <code>Wrapper</code> interface.
	 * @throws UnexpectedContentException if the message content is invalid
	 */
	protected abstract void wrapContent() throws UnexpectedContentException;
	
	protected void throwWrappingException(String message) throws UnexpectedContentException{
		throw new UnexpectedContentException(message);
	}
	
	
}
