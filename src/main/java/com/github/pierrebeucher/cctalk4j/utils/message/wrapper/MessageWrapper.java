package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>MessageWrapper</code> is used to wrap a message
 * and provide an interface to easily access the internal
 * content of the message. 
 * @author Pierre Beucher
 *
 */
public interface MessageWrapper {

	/**
	 * 
	 * @return the wrapped message
	 */
	Message getWrappedMessage();
}
