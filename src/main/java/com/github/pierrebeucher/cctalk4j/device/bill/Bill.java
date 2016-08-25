package com.github.pierrebeucher.cctalk4j.device.bill;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Representation of a bill for Header 157 and 158 (Request / Modify bill ID),
 * with bill type and identification code (country, value, issue)
 * @author Pierre Beucher
 *
 */
public class Bill {

	/**
	 * Unprogrammed bill representation of 7 dots
	 */
	public static final String UNPROGRAMMED_BILL_IDENTIFICATION = ".......";
	
	/**
	 * Check whether a <code>Bill</code> is programmed or not.
	 * An unprogrammed bill is characterized by an identification
	 * string of 7 dots ("......."). 
	 * @param b bill to check
	 * @return true if given bll is programmed, false otherwise
	 */
	public static boolean isProgrammed(Bill b){
		return !UNPROGRAMMED_BILL_IDENTIFICATION.equals(b.rawIdentification());
	}
	
	private byte billType;
	private String countryCode;
	private String valueCode;
	private String issueCode;
	
	/**
	 * @param billType
	 * @param countryCode
	 * @param valueCode
	 * @param issueCode
	 */
	public Bill(byte billType, String countryCode, String valueCode, String issueCode) {
		super();
		this.billType = billType;
		this.countryCode = countryCode;
		this.valueCode = valueCode;
		this.issueCode = issueCode;
	}
	
	/**
	 * Generate a byte array representing this <code>Bill</code>,
	 * such as:
	 * <p><code>[ bill type ] [ char 1 ] [ char 2 ] [ char 3 ] ...</code></p>
	 * With billType the result of {@link #billType()} and
	 * the character set the result of {@link #rawIdentification()}
	 * converted into an ASCII byte array.
	 * @return byte array representing this <code>Bill</code>
	 */
	public byte[] toByteArray(){
		byte[] identificationBytes = rawIdentification().getBytes(StandardCharsets.US_ASCII);
		ByteBuffer buf = ByteBuffer.allocate(identificationBytes.length + 1);
		buf.put(billType);
		buf.put(identificationBytes);
		return buf.array();
	}
	
	/**
	 * 
	 * @return this <code>Bill</code> type
	 */
	public byte billType(){
		return this.billType;
	}
	
	/**
	 * 
	 * @return the country code of this <code>Bill</code>.
	 */
	public String countryCode(){
		return this.countryCode;
	}
	
	/**
	 * Return the value code letter of this <code>Bill</code>.
	 * Be aware the value code needs to be compared against the country
	 * scaling factor for the real bill value.
	 * @return the value code letter of this <code>Bill</code>.
	 */
	public String valueCode(){
		return this.valueCode;
	}
	
	/**
	 * 
	 * @return the issue code letter of this <code>Bill</code>.
	 */
	public String issueCode(){
		return this.issueCode;
	}
	
	/**
	 *  
	 * @return the standard CCVVVI identification code representing this bill.
	 */
	public String rawIdentification(){
		return countryCode + valueCode + issueCode;
	}

	/**
	 * <p>This <code>Bill</code> as String:</p>
	 * <p><code>{billType}:{rawIdentification}</code></p> 
	 * <p>like <code>1:XO0010A</code> for billType 1, Country Code XO,
	 * valueCode 0010 and issueCode A</p>
	 */
	@Override
	public String toString() {
		return billType + ":" + rawIdentification();
	}

	/**
	 * A <code>Bill</code> is equals to another Object if
	 * said Object is not null, an instance of <code>Bill</code>,
	 * and billType, countryCode, issueCode and valueCode
	 * are identical. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bill other = (Bill) obj;
		if (billType != other.billType)
			return false;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		if (issueCode == null) {
			if (other.issueCode != null)
				return false;
		} else if (!issueCode.equals(other.issueCode))
			return false;
		if (valueCode == null) {
			if (other.valueCode != null)
				return false;
		} else if (!valueCode.equals(other.valueCode))
			return false;
		return true;
	}
	
	
}
