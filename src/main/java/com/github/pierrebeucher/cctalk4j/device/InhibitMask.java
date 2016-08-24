package com.github.pierrebeucher.cctalk4j.device;

import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * <p><code>InhibitMask</code> represents an inhibit mask return by
 * used by Header 231 and 230, using an underlying BitSet.</p>
 * <p>The BitSet value is used to define the inhibit mask bytes. 
 * For example, a BitSet like [0,0,0,0,1,0,0,0,0,1,1,0,1,1,1,1] correspond
 * to an inhibit mask of: 
 * <ul>
 * <li>0110 1111</li>
 * <li>0000 1000</li>
 * </ul>
 * e.g. note 0, 1, 2, 3, 5, 6, 11 enabled.
 * </p>
 * @author Pierre Beucher
 *
 */
public class InhibitMask {

	private BitSet bitSet;
	
	/**
	 * @param bitSet
	 */
	public InhibitMask(BitSet bitSet) {
		super();		
		this.bitSet = bitSet;
	}
	
	/**
	 * Check whether the note a the given index is enabled.
	 * @param index index of the note
	 * @return true if enabled, false otherwise
	 */
	public boolean isEnabled(int index){
		return bitSet.get(index);
	}
	
	/**
	 * @return a byte array of fixed size 2
	 * representing this <code>InhibitMask</code>
	 */
	public byte[] bytes(){
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.put(bitSet.toByteArray());
		return buf.array();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("[");
		for(int i=0; i<15; i++){
			builder.append(i + ":" + bitSet.get(i)+",");
		}
		builder.append("16:" + bitSet.get(16) + "]");
		return builder.toString();
	}

	/**
	 * Compare this <code>InhibitMask</code> with another object.
	 * Both are equals if the given object is not null, an instance
	 * of InhibitMask, and the underlying BitSet are equals.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InhibitMask other = (InhibitMask) obj;
		if (bitSet == null) {
			if (other.bitSet != null)
				return false;
		} else if (!bitSet.equals(other.bitSet))
			return false;
		return true;
	}
}
