package com.github.pierrebeucher.cctalk4j.core;

/**
 * <p>A <code>Message</code> is the representation of a ccTalk message as defined
 * in ccTalk specification <i>Message structure</i>. It is composed of a destination,
 * number of data bytes, header, checksum and data bytes.</p> 
 * @author Pierre Beucher
 *
 */
public interface Message {
	
	/**
	 * A byte array representing this message.
	 * @return
	 */
	public byte[] bytes();
	
	/**
	 * @return an Hexadecimal String representation of this message.
	 */
	public String getHexMessage();
	
	/**
	 * 
	 * @return header byte represented in this message
	 */
	public byte getHeader();
	
	/**
	 * 
	 * @return destination byte represented in this message
	 */
	public byte getDestination();
	
//	/**
//	 * Return the checksum byte(s) represented in this message. Depending
//	 * on the message structure, the returned array may contain 1 or 2 elements.
//	 * @return checkums byte(s) represented in this message.
//	 */
//	public byte[] getChecksum();
	
	/**
	 * 
	 * @return the data array contained in this message.
	 */
	public byte[] getDataBytes();

}
