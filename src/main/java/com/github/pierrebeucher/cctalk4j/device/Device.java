package com.github.pierrebeucher.cctalk4j.device;

import java.util.List;

import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessageParsingException;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.SelfCheckResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

/**
 * Base interface for any device.
 * 
 * @author Pierre Beucher
 *
 */
public interface Device {

	/**
	 * Open a connection to this device and call any configurator
	 * affected to this device.
	 * @see #addConfigurator(DeviceConfigurator)
	 * @throws MessagePortException
	 */
	public void connect() throws MessagePortException, DeviceConfigurationException;
	
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
	 * @throws DeviceRequestException if ack fails (timeout or received a non-ack message)
	 */
	void simplePoll() throws DeviceRequestException;
	
	/**
	 * Request the device manufacturer's unique ID.
	 * Based on header 246.
	 * @return device manufacturer's unique ID as String
	 */
	String requestManufacturerId() throws DeviceRequestException;
	
	/**
	 * Request the device equipment category ID.
	 * Based on header 245.
	 * @return equipment category ID as String
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException
	 */
	String requestEquipmentCategoryId() throws DeviceRequestException;
	
	/**
	 * Request the device product code.
	 * Based on header 244.
	 * @return equipment category ID as String
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException
	 */
	String requestProductCode() throws DeviceRequestException;
	
	/**
	 * Request the device build code.
	 * Based on header 192.
	 * @return device build code as String
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException
	 */
	String requestBuildCode() throws DeviceRequestException;
	
	/**
	 * Request the device encryption support. Based on header 111.
	 * @return device encryption support.
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException
	 */
	Object requestEncryptionSupport() throws DeviceRequestException;
	
	/**
	 * Perform a self check. Based on header 232.
	 * @return 
	 * @throws MessageParsingException 
	 * @throws MessagePortException 
	 */
	SelfCheckResponseWrapper performSelfCheck() throws DeviceRequestException;
	
	/**
	 * Send a request to the device. This method does not expected
	 * any response from the device, whether its an ACK or another type of response.
	 * If the device send an answer, it is ignored. To send a request expecting a response,
	 * use {@link #requestResponse(Message)}.
	 * @param m message to send
	 * @throws DeviceRequestException
	 */
	void request(Message m) throws DeviceRequestException;
	
	/**
	 * Send a request to the device, and wait for a response.
	 * To send a message which to does expect a response, use {@link #request(Message)}
	 * @param m message to send
	 * @return 
	 * @throws DeviceRequestException
	 */
	Message requestResponse(Message m) throws DeviceRequestException;
	
	/**
	 * Add a device configurator to this device. The configurator will be called
	 * upon device connection.
	 * @see #connect()
	 * @param c configurator to use
	 */
	void addConfigurator(DeviceConfigurator c);
	
	/**
	 * Remove all configurators
	 */
	void clearConfigurators();
	
	/**
	 * 
	 * @return a list containing all configurators currently affected to this device
	 */
	List<DeviceConfigurator> getConfigurators();
	
	/**
	 * 
	 * @return the <code>MessagePort</code> instance used by this device.
	 */
	MessagePort getMessagePort();
	
}
