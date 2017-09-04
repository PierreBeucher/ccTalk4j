package com.github.pierrebeucher.cctalk4j.device.bill.validator;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.util.BitSet;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.AfterClass;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageFactory;
import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.device.DeviceConfigurationException;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.device.DeviceRequestException;
import com.github.pierrebeucher.cctalk4j.device.InhibitMask;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BillIdResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BillOperatingModeResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.CountryScalingFactorWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

public class BillValidatorIT {

	/*
	 * Port used to communicate with the bill validator
	 */
	private String portCom;
	
	/*
	 * Address of the validator
	 */
	private byte address;
	
	/*
	 * instance of validator used for test (created by beforeClass)
	 */
	private BillValidator billValidator;
	
	/*
	 * bill type used by validator
	 */
	private String billType1Ascii;
	
	/*
	 * bill type used to check modify bill functionnality
	 */
	private String billUpdateAscii;
	
	/*
	 * unprogrammed bill type for validator
	 */
	private byte unprogrammedBillType;
	
	/*
	 * scaling factor for the country code used by validator
	 */
	private short scalingFactor;
	
	/*
	 * decimal place for the country code used by validator
	 */
	private byte decimalPlace;
	
	/*
	 * Escrow operating mode
	 */
	private boolean billOperatingModeEscrow;
	
	/*
	 * Stacker operating mode
	 */
	private boolean billOperatingModeStacker;
	
	@Parameters({"billValidator.comPort", "billValidator.address", "bill.type.1.rawId",
		"bill.toUpdate.rawId", "bill.unprogrammed.type", "bill.scalingFactor",
		"bill.decimalPlace", "billValidator.billOperatingModeEscrow", "billValidator.billOperatingModeStacker"})
	@BeforeClass
	public void beforeClass(String portCom, byte address, String billType1Ascii,
			String billUpdateAscii, byte unprogrammedBillType, short scalingFactor,
			byte decimalPlace, boolean billOperatingModeEscrow, boolean billOperatingModeStacker) throws MessagePortException, DeviceConfigurationException {
		this.portCom = portCom;
		this.address = address;
		this.billType1Ascii = billType1Ascii;
		this.billUpdateAscii = billUpdateAscii;
		this.unprogrammedBillType = unprogrammedBillType;
		this.scalingFactor = scalingFactor;
		this.decimalPlace = decimalPlace;
		this.billOperatingModeEscrow = billOperatingModeEscrow;
		this.billOperatingModeStacker = billOperatingModeStacker;
		
		this.billValidator = DeviceFactory.billValidatorSerialCRC(this.portCom, this.address);
		this.billValidator.connect();
	}
	
	@Test
	public void readBufferedNoteEvents_nominal() throws DeviceRequestException {
		BillEventBuffer eventBuf = billValidator.readBufferedNoteEvents();
		Assert.assertEquals(eventBuf.getBillEvents().length, 5);
	}
	
	@Test
	public void requestMasterInhibitStatus() throws DeviceRequestException{
		billValidator.modifyMasterInhibitStatus(true);
		boolean inhibit = billValidator.requestMasterInhibitStatus();
		Assert.assertEquals(inhibit, true);
	}
	
	@Test
	public void modifyMasterInhibitStatus_true() throws DeviceRequestException{
		billValidator.modifyMasterInhibitStatus(true);
		boolean inhibit = billValidator.requestMasterInhibitStatus();
		Assert.assertEquals(inhibit, true);
	}
	
	@Test
	public void modifyMasterInhibitStatus_false() throws DeviceRequestException{
		billValidator.modifyMasterInhibitStatus(false);
		boolean inhibit = billValidator.requestMasterInhibitStatus();
		Assert.assertEquals(inhibit, false);
	}
	
