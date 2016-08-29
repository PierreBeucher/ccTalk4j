package com.github.pierrebeucher.cctalk4j.core;

/**
 * <code>MessageFactory</code> contains static functions to create <code>Message</code> instances.
 * @author Pierre Beucher
 *
 */
public class MessageFactory {

	/**
	 * An empty data byte array, set to message with no data payload.
	 */
	public static final byte[] EMPTY_DATA_BYTES = new byte[]{};
	private MessageFactory(){
		
	}
	
	/**
	 * Create a new <code>CRCChecksumMessage</code> with no data payload. Data
	 * bytes defined is {@link #EMPTY_DATA_BYTES}.
	 * @param destination message's destination
	 * @param header message's header
	 * @return created message with given destinatination, header, and no data payload
	 */
	public static CRCChecksumMessage messageCRCChecksum(byte destination, Header header){
		return messageCRCChecksum(destination, header, EMPTY_DATA_BYTES);
	}
	
	/**
	 * Create a new <code>CRCChecksumMessage</code>.
	 * @param destination message's destination
	 * @param header message's header
	 * @param data message's data payload
	 * @return created message with given destinatination, header and data payload
	 */
	public static CRCChecksumMessage messageCRCChecksum(byte destination, Header header, byte[] data){
		return new CRCChecksumMessage(destination, header.getValue(), data);
	}
	
	/**
	 * Create a new <code>SimpleChecksumMessage</code> with no data payload. Data
	 * bytes defined is {@link #EMPTY_DATA_BYTES}.
	 * @param destination message's destination
	 * @param source message's source
	 * @param header message's header
	 * @return created message with given destination, source, header and empty data payload
	 */
	public static SimpleChecksumMessage messageSimpleChecksum(byte destination, byte source, Header header){
		return messageSimpleChecksum(destination, source, header, EMPTY_DATA_BYTES);
	}
	
	/**
	 *  Create a new <code>SimpleChecksumMessage</code>.
	 * @param destination message's destination
	 * @param source message's source
	 * @param header message's header
	 * @param data message's data payload
	 * @return created message with given destination, source, header and data payload
	 */
	public static SimpleChecksumMessage messageSimpleChecksum(byte destination, byte source, Header header, byte[] data){
		return new SimpleChecksumMessage(destination, source, header.getValue(), data);
	}
	
}
