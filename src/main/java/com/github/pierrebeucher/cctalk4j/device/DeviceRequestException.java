package com.github.pierrebeucher.cctalk4j.device;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;

public class DeviceRequestException extends CcTalkException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2154915537278868854L;
	
	
	/**
	 * 
	 */
	public DeviceRequestException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected DeviceRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DeviceRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DeviceRequestException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DeviceRequestException(Throwable cause) {
		super(cause);
	}
	

}
