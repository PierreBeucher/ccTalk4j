package com.github.pierrebeucher.cctalk4j.core;

public interface MessageParser {

	/**
	 * Set the message bytes to parse.
	 * @param m message bytes to parse
	 */
	public void setMessageBytes(byte[] messageBytes);
	
	/**
	 * Parse the given bytes into a <code>Message</code>
	 * @param bytes bytes to parse
	 * @return parsed message
	 * @throws MessageParsingException if the message cannot be parsed
	 */
	public Message parse() throws MessageParsingException;
	
	/**
	 * Extract the header byte from the message.
	 * @return
	 */
	public byte headerByte() throws MessageParsingException;
	
	/**
	 * Extract the destination byte from the message.
	 * @return destination byte
	 */
	public byte destinationByte() throws MessageParsingException;
	
	public byte dataLengthByte() throws MessageParsingException;
	
	/**
	 * <p>Extract the data bytes from the parsed message. Data bytes
	 * are deduced from the data length byte.</p>
	 * <p>Examples: <br>
	 * For [1, 3, 40, header, 42, 43, 44, checksum] would return
	 * [42, 43, 44]. <br>
	 * For [1, 3, 40, header, 42, 43, 44, 45, checksum] would throw
	 * an exception as data length (3) does not match the message
	 * total length (it seems there is 4 data bytes instead of 3)</p>
	 * @return data bytes extracted
	 * @throws MessageParsingException if entire message length does not match the specified data length byte 
	 */
	public byte[] dataBytes() throws MessageParsingException;
}
