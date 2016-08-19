package com.github.pierrebeucher.cctalk4j.core;

/**
 * Set of ccTalk Headers.  
 * @author Pierre Beucher
 *
 */
public enum Header {
	
	SIMPLE_POLL(254),
	NONE(0);
	
	private byte value;
	
	/**
	 * We use an integer for simplicity and readability purpose.
	 * @param i header number
	 */
	Header(int i){
		this.value = Utils.unsignedIntToByte(i);
	}
	
	public byte getValue(){
		return value;
	}
	
	public int getIntegerValue(){
		return Utils.byteToUnsignedInt(value);
	}
}
