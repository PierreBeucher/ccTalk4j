package com.github.pierrebeucher.cctalk4j.device.bill.validator;

import com.github.pierrebeucher.cctalk4j.device.DeviceException;

/**
 * Thrown when a bill routing request (Header 154) returned an error code.
 * @author Pierre Beucher
 *
 */
public class BillRoutingException extends DeviceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1983418663664622829L;

	/**
	 * 
	 */
	public BillRoutingException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected BillRoutingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BillRoutingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public BillRoutingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BillRoutingException(Throwable cause) {
		super(cause);
	}
}
