package com.github.pierrebeucher.cctalk4j.core;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <i>Standard Message Packet, CRC checksum</i> message, as described
 * in the ccTalk specifications. Such messages uses the CRC-16 CCIT checksum, and contains:
 * <ul>
 * <li>Destination address</li>
 * <li>Number of data bytes</li>
 * <li>CRC-16 LSB (Least Significant Byte)</li>
 * <li>Header</li>
 * <li>Data 1</li>
 * <li>...</li>
 * <li>Data n</li>
 * <li>CRC-16 MSB (Most Significant Byte)</li>
 * </ul>
 * @author Pierre Beucher
 *
 */
public class CRCChecksumMessage extends AbstractMessage {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private byte[] checksum;
	
	/**
	 * <p>Create a new message using the given destination, header and data.
	 * The checksum and data length is deduced from the given parameter.<p>
	 * @param destination
	 * @param header
	 * @param data
	 * @throws IllegalArgumentException if data length exceed 255
	 */
	public CRCChecksumMessage(byte destination, byte header, byte[] data)
			throws IllegalArgumentException {
		super(destination, header, data);
		this.checksum = calculateChecksum();
	}
	
	public byte[] bytes() {
		ByteBuffer buf = ByteBuffer.allocate(5 + data.length);
		buf.put(destination);
		buf.put(dataLength);
		buf.put(getCrcLsb());
		buf.put(header);
		buf.put(data);
		buf.put(getCrcMsb());
		return buf.array();
	}
	
	/**
	 * Build the byte array used to calculate CRC 16 CCIT checksum,
	 * composed of: [header, destination, data_count, data_1, ..., data_n]
	 * @return bye array used to calculate this message CRC 16 CCIT
	 */
	private byte[] buildChecksumFeed(){
		//to checksum: header (1), data count (1), dest (1),  and data (n)
		byte[] feed = new byte[3 + data.length];
		feed[0] = destination;
		feed[1] = dataLength;
		feed[2] = header;
		for(int i=0; i<data.length; i++){
			feed[i+3] = data[i];
		}
		
		return feed;
	}

	private byte[] calculateChecksum(){
		byte[] checksumFeed = buildChecksumFeed();
		short checksumCrc16 = Utils.checksumCRC16CCIT(checksumFeed);
		
		logger.debug("Calculated 16bits CRC checksum for {} is {}", this, checksumCrc16);
		
		//CRC16 checksum composed of 16 bits (2 bytes)
		return ByteBuffer.allocate(2).putShort(checksumCrc16).array();
	}

	/**
	 * Return the CRC 16 CCIT calculated for this message.
	 * The returned array contains 2 elements, the first element
	 * being the Most Significant Byte (MSB), the second element
	 * the Least Significant Byte (LSB).
	 */
	public byte[] getChecksum() {
		return checksum;
	}
	
	/**
	 * Return this message's CRC least significant byte.
	 * For example, with CRC = [27, 42], will return 42.
	 * @return this message's CRC least significant byte.
	 */
	public byte getCrcLsb(){
		return checksum[1];
	}
	
	/**
	 * Return this message's CRC Most Significant Byte.
	 * For example, with CRC = [27, 42], will return 27.
	 * @return this message's CRC most significant byte.
	 */
	public byte getCrcMsb(){
		return checksum[0];
	}
	
	public String getHexMessage() {
		StringBuilder strBuilder = new StringBuilder(20 + 2 * data.length);
		strBuilder.append(Utils.byteToHex(destination));
		strBuilder.append(" ");
		strBuilder.append(Utils.byteToHex(dataLength));
		strBuilder.append(" ");
		strBuilder.append(Utils.byteToHex(getCrcLsb()));
		strBuilder.append(" ");
		strBuilder.append(Utils.byteToHex(header));
		if(data.length > 0){
			strBuilder.append(" ");
			strBuilder.append(Utils.bytesToHex(data));
		}
		strBuilder.append(" ");
		strBuilder.append(Utils.byteToHex(getCrcMsb()));
		return strBuilder.toString();
	}

	@Override
	public String toString() {
		return "CRCChecksumMessage [checksum=" + Arrays.toString(checksum) + ", destination=" + destination + ", header="
				+ header + ", data=" + Arrays.toString(data) + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CRCChecksumMessage))return false;
		CRCChecksumMessage m = (CRCChecksumMessage) other;
		return super.equals(m) &&
				Arrays.equals(m.checksum, this.checksum);
				
	}
	
	
}
