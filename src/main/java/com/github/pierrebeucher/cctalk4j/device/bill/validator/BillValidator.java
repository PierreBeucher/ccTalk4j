package com.github.pierrebeucher.cctalk4j.device.bill.validator;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.AbstractDevice;
import com.github.pierrebeucher.cctalk4j.device.Device;
import com.github.pierrebeucher.cctalk4j.device.InhibitMask;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.AckWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BillEventBufferResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BooleanResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.InhibitStatusResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

/**
 * <code>BillValidator</code> represents a Bill Validator device. 
 * @author Pierre Beucher
 *
 */
public class BillValidator extends AbstractDevice implements Device {
			
	public BillValidator(MessagePort port, Class<? extends MessageBuilder> messageBuilderClass,
			byte deviceAddress) {
		super(port, messageBuilderClass, deviceAddress);
	}
	
	public BillEventBuffer readBufferedNoteEvents() throws MessageIOException, UnexpectedContentException{
		Message response = requestResponse(Header.READ_BUFFERED_BILL_EVENTS);
		BillEventBufferResponseWrapper wrapper = BillEventBufferResponseWrapper.wrap(response);
		return new BillEventBuffer(wrapper.getBillEvents(), wrapper.getEventCounter());
	}
	
	/**
	 * Request the master inhibit status, based on Header 227.
	 * @return false if master inhibit is active, true if master normal operation
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public boolean requestMasterInhibitStatus() throws MessageIOException, UnexpectedContentException{
		Message response = requestResponse(Header.REQUEST_MASTER_INHIBIT_STATUS);
		return BooleanResponseWrapper.wrap(response).booleanValue();
	}
	
	/**
	 * Modify the master inhibit status using Header 228.
	 * @param inhibitStatus false for master inhibition active, true for normal operation
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public void modifyMasterInhibitStatus(boolean inhibitStatus) throws MessageIOException, UnexpectedContentException{
		Message response = requestResponse(
				Header.MODIFY_MASTER_INHIBIT_STATUS,
				new byte[]{Utils.boolToByte(inhibitStatus)});
		AckWrapper.wrap(response);
	}
	
	/**
	 * Request the inhibit status using Header 230. Result is returned as BitSet.
	 * @return
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public InhibitMask requestInhibitStatus() throws MessageIOException, UnexpectedContentException{
		Message response = requestResponse(Header.REQUEST_INHIBIT_STATUS);
		return new InhibitMask(InhibitStatusResponseWrapper.wrap(response).bitSet());
	}
	
	/**
	 * Modify the inhibit status using Header 231.
	 * @param mask inhibit masks to set
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	public void modifyInhibitStatus(InhibitMask mask) throws MessageIOException, UnexpectedContentException{
		Message response = requestResponse(Header.MODIFY_INHIBIT_STATUS, mask.bytes());
		AckWrapper.wrap(response);
	}
}
