package com.github.pierrebeucher.cctalk4j.device.bill.validator;

import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

import junit.framework.Assert;

public class BillValidatorTest {

	private String portCom = "COM6";
	private byte address = 40;
	
	@Test
	public void readBufferedNoteEvents_nominal() throws MessageIOException, UnexpectedContentException {
		BillValidator validator = DeviceFactory.billValidatorSerialCRC(portCom, address);
		try {
			validator.connect();
			BillEventBuffer eventBuf = validator.readBufferedNoteEvents();
			Assert.assertEquals(eventBuf.getBillEvents().length, 5);
		} finally {
			validator.disconnect();
		}
	}
}
