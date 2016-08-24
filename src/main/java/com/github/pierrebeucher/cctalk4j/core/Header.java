package com.github.pierrebeucher.cctalk4j.core;

/**
 * Set of ccTalk Headers.  
 * @author Pierre Beucher
 *
 */
public enum Header {
	
	SIMPLE_POLL(254),
	
	REQUEST_MANUFACTURER_ID(246),
	REQUEST_EQUIPMENT_CATEGORY_ID(245),
	REQUEST_PRODUCT_CODE(244),
	
	REQUEST_BUILD_CODE(192),
	
	REQUEST_ENCRYPTION_SUPPORT(111),
	
	READ_BUFFERED_BILL_EVENTS(159),
	
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
