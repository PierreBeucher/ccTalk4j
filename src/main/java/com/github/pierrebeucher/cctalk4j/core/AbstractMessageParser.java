package com.github.pierrebeucher.cctalk4j.core;

import java.util.Arrays;

public abstract class AbstractMessageParser implements MessageParser {

	protected byte[] bytes;
	
	/**
	 * 
	 */
	public AbstractMessageParser() {
		super();
	}
	
	public AbstractMessageParser(byte[] bytes){
		setMessageBytes(bytes);
	}
	
	public void setMessageBytes(byte[] messageBytes) {
		this.bytes = messageBytes;
	}
	
	public byte[] getMessageBytes() {
		return bytes;
	}

	/**
	 * Check the message length is at least 5 (destination, data length,
	 * source or checksum, header and checksum). Does not check
	 * data length byte against data bytes (use {@link #dataBytes()} )
	 * @throws MessageParsingException if message length lesser than 5
	 */
	protected void checkMessageLength() throws MessageParsingException{
		if(bytes.length < 5){
			throw new MessageParsingException("Message length incorrect, must be at least 5.");
		}
	}
	
	public byte dataLengthByte() throws MessageParsingException{
		checkMessageLength();
		return bytes[1];
	}
	
	public byte[] dataBytes() throws MessageParsingException{
		checkMessageLength();
		int dataLength = dataLengthByte();
		
		if(bytes.length != 5 + dataLength){
			throw new MessageParsingException("Message data length byte"
					+ " does not match the actual message length. Expected "
					+ dataLength + " data byte(s) but found " + (bytes.length - 5) + ".");
		}
		
		return Arrays.copyOfRange(bytes, 4, 4+dataLength);
	}
	
	public byte destinationByte() throws MessageParsingException{
		checkMessageLength();
		return bytes[0];
	}
	
	public byte headerByte() throws MessageParsingException{
		checkMessageLength();
		return bytes[3];
	}
	
	protected abstract Message doParse() throws MessageParsingException;
	
	public Message parse() throws MessageParsingException {
		checkMessageLength();
		
		return doParse();
	}

}
