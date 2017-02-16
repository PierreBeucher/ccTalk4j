package com.github.pierrebeucher.cctalk4j.device;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * Wrapper for a response obtained by a MessageReader.
 * A message is normally received, but there may also have
 * a timeout, an exception or any other element the caller thread
 * may need to retrieve.
 * @author Pierre Beucher
 *
 */
public class MessageReaderResponse {
	
	private Exception exception;
	
	private boolean hasTimedOut;

	private Message message;
	
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public boolean isHasTimedOut() {
		return hasTimedOut;
	}

	public void setHasTimedOut(boolean hasTimedOut) {
		this.hasTimedOut = hasTimedOut;
	}
}
