package com.github.pierrebeucher.cctalk4j.handler;

public class CreditPollingException extends DeviceHandlingException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1946301171837761463L;

	public CreditPollingException() {
		super();
	}

	protected CreditPollingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CreditPollingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreditPollingException(String message) {
		super(message);
	}

	public CreditPollingException(Throwable cause) {
		super(cause);
	}

}
