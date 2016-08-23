package com.github.pierrebeucher.cctalk4j.device;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

/**
 * Base interface for any device.
 * 
 * @author Pierre Beucher
 *
 */
public interface Device {

	/**
	 * Open a connection to this device.
	 * @throws MessagePortException
	 */
	public void connect() throws MessagePortException;
	
	/**
	 * Close any open connection to this device.
	 * @throws MessagePortException 
	 */
	public void disconnect() throws MessagePortException;
	
	/**
	 * 
	 * @return true if a connection to the device is active
	 */
	public boolean isConnected();
	
	/**
	 * 
	 * @return current read timeout in ms
	 */
	public int getReadTimeout();

	/**
	 * Set the timeout when reading a message.
	 * @param readTimeout timeout in ms
	 */
	public void setReadTimeout(int readTimeout);

	/**
	 * 
	 * @return current write timeout in ms
	 */
	public int getWriteTimeout();

	/**
	 * Set the timeout when writing a message.
	 * @param readTimeout timeout in ms
	 */
	public void setWriteTimeout(int writeTimeout);
	
	/**
	 * <p>Perform a simple poll. Based on header 254.</p>
	 * @return true if an ACK is received, false if no response is received.
	 * @throws MessageIOException if write timeout occur
	 * @throws UnexpectedMessageException if a non-ACK response is received
	 * @throws UnexpectedContentException 
	 */
	boolean simplePoll() throws MessageIOException, UnexpectedContentException;
	
	/**
	 * Request the device manufacturer's unique ID.
	 * Based on header 246.
	 * @return device manufacturer's unique ID as String
	 * @throws MessageIOException,
	 * @throws UnexpectedContentException 
	 */
	String requestManufacturerId() throws MessageIOException, UnexpectedContentException;
	
	/**
	 * Request the device equipment category ID.
	 * Based on header 245.
	 * @return equipment category ID as String
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException
	 */
	String requestEquipmentCategoryId() throws MessageIOException, UnexpectedContentException;
	
	/**
	 * Request the device product code.
	 * Based on header 244.
	 * @return equipment category ID as String
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException
	 */
	String requestProductCode() throws MessageIOException, UnexpectedContentException;
	
	/**
	 * Request the device build code.
	 * Based on header 192.
	 * @return device build code as String
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException
	 */
	String requestBuildCode() throws MessageIOException, UnexpectedContentException;
	
	/**
	 * Request the device encryption support. Based on header 111.
	 * @return device encryption support.
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException
	 */
	Object requestEncryptionSupport() throws MessageIOException, UnexpectedContentException;
	
	
}
