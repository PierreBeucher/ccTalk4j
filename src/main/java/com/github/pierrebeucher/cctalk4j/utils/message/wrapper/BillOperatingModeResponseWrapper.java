package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import java.util.BitSet;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrapper</code> for a bill operating mode request (Header 152).
 * @author Pierre Beucher
 *
 */
public class BillOperatingModeResponseWrapper extends ResponseWrapper {

	private boolean stackerUsed;
	private boolean escrowUsed;
	
	public static BillOperatingModeResponseWrapper wrap(Message m) throws UnexpectedContentException{
		BillOperatingModeResponseWrapper wp = new BillOperatingModeResponseWrapper(m);
		wp.wrapContent();
		return wp;
	}
	
	protected BillOperatingModeResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	/**
	 * Extract boolean value of the only data byte for
	 * stacker (bit 0) and escrow (bit 1). If other bits are
	 * set (not 0), throw an <code>UnexpectedContentException</code>
	 */
	@Override
	protected void wrapContent() throws UnexpectedContentException {
		if(message.getDataBytes().length != 1){
			throw new UnexpectedContentException("Expected 1 byte,"
					+ " but found " + message.getDataBytes().length + " bytes.");
		}
		
		//convert our bits into boolean values
		BitSet bitSet = BitSet.valueOf(message.getDataBytes());
		if(bitSet.length() > 2){
			throw new UnexpectedContentException("Mode control mask byte do not correspond "
					+ " to a bill operating mode: too much bits are set.");
		}
		
		this.stackerUsed = bitSet.get(0);
		this.escrowUsed = bitSet.get(1);
	}

	/**
	 * 
	 * @return true if stacked is used, false otherwise
	 */
	public boolean isStackerUsed() {
		return stackerUsed;
	}

	/**
	 * 
	 * @return true if escrow is used, false otherwise
	 */
	public boolean isEscrowUsed() {
		return escrowUsed;
	}
}
