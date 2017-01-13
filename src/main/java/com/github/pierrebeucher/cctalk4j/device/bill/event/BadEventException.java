package com.github.pierrebeucher.cctalk4j.device.bill.event;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;

public class BadEventException extends CcTalkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1964360249527525205L;

	/**
	 * 
	 */
	public BadEventException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected BadEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BadEventException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public BadEventException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BadEventException(Throwable cause) {
		super(cause);
	}

}
