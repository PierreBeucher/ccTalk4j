package com.github.pierrebeucher.cctalk4j.core;

import java.util.Arrays;

public abstract class AbstractMessage implements Message {

	protected byte destination;
	protected byte header;
	protected byte dataLength;
	protected byte[] data;
	
	/**
	 * @param destination
	 * @param header
	 * @param dataByteCount
	 * @param data
	 */
	protected AbstractMessage(byte destination, byte header, byte[] data) {
		super();
		
		if(data.length > 255){
			throw new IllegalArgumentException("Data array length must not exceed 255");
		}
		
		this.destination = destination;
		this.header = header;
		this.data = data;
		this.dataLength = (byte)data.length; //binary representation safe as we ensured 0 < length < 256
	}

	public byte getDestination() {
		return destination;
	}

	public byte getHeader() {
		return header;
	}

	public byte[] getDataBytes() {
		return data;
	}

	@Override
	public boolean equals(Object other) {
		 if (other == null) return false;
		 if (other == this) return true;
		 if (!(other instanceof AbstractMessage))return false;
		 AbstractMessage m = (AbstractMessage)other;
		 return m.destination == this.destination &&
				 m.dataLength == this.dataLength &&
				 m.header == this.header &&
				 Arrays.equals(m.data, this.data);
	}

}
