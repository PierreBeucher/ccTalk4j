package com.github.pierrebeucher.cctalk4j.device;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;

public class DeviceException extends CcTalkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6456288813568149005L;
	
	/**
	 * 
	 */
	public DeviceException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected DeviceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DeviceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DeviceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DeviceException(Throwable cause) {
		super(cause);
	}
}
