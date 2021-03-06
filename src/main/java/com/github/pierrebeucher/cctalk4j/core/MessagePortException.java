package com.github.pierrebeucher.cctalk4j.core;

/**
 * Thrown when an error occur using a <code>MessagePort</code>.
 * @author Pierre Beucher
 *
 */
public class MessagePortException extends MessageIOException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1996178835692130739L;
	
	/**
	 * 
	 */
	public MessagePortException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected MessagePortException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessagePortException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MessagePortException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MessagePortException(Throwable cause) {
		super(cause);
	}
	
}
