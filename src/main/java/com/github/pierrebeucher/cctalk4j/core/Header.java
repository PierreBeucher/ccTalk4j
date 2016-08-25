package com.github.pierrebeucher.cctalk4j.core;

/**
 * Set of ccTalk Headers.  
 * @author Pierre Beucher
 *
 */
public enum Header {
	
	//since the header set is not full
	//it is best to keep spaces between
	//discontinued header values
	SIMPLE_POLL(254),
	
	REQUEST_MANUFACTURER_ID(246),
	REQUEST_EQUIPMENT_CATEGORY_ID(245),
	REQUEST_PRODUCT_CODE(244),
	
	MODIFY_INHIBIT_STATUS(231),
	REQUEST_INHIBIT_STATUS(230),
	
	MODIFY_MASTER_INHIBIT_STATUS(228),
	REQUEST_MASTER_INHIBIT_STATUS(227),
	
	REQUEST_BUILD_CODE(192),
	
	READ_BUFFERED_BILL_EVENTS(159),
	MODIFY_BILL_ID(158),
	REQUEST_BILL_ID(157),
	
	REQUEST_ENCRYPTION_SUPPORT(111),
	
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
