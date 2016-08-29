package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageFactory;

public class BillOperatingModeResponseWrapperTest {
	
	//message is represented as Bit 0 = Stacker, Bit 1 = Escrow
	//so value 3 (0000 0011) enables both
	private Message messageNoStackerNoEscrow = MessageFactory.messageCRCChecksum((byte) 40, Header.NONE, new byte[]{0});
	private Message messageStackerEscrow = MessageFactory.messageCRCChecksum((byte) 40, Header.NONE, new byte[]{3});
	private Message messageNoStackerEscrow = MessageFactory.messageCRCChecksum((byte) 40, Header.NONE, new byte[]{2});
	private Message messageStackerNoEscrow = MessageFactory.messageCRCChecksum((byte) 40, Header.NONE, new byte[]{1});
	
	private Message messageErrTooShort = MessageFactory.messageCRCChecksum((byte) 40, Header.NONE, new byte[]{});
	private Message messageErrTooLong = MessageFactory.messageCRCChecksum((byte) 40, Header.NONE, new byte[]{2,1});
	private Message messageErrTooMuchBits = MessageFactory.messageCRCChecksum((byte) 40, Header.NONE, new byte[]{4});
	
	@Test
	public void wrap_nominal() throws UnexpectedContentException {
		Assert.assertNotNull(BillOperatingModeResponseWrapper.wrap(messageNoStackerEscrow));
	}
	
	@Test
	public void wrap_err_too_short() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				BillOperatingModeResponseWrapper.wrap(messageErrTooShort);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void wrap_err_too_long() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				BillOperatingModeResponseWrapper.wrap(messageErrTooLong);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void wrap_err_not_boolean() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				BillOperatingModeResponseWrapper.wrap(messageErrTooMuchBits);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}

	@Test
	public void isStackerUsed_false() throws UnexpectedContentException {
		Assert.assertFalse(BillOperatingModeResponseWrapper.wrap(messageNoStackerNoEscrow).isStackerUsed());
	}
	
	@Test
	public void isStackerUsed_true() throws UnexpectedContentException {
		Assert.assertTrue(BillOperatingModeResponseWrapper.wrap(messageStackerNoEscrow).isStackerUsed());
	}
	
	@Test
	public void isEscrowUsed_true() throws UnexpectedContentException {
		Assert.assertTrue(BillOperatingModeResponseWrapper.wrap(messageStackerEscrow).isEscrowUsed());
	}
	
	@Test
	public void isEscrowUsed_false() throws UnexpectedContentException {
		Assert.assertFalse(BillOperatingModeResponseWrapper.wrap(messageStackerNoEscrow).isEscrowUsed());
	}	
}
