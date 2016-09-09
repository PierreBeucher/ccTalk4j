package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import java.nio.ByteBuffer;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrapper</code> used to wrap a Country Scaling Factor response
 * (Header 156). 
 * @author Pierre Beucher
 *
 */
public class CountryScalingFactorWrapper extends ResponseWrapper {

	/**
	 * Transform the scaling factor lsb and msb into a single short.
	 * This take both bytes (16 bits) and puts it into a short (16 bits as well) 
	 * @return the scalingFactor as short value
	 */
	public static int produceScalingFactorInt(byte sfMsb, byte sfLsb){
		return ByteBuffer.wrap(new byte[]{0, 0, sfMsb, sfLsb}).getInt();
	}
	
	/*
	 * Scaling factor as int and not short, event though 2 bytes may be represented as short:
	 * Java short is unsigned, we may end-up with negative value for real scaling factor > 32767
	 * by using a short instead of int. 
	 */
	private int scalingFactor;
	private byte decimalPlace;
	
	public static CountryScalingFactorWrapper wrap(Message m) throws UnexpectedContentException{
		CountryScalingFactorWrapper mw = new CountryScalingFactorWrapper(m);
		mw.wrapContent();
		return mw;
	}
	
	protected CountryScalingFactorWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		if(message.getDataBytes().length != 3){
			throw new UnexpectedContentException("Message data length must be equals to 3 (scaling factor LSB / MSB and decimal place).");
		}
		
		byte scalingFactorLsb = message.getDataBytes()[0];
		byte scalingFactorMsb = message.getDataBytes()[1];
		this.scalingFactor = produceScalingFactorInt(scalingFactorMsb, scalingFactorLsb);
		this.decimalPlace = message.getDataBytes()[2];
	}
	
	/**
	 * @return decimal place byte
	 */
	public byte getDecimalPlace() {
		return decimalPlace;
	}

	/**
	 * 
	 * @return the decimal factor
	 */
	public int getScalingFactor() {
		return scalingFactor;
	}
	
	@Override
	public String toString() {
		return "[scalingFactor=" + scalingFactor + ", decimalPlace=" + decimalPlace + "]";
	}
}