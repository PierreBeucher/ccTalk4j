package com.github.pierrebeucher.cctalk4j.utils.message.builder;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;

/**
 * Thrown when a MessageBuilder is trying to build an incorrectly defined message.
 * @author Pierre Beucher
 *
 */
public class MessageBuildException extends CcTalkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -186893344824445978L;
	
	/**
	 * 
	 */
	public MessageBuildException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected MessageBuildException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageBuildException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MessageBuildException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MessageBuildException(Throwable cause) {
		super(cause);
	}

}
