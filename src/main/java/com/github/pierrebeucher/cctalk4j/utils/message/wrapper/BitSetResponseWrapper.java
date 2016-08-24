package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import java.util.BitSet;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrapper</code> used to wrap a response interpreted as a bit set.
 * @author Pierre Beucher
 *
 */
public class BitSetResponseWrapper extends ResponseWrapper {

	public static BitSetResponseWrapper wrap(Message m) throws UnexpectedContentException{
		BitSetResponseWrapper mw = new BitSetResponseWrapper(m);
		mw.wrapContent();
		return mw;
	}
	
	private BitSet bitSet;
	
	protected BitSetResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		this.bitSet = BitSet.valueOf(message.getDataBytes());
	}

	/**
	 * BitSet wrapping the message data payload.
	 * @return
	 */
	public BitSet bitSet(){
		return this.bitSet;
	}
}
