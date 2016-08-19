package com.github.pierrebeucher.cctalk4j.core;

public class CcTalkException extends Exception {

	/**
	 * 
	 */
	public CcTalkException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected CcTalkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CcTalkException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CcTalkException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CcTalkException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1145626884970231951L;
	
	

}
