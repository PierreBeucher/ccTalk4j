package com.github.pierrebeucher.cctalk4j.serial;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;

public class SerialPortException extends CcTalkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1893900627646767833L;

	public SerialPortException() {
		super();
	}

	protected SerialPortException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SerialPortException(String message, Throwable cause) throws SerialPortException {
		super(message, cause);
	}

	public SerialPortException(String message) throws SerialPortException {
		super(message);
	}

	public SerialPortException(Throwable cause) throws SerialPortException {
		super(cause);
	}

}
