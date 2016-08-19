package com.github.pierrebeucher.cctalk4j.core;

public class CRCChecksumMessageParser extends AbstractMessageParser
		implements MessageParser {

	public CRCChecksumMessageParser() {
		super();
	}
	
	public CRCChecksumMessageParser(byte[] bytes) {
		super(bytes);
	}
	
	/**
	 * 
	 * @return the CRC LSB from the byte representation of the parsed message
	 */
	public byte crcLsb(){
		return bytes[2];
	}
	
	/**
	 * 
	 * @return the CRC MSB from the byte representation of the parsed message
	 */
	public byte crcMsb(){
		return bytes[bytes.length-1];
	}

	@Override
	protected Message doParse() throws MessageParsingException{
		try {
			CRCChecksumMessage message = new CRCChecksumMessage(
					destinationByte(),
					headerByte(),
					dataBytes());
			
			//verify checksum
			if(message.getCrcLsb() != crcLsb() || message.getCrcMsb() != crcMsb()){
				throw new MessageParsingException("Invalid checksum: expected ["
						+ message.getCrcLsb() + ", " + message.getCrcMsb() + "], but"
						+ " parsed checksum is [" + crcLsb() + "," + crcMsb() + "].");
			}
			
			return message;
		} catch (IllegalArgumentException e) {
			throw new MessageParsingException(e);
		}
	}

	

}