	@Test
	public void requestInhibitStatus() throws DeviceRequestException{
		byte inhibitMask1 = Byte.parseByte("00001010", 2); 
		byte inhibitMask2 = Byte.parseByte("00000000", 2);
		InhibitMask expected = new InhibitMask(BitSet.valueOf(new byte[]{inhibitMask1, inhibitMask2}));
		billValidator.modifyInhibitStatus(expected);
		InhibitMask actual = billValidator.requestInhibitStatus();
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void modifyInhibitStatus() throws DeviceRequestException{
		byte inhibitMask1 = Byte.parseByte("00001110", 2); 
		byte inhibitMask2 = Byte.parseByte("00000000", 2);
		InhibitMask expected = new InhibitMask(BitSet.valueOf(new byte[]{inhibitMask1, inhibitMask2}));
		billValidator.modifyInhibitStatus(expected);
		InhibitMask actual = billValidator.requestInhibitStatus();
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void requestBillId_1() throws DeviceRequestException, UnexpectedContentException{
		Message expectedMessage = MessageFactory.messageCRCChecksum((byte)1, Header.NONE, billType1Ascii.getBytes());
		BillIdResponseWrapper expected = BillIdResponseWrapper.wrap(expectedMessage);
		BillIdResponseWrapper actual = billValidator.requestBillId((byte) 1);
		Assert.assertEquals(actual, expected);
	}
	
	/**
	 * This test is only perform on a unprogrammed bill to ensure our device
	 * is not disrupted.
	 * @throws MessageIOException
	 * @throws UnexpectedContentException
	 */
	@Test
	public void modifyBillId() throws DeviceRequestException{
		BillIdResponseWrapper billBefore = billValidator.requestBillId(unprogrammedBillType);
		if(BillIdResponseWrapper.isProgrammed(billBefore)){
			throw new RuntimeException("Cannot test modifyBillId() on programmed bill " +
					billBefore + ", use a non programmed bill for the test.");
		}
		
		byte modifiedBillType = unprogrammedBillType;
		billValidator.modifyBillId(modifiedBillType, billUpdateAscii);
		BillIdResponseWrapper billAfter = billValidator.requestBillId(modifiedBillType);
		
		//replace back unprogrammed bill before assert
		try{
			billValidator.modifyBillId(modifiedBillType, billBefore.getAsciiData());
		} catch (Exception e){
			throw new RuntimeException("Cannot replace an unprogrammed bill for type " +
					unprogrammedBillType + ", caused by:" + e + " - IMPORTANT: " +
					" REDEFINE AN UNPROGRAMMED BILL FOR SAID TYPE.", e);
		}
		
		Assert.assertEquals(billUpdateAscii, billAfter.getAsciiData());
	}
	
	@Test
	public void requestCountryScalingFactor_scalingFactor() throws DeviceRequestException{
		CountryScalingFactorWrapper wp = billValidator.requestCountryScalingFactor("XO");
		Assert.assertEquals(wp.getScalingFactor(), this.scalingFactor);
	}
	
	@Test
	public void requestCountryScalingFactor_decimalPlace() throws DeviceRequestException{
		CountryScalingFactorWrapper wp = billValidator.requestCountryScalingFactor("XO");
		Assert.assertEquals(wp.getDecimalPlace(), this.decimalPlace);
	}
	
	//disabled for now, our validator seems to have issue with the route bill command
	//the command does route the bill, but return value is erratic
	//@Test
	public void routeBill() throws DeviceRequestException, InterruptedException{
		//simulate bill acceptance
		billValidator.modifyMasterInhibitStatus(true);
		billValidator.modifyInhibitStatus(new InhibitMask(BitSet.valueOf(new byte[]{-1, -1})));
		
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				billValidator.routeBill(BillValidator.ROUTE_CODE_SEND_BILL_CASHBOX_STACKER);
			}
		};
		Assert.assertThrows(BillRoutingException.class, r);
	}
	
	//@Test
	//well, our bill validator does not seem to be able to change operating mode
	//disabled for now until we contact find out why
	//TODO 
	public void modifyBillOperatingMode() throws DeviceRequestException{		
		boolean escrowAfter = true;
		boolean stackerAfter = false;
		billValidator.modifyBillOperatingMode(escrowAfter, stackerAfter);
		
		try {
			BillOperatingModeResponseWrapper responseAfter = billValidator.requestBillOperatingMode();
			Assert.assertEquals(responseAfter.isEscrowUsed(), escrowAfter);
			Assert.assertEquals(responseAfter.isStackerUsed(), stackerAfter);
		} finally {
			//reset value
			try {
				billValidator.modifyBillOperatingMode(billOperatingModeStacker, billOperatingModeEscrow);
			} catch (Exception e) {
				throw new RuntimeException("Cannot reset bill operating mode: " + e.getMessage(), e);
			}
		}	 	
	}
	
	@Test
	public void requestBillOperatingMode() throws UnexpectedContentException, MessageIOException, DeviceRequestException{
		BillOperatingModeResponseWrapper response = billValidator.requestBillOperatingMode();
		Assert.assertEquals(response.isEscrowUsed(), billOperatingModeEscrow);
		Assert.assertEquals(response.isStackerUsed(), billOperatingModeStacker);
	}
	
	@AfterClass
	public void afterClass() throws MessagePortException{
		billValidator.disconnect();
	}
}
