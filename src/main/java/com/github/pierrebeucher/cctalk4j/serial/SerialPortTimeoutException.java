package com.github.pierrebeucher.cctalk4j.serial;

public class SerialPortTimeoutException extends SerialPortException {

	public SerialPortTimeoutException() {
		super();
	}

	protected SerialPortTimeoutException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SerialPortTimeoutException(String message, Throwable cause) throws SerialPortException {
		super(message, cause);
	}

	public SerialPortTimeoutException(String message) throws SerialPortException {
		super(message);
	}

	public SerialPortTimeoutException(Throwable cause) throws SerialPortException {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7537862106247549174L;

}
