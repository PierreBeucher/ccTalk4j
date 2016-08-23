package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;

/**
 * Exception thrown by a <code>MessageWrapper</code> when an
 * error is encountered wrapping a message, such as unrecognized
 * message content.
 * @author Pierre Beucher
 *
 */
public class UnexpectedContentException extends CcTalkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1422319592467672984L;
	
	/**
	 * 
	 */
	public UnexpectedContentException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	protected UnexpectedContentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnexpectedContentException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UnexpectedContentException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnexpectedContentException(Throwable cause) {
		super(cause);
	}

}
