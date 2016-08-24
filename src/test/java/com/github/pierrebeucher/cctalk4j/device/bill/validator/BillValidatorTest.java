package com.github.pierrebeucher.cctalk4j.device.bill.validator;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

public class BillValidatorTest {

	private String portCom = "COM6";
	private byte address = 40;
	private BillValidator billValidator;
	
	@BeforeClass
	public void beforeClass() throws MessagePortException{
		billValidator = DeviceFactory.billValidatorSerialCRC(portCom, address);
		billValidator.connect();
	}
	
	@Test
	public void readBufferedNoteEvents_nominal() throws MessageIOException, UnexpectedContentException {
		BillEventBuffer eventBuf = billValidator.readBufferedNoteEvents();
		Assert.assertEquals(eventBuf.getBillEvents().length, 5);
	}
	
	@Test
	public void requestMasterInhibitStatus() throws MessageIOException, UnexpectedContentException{
		billValidator.modifyMasterInhibitStatus(true);
		boolean inhibit = billValidator.requestMasterInhibitStatus();
		Assert.assertEquals(inhibit, true);
	}
	
	@Test
	public void modifyMasterInhibitStatus_true() throws MessageIOException, UnexpectedContentException{
		billValidator.modifyMasterInhibitStatus(true);
		boolean inhibit = billValidator.requestMasterInhibitStatus();
		Assert.assertEquals(inhibit, true);
	}
	
	@Test
	public void modifyMasterInhibitStatus_false() throws MessageIOException, UnexpectedContentException{
		billValidator.modifyMasterInhibitStatus(false);
		boolean inhibit = billValidator.requestMasterInhibitStatus();
		Assert.assertEquals(inhibit, false);
	}
	
	@AfterClass
	public void afterClass() throws MessagePortException{
		billValidator.disconnect();
	}
}
