package com.github.pierrebeucher.cctalk4j.device.bill.event;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;

/**
 * Thrown when an event is not recognized.
 * @author Pierre Beucher
 *
 */
public class UnrecognizedEventException extends CcTalkException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1996178835692130739L;
	
	/**
	 * 
	 */
	public UnrecognizedEventException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected UnrecognizedEventException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnrecognizedEventException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UnrecognizedEventException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnrecognizedEventException(Throwable cause) {
		super(cause);
	}
	
}
