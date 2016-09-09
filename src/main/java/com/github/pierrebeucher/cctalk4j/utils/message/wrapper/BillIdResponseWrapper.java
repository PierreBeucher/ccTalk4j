package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrapper</code> is used to wrap a Request Bill ID response (Header 157).
 * @author Pierre Beucher
 *
 */
public class BillIdResponseWrapper extends AsciiDataResponseWrapper {

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
	public static boolean isProgrammed(BillIdResponseWrapper wp){
		return !UNPROGRAMMED_BILL_IDENTIFICATION.equals(wp.getCountryCode() + wp.getValueCode() + wp.getIssueCode());
	}
	
	private String countryCode;
	private String valueCode;
	private String issueCode;
	
	public static BillIdResponseWrapper wrap(Message m) throws UnexpectedContentException{
		BillIdResponseWrapper mw = new BillIdResponseWrapper(m);
		mw.wrapContent();
		return mw;
	}
	
	protected BillIdResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		super.wrapContent();
		if(message.getDataBytes().length < 7){
			throw new UnexpectedContentException("Bill id response is expected to have at least 7 characters.");
		}
		
		//calculated by super.wrapContent()
		String asciiData = getAsciiData();
		
		this.countryCode = asciiData.substring(0, 2);
		this.valueCode = asciiData.substring(2, 6);
		this.issueCode = asciiData.substring(6, 7);
	}

	/**
	 * @return wrapped message's country code
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * wrapped message's value code
	 * @return
	 */
	public String getValueCode() {
		return valueCode;
	}

	/**
	 * 
	 * @return wrapped message's issue code
	 */
	public String getIssueCode() {
		return issueCode;
	}

	@Override
	public String toString() {
		return "BillIdResponseWrapper [countryCode=" + countryCode + ", valueCode=" + valueCode + ", issueCode="
				+ issueCode + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BillIdResponseWrapper other = (BillIdResponseWrapper) obj;
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
