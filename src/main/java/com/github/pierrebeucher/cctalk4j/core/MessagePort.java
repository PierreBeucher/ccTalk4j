package com.github.pierrebeucher.cctalk4j.core;

/**
 * <code>MessagePort<code> is used to read and write ccTalk messages. 
 * @author Pierre Beucher
 *
 */
public interface MessagePort {

	public static final int MESSAGE_TYPE_STANDARD_CHECKSUM = 1;
	public static final int MESSAGE_TYPE_CRC16_CHECKSUM = 2;
	
	/**
	 * Open this message port for read/write operations.
	 * @throws MessagePortException 
	 */
	public void open() throws MessagePortException;
	
	/**
	 * Close the message port.
	 */
	public void close() throws MessagePortException;
	
	/**
	 * 
	 * @return true if this port is open.
	 */
	public boolean isOpen();
	
	/**
	 * 
	 * @return true if this port is closed.
	 */
	public boolean isClosed();
	
	/**
	 * Write a single message on this port.
	 * @param m message to write
	 * @throws MessagePortException
	 */
	public void write(Message m) throws MessagePortException;
	
	/**
	 * Try to read a single message from this port until a timeout occur.
	 * @param timeout time to wait for a message before timeout
	 * @return the read message
	 * @throws MessagePortException if no message is available, or an error occured reading
	 * the message
	 * @throws MessageParsingException if read message cannot be parsed (incorrect checksum, incorrect length...)
	 */
	public Message read(int timeout) throws MessagePortException, MessageParsingException;
	
}
