package com.github.pierrebeucher.cctalk4j.core;

/**
 * Thrown when a timeout occur when reading/writing a <code>MessagePort<cpde>
 * @author Pierre Beucher
 *
 */
public class MessagePortTimeoutException extends MessagePortException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3434196879215791310L;
	
	/**
	 * 
	 */
	public MessagePortTimeoutException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MessagePortTimeoutException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessagePortTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MessagePortTimeoutException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MessagePortTimeoutException(Throwable cause) {
		super(cause);
	}

}
