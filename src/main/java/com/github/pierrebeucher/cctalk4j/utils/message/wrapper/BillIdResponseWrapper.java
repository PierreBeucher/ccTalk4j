package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrapper</code> is used to wrap a Request Bill ID response (Header 157).
 * @author Pierre Beucher
 *
 */
public class BillIdResponseWrapper extends AsciiDataResponseWrapper {

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

}
