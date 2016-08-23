package com.github.pierrebeucher.cctalk4j.utils.message.builder;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>MessageBuilder</code> is used to build messages.
 * Message content is defined before calling {@link #build()}
 * method to create an instance of the configured message.
 * @author Pierre Beucher
 *
 */
public interface MessageBuilder {

	/**
	 * Empty data byte array set by default, unles {@link #data(byte[])}
	 * is called.
	 */
	public static final byte[] EMPTY_DATA = new byte[]{};
	
	/**
	 * Set the destination for the message to build.
	 * @param dest destination to set
	 * @return this <code>MessageBuilder</code>
	 */
	public MessageBuilder destination(byte dest);
	
	/**
	 * Set the source for the message to build.
	 * @param source source to set
	 * @return this <code>MessageBuilder</code>
	 */
	public MessageBuilder source(byte source);
	
	/**
	 * Set the header byte for the message to build.
	 * Same as calling {@link #header(Header)} with
	 * a <code>Header</code>.
	 * @param header header to set
	 * @return this <code>MessageBuilder</code>
	 */
	public MessageBuilder header(byte header);
	
	/**
	 * Set the header for the message to build.
	 * Same as calling {@link #header(byte)} with
	 * a byte.
	 * @param header header to set
	 * @return this <code>MessageBuilder</code>
	 */
	public MessageBuilder header(Header h);
	
	/**
	 * Set the data byte array for the message to build.
	 * By default, {@link #EMPTY_DATA} is set as the data
	 * byte array for this <code>MessageBuilder</code>.
	 * Calling this function will override any existing
	 * data byte array.
	 * @param data data byte array to set
	 * @return this <code>MessageBuilder</code>
	 */
	public MessageBuilder data(byte[] data);
	
	/**
	 * Build the effective Message.
	 * @return build message
	 * @throws MessageBuildException if defined message content is not consistent
	 */
	public Message build() throws MessageBuildException;
}
