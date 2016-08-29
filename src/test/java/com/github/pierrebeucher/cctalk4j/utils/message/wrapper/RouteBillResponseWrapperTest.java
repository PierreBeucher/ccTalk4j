package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageFactory;
import com.github.pierrebeucher.cctalk4j.core.Utils;

public class RouteBillResponseWrapperTest {

	private Byte errEscrowEmptyByte = Utils.unsignedIntToByte(254);
	private Byte errFailedToRouteBillByte = Utils.unsignedIntToByte(255);
	
	private Message messageAck = MessageFactory.messageCRCChecksum(
			(byte) 1, Header.NONE, new byte[]{});
	private Message messageEscrowEmpty = MessageFactory.messageCRCChecksum(
			(byte) 1, Header.NONE, new byte[]{errEscrowEmptyByte});
	private Message messageFailedToRouteBill = MessageFactory.messageCRCChecksum(
			(byte) 1, Header.NONE, new byte[]{errFailedToRouteBillByte});
	private Message messageErrTooLong = MessageFactory.messageCRCChecksum(
			(byte) 1, Header.NONE, new byte[]{errFailedToRouteBillByte, 0});
	
	
	@Test
	public void wrap_nominal_ack() throws UnexpectedContentException {
		Assert.assertNotNull(RouteBillResponseWrapper.wrap(messageAck));
	}
	
	@Test
	public void wrap_nominal_with_err_code() throws UnexpectedContentException {
		Assert.assertNotNull(RouteBillResponseWrapper.wrap(messageEscrowEmpty));
	}
	
	@Test
	public void wrap_nominal_err_too_long() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				RouteBillResponseWrapper.wrap(messageErrTooLong);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	public void getErrorCode_nominal() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageEscrowEmpty);
		Assert.assertEquals(wp.getErrorCode(), errEscrowEmptyByte);
	}
	
	public void getErrorCode_ack() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageAck);
		Assert.assertNull(wp.getErrorCode());
	}

	@Test
	public void isAck() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageAck);
		Assert.assertTrue(wp.isAck());
	}

	@Test
	public void isError_true_failedToRouteBill() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageFailedToRouteBill);
		Assert.assertTrue(wp.isError());
	}
	
	@Test
	public void isError_true_escrowEmpty() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageEscrowEmpty);
		Assert.assertTrue(wp.isError());
	}
	
	@Test
	public void isError_false_ack() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageAck);
		Assert.assertFalse(wp.isError());
	}
	
	@Test
	public void isFailedToRouteBill_true() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageFailedToRouteBill);
		Assert.assertTrue(wp.isErrorFailedToRouteBill());
	}
	
	@Test
	public void isFailedToRouteBill_false_ack() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageAck);
		Assert.assertFalse(wp.isErrorFailedToRouteBill());
	}
	
	@Test
	public void isFailedToRouteBill_false_escrow_empty() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageEscrowEmpty);
		Assert.assertFalse(wp.isErrorFailedToRouteBill());
	}
	
	@Test
	public void isErrorEscrowEmpty_true() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageEscrowEmpty);
		Assert.assertTrue(wp.isErrorEscrowEmpty());
	}
	
	@Test
	public void isErrorEscrowEmpty_false_ack() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageAck);
		Assert.assertFalse(wp.isErrorEscrowEmpty());
	}
	
	@Test
	public void isErrorEscrowEmpty_false_failed_to_route_bill() throws UnexpectedContentException {
		RouteBillResponseWrapper wp = RouteBillResponseWrapper.wrap(messageFailedToRouteBill);
		Assert.assertFalse(wp.isErrorEscrowEmpty());
	}

	
}
