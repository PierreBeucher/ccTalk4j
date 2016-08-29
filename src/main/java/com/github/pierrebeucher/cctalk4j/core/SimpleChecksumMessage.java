package com.github.pierrebeucher.cctalk4j.core;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>A <i>Standard Message Packets, Simple checksum</i> message, as described
 * in the ccTalk specifications. Such messages uses a standard checksum and contains:
 * <ul>
 * <li>Destination address</li>
 * <li>Number of data bytes</li>
 * <li>Source Address</li>
 * <li>Header</li>
 * <li>Data 1</li>
 * <li>...</li>
 * <li>Data n</li>
 * <li>Checksum</li>
 * </ul>
 * The cheskum being defined as <i>a simple zero-sum checksum such
 * that the 8-bit addition ( modulo 256 ) of all the bytes in the message
 * from the start to the checksum itself is zero"</i></p>
 * 
 * @author Pierre Beucher
 *
 */
public class SimpleChecksumMessage extends AbstractMessage {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private byte source;
	private byte checksum; 
	
	public SimpleChecksumMessage(byte destination, byte source, byte header, byte[] data) {
		super(destination, header, data);
		this.source = source;
		this.checksum = calculateChecksum();
	}
	
	private byte calculateChecksum(){
		//use an int to perform the 8bit addition of the message
		//this int least significant bits represent our addition 
		int intChecksum = Utils.byteToUnsignedInt(source) + Utils.byteToUnsignedInt(destination)
			+ Utils.byteToUnsignedInt(header) + data.length;
		for(byte b : data){
			intChecksum = intChecksum + Utils.byteToUnsignedInt(b);
		}
		
		// extract the least significant bits
		ByteBuffer buf = ByteBuffer.allocate(4).putInt(intChecksum);
		byte byteChecksum = buf.get(3);
		byte finalChecksum = (byte)(256 - Utils.byteToUnsignedInt(byteChecksum));
		
		logger.debug("Checksum for {}: {}", this, finalChecksum);
		
		return finalChecksum;
	}

	public byte[] bytes() {
		ByteBuffer buf = ByteBuffer.allocate(5 + data.length);
		buf.put(destination);
		buf.put(dataLength);
		buf.put(source);
		buf.put(header);
		buf.put(data);
		buf.put(checksum);
		return buf.array();
	}

	public byte[] getChecksum() {
		return new byte[]{ checksum };
	}

	public byte getSource() {
		return source;
	}

	public String getHexMessage() {
		StringBuilder strBuilder = new StringBuilder(20 + 2 * data.length);
		strBuilder.append(Utils.byteToHex(destination));
		strBuilder.append(Utils.byteToHex(dataLength));
		strBuilder.append(Utils.byteToHex(source));
		strBuilder.append(Utils.byteToHex(header));
		strBuilder.append(Utils.bytesToHex(data));
		strBuilder.append(Utils.byteToHex(checksum));
		return strBuilder.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SimpleChecksumMessage))return false;
		SimpleChecksumMessage m = (SimpleChecksumMessage) other;
		return super.equals(m) &&
				m.checksum == this.checksum &&
				m.source == this.source;
				
	}

}
