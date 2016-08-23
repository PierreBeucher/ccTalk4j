package com.github.pierrebeucher.cctalk4j.core;

/**
 * Thrown when an IO exception occur during message processing.
 * @author Pierre Beucher
 *
 */
public class MessageIOException extends CcTalkException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7619986906034214820L;
	
	/**
	 * 
	 */
	public MessageIOException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MessageIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageIOException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MessageIOException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MessageIOException(Throwable cause) {
		super(cause);
	}

}
