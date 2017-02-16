package com.github.pierrebeucher.cctalk4j.device.bill.validator;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.AbstractDevice;
import com.github.pierrebeucher.cctalk4j.device.Device;
import com.github.pierrebeucher.cctalk4j.device.DeviceRequestException;
import com.github.pierrebeucher.cctalk4j.device.InhibitMask;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.AckWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BillEventBufferResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BillIdResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BillOperatingModeResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BooleanResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.CountryScalingFactorWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.InhibitStatusResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.RouteBillResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

/**
 * <code>BillValidator</code> represents a Bill Validator device. 
 * @author Pierre Beucher
 *
 */
public class BillValidator extends AbstractDevice implements Device {
	
	/**
	 * Used by {@link #routeBill(byte)} to return a bill
	 */
	public static final byte ROUTE_CODE_RETURN_BILL = 0;
	
	/**
	 * Used by {@link #routeBill(byte)} to send a bill to the cashbox or stacker
	 */
	public static final byte ROUTE_CODE_SEND_BILL_CASHBOX_STACKER = 1;
	
	/**
	 * Used by {@link #routeBill(byte)} to extend escrow timeout
	 */
	public static final byte ROUTE_CODE_EXTEND_ESCROW_TIMEOUT = -1; //255 in unsigned byte
	
	public BillValidator(MessagePort port, Class<? extends MessageBuilder> messageBuilderClass,
			byte deviceAddress) {
		super(port, messageBuilderClass, deviceAddress);
	}
	
	public BillEventBuffer readBufferedNoteEvents() throws DeviceRequestException{
		Message response = requestResponse(Header.READ_BUFFERED_BILL_EVENTS);
		BillEventBufferResponseWrapper wrapper;
		try {
			wrapper = BillEventBufferResponseWrapper.wrap(response);
			return new BillEventBuffer(wrapper.getBillEvents(), wrapper.getEventCounter());
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Request the master inhibit status, based on Header 227.
	 * @return false if master inhibit is active, true if master normal operation
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public boolean requestMasterInhibitStatus() throws DeviceRequestException{
		Message response = requestResponse(Header.REQUEST_MASTER_INHIBIT_STATUS);
		try {
			return BooleanResponseWrapper.wrap(response).booleanValue();
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Modify the master inhibit status using Header 228.
	 * @param inhibitStatus false for master inhibition active, true for normal operation
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public void modifyMasterInhibitStatus(boolean inhibitStatus) throws DeviceRequestException{
		try{
			Message response = requestResponse(
				Header.MODIFY_MASTER_INHIBIT_STATUS,
				new byte[]{Utils.boolToByte(inhibitStatus)});
			AckWrapper.wrap(response);
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Request the inhibit status using Header 230. Result is returned as BitSet.
	 * @return
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public InhibitMask requestInhibitStatus() throws DeviceRequestException{
		Message response = requestResponse(Header.REQUEST_INHIBIT_STATUS);
		try {
			return new InhibitMask(InhibitStatusResponseWrapper.wrap(response).bitSet());
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Modify the inhibit status using Header 231.
	 * @param mask inhibit masks to set
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public void modifyInhibitStatus(InhibitMask mask) throws DeviceRequestException{
		Message response = requestResponse(Header.MODIFY_INHIBIT_STATUS, mask.bytes());
		try {
			AckWrapper.wrap(response);
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	/**
	 * Modify a bill ID using Header 158 and bill type identified in the given <code>Bill</code>
	 * @param identificationCode new identification code for the bill id
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public void modifyBillId(byte billType, String identificationCode) throws DeviceRequestException{
		byte[] bytes = Utils.concat(new byte[]{billType}, identificationCode.getBytes(StandardCharsets.US_ASCII));
		Message response = requestResponse(Header.MODIFY_BILL_ID, bytes);
		try {
			AckWrapper.wrap(response);
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Retrieve a bill id for the given bill type, using Header 157. 
	 * @param billType bill type to request
	 * @return identification of the given bill type as a <code>Bill</code>
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public BillIdResponseWrapper requestBillId(byte billType) throws DeviceRequestException{
		Message response = requestResponse(Header.REQUEST_BILL_ID, new byte[]{billType});
		try {
			return BillIdResponseWrapper.wrap(response);
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Retrieve the country scaling factor for the given country code. 
	 * @param countryCode country code for which to retrieve scaling factor
	 * @return obtained country scaling factor
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 * @throws IllegalArgumentException
	 */
	public CountryScalingFactorWrapper requestCountryScalingFactor(String countryCode)
				throws DeviceRequestException, IllegalArgumentException{
		if(countryCode.length() > 2){
			throw new IllegalArgumentException("Country code must not exceed 2 characters length.");
		}
		
		Message response = requestResponse(
				Header.REQUEST_COUNTRY_SCALING_FACTOR,
				countryCode.getBytes(StandardCharsets.US_ASCII));
		try {
			return CountryScalingFactorWrapper.wrap(response);
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Commands the routing of a bill held in escrow.
	 * Use {@link #ROUTE_CODE_EXTEND_ESCROW_TIMEOUT}, {@link #ROUTE_CODE_RETURN_BILL}
	 * or {@link #ROUTE_CODE_SEND_BILL_CASHBOX_STACKER}.  
	 * @param routeCode route code to apply
	 * @throws DeviceRequestException 
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException  
	 */
	public void routeBill(byte routeCode) throws BillRoutingException, DeviceRequestException {
		Message response;
		try {
			response = requestResponse(Header.ROUTE_BILL, routeCode);

			RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(response);
			if(wp.isError()){
				String errMsg = null;
				if(wp.isErrorEscrowEmpty()){
					errMsg = wp.getErrorCode() + ": escrow is empty.";
				} else if (wp.isErrorFailedToRouteBill()) {
					errMsg = wp.getErrorCode() + ": failed to route bill.";
				} else {
					errMsg = "Unknown error code: " + wp.getErrorCode();
				}
				throw new BillRoutingException(errMsg);
			}
		} catch (DeviceRequestException | UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Modify the bill operating mode using Header 153. 
	 * @param useStacker true to use stacker, false otherwise
	 * @param useEscrow true to use escrow, false otherwise
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public void modifyBillOperatingMode(boolean useStacker, boolean useEscrow) throws DeviceRequestException{
		// {useStacker, useEscrow}
		BitSet bitSet = new BitSet(2);
		bitSet.set(0, useEscrow);
		bitSet.set(1, useStacker);
		
		Message response = requestResponse(Header.MODIFY_BILL_OPERATING_MODE, bitSet.toByteArray());
		try {
			AckWrapper.wrap(response);
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
	
	/**
	 * Request the bill operating mod using Header 152.
	 * @return bill operating mode response 
	 * @throws UnexpectedContentException
	 * @throws MessageIOException
	 */
	public BillOperatingModeResponseWrapper requestBillOperatingMode() throws DeviceRequestException {
		Message response;
		try {
			response = requestResponse(Header.REQUEST_BILL_OPERATING_MODE);
			return BillOperatingModeResponseWrapper.wrap(response);
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}
}
