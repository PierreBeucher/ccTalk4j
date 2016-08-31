package com.github.pierrebeucher.cctalk4j.handler;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;

/**
 * Thrown when an operation performed by a <code>DeviceHandler</code> failed.
 * @author Pierre Beucher
 *
 */
public class DeviceHandlingException extends CcTalkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1786821317984033780L;

	/**
	 * 
	 */
	public DeviceHandlingException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected DeviceHandlingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DeviceHandlingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DeviceHandlingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DeviceHandlingException(Throwable cause) {
		super(cause);
	}
}
