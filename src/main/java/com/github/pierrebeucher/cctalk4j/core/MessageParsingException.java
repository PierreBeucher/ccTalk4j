package com.github.pierrebeucher.cctalk4j.core;

/**
 * Thrown when a message parsing exception occur, i.e. when
 * a message does not match the appropriate message structure
 * (destination, header, data, etc.)
 * @author Pierre Beucher
 *
 */
public class MessageParsingException extends MessageIOException{

	/**
	 * 
	 */
	public MessageParsingException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected MessageParsingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MessageParsingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MessageParsingException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3553093952290970075L;
	
	

}
