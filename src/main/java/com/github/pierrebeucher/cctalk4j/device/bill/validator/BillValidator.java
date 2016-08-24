package com.github.pierrebeucher.cctalk4j.device.bill.validator;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.device.AbstractDevice;
import com.github.pierrebeucher.cctalk4j.device.Device;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BillEventBufferResponseWrapper;
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

}
