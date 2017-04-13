package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * <code>ResponseWrapper</code> for a Route Bill (Header 154) response. 
 * @author Pierre Beucher
 *
 */
public class RouteBillResponseWrapper extends ResponseWrapper {
	
	/**
	 * Error code indicating that escrow is empty.
	 */
	public static final byte ROUTE_CODE_ERROR_ESCROW_EMPTY = -2; //254 in unsgined byte
	
	/**
	 * Error code indicating a failure to route bill.
	 */
	public static final byte ROUTE_CODE_ERROR_FAILED_TO_ROUTE_BILL = -1; //255 in unsgined byte
	
	public static RouteBillResponseWrapper wrap(Message m) throws UnexpectedContentException{
		RouteBillResponseWrapper wp = new RouteBillResponseWrapper(m);
		wp.wrapContent();
		return wp;
	}
	
	private Byte errorCode = null;
	
	/*
	 * Whether to throw an exception if response length is not correct
	 * Some bill validator does not response with a response of length 0 or 1
	 * TODO allow user to confire this behavior... 
	 */
	private boolean ignoreIncorrectResponseLength=true;
	
	protected RouteBillResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

	@Override
	protected void wrapContent() throws UnexpectedContentException {
		//message data is either empty (ack) or a single error code		
		switch(message.getDataBytes().length){
			case 0:
				this.errorCode = null;
				break;
			case 1:
				this.errorCode = message.getDataBytes()[0];
				break;
			default:
				if(!ignoreIncorrectResponseLength){
					throw new UnexpectedContentException("Route bill response payload length must be 0 or 1, but is " + message.getDataBytes().length);
				}
		}
		
	}
	
	/**
	 * 
	 * @return true if wrapped message represents the escrow is empty error. 
	 */
	public boolean isErrorEscrowEmpty(){
		return isError() && errorCode == ROUTE_CODE_ERROR_ESCROW_EMPTY;
	}
	
	/**
	 * 
	 * @return true if wrapped message represents failed to route bill error.
	 */
	public boolean isErrorFailedToRouteBill(){
		return isError() && errorCode == ROUTE_CODE_ERROR_FAILED_TO_ROUTE_BILL;
	}
	
	/**
	 * 
	 * @return true if the wrapped message is an ACK, 
	 * i.e the message did not contain an error code byte.
	 */
	public boolean isAck(){
		return !isError();
	}
	
	/**
	 * 
	 * @return true if the wrapped message contain en error, i.e.
	 * the message is not an ACK and contained an error code byte.
	 */
	public boolean isError(){
		return errorCode != null;
	}

	/**
	 * @return the errorCode if the wrapped message contains an error code, or null.
	 */
	public Byte getErrorCode(){
		return this.errorCode;
	}
}
