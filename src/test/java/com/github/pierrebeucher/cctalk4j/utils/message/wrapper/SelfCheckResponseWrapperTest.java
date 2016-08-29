package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageFactory;

public class SelfCheckResponseWrapperTest {

	private byte faultCode = 42;
	private byte extraInfo = 3;
	private Message messageNoFault = MessageFactory.messageCRCChecksum((byte)40, Header.NONE, new byte[]{0});
	private Message messageFaultNoExtraInfo = MessageFactory.messageCRCChecksum((byte)40, Header.NONE, new byte[]{faultCode});
	private Message messageFaultWithExtraInfo = MessageFactory.messageCRCChecksum((byte)40, Header.NONE, new byte[]{faultCode, extraInfo});
	
	private Message messageTooLong = MessageFactory.messageCRCChecksum((byte)40, Header.NONE, new byte[]{faultCode, extraInfo, 2});
	private Message messageTooShort = MessageFactory.messageCRCChecksum((byte)40, Header.NONE, new byte[]{});
	
	@Test
	public void wrap_nominal() throws UnexpectedContentException {
		Assert.assertNotNull(SelfCheckResponseWrapper.wrap(messageNoFault));
	}
	
	@Test(expectedExceptions={UnexpectedContentException.class})
	public void wrap_err_too_long() throws UnexpectedContentException {
		SelfCheckResponseWrapper.wrap(messageTooLong);
	}
	
	@Test(expectedExceptions={UnexpectedContentException.class})
	public void wrap_err_too_short() throws UnexpectedContentException {
		SelfCheckResponseWrapper.wrap(messageTooShort);
	}
	
	@Test
	public void faultCode_fault() throws UnexpectedContentException {
		SelfCheckResponseWrapper wp = SelfCheckResponseWrapper.wrap(messageFaultNoExtraInfo);
		Assert.assertEquals(wp.faultCode(), faultCode);
	}
	
	@Test
	public void faultCode_ok() throws UnexpectedContentException {
		SelfCheckResponseWrapper wp = SelfCheckResponseWrapper.wrap(messageNoFault);
		Assert.assertEquals(wp.faultCode(), SelfCheckResponseWrapper.NO_FAULT_DETECTED);
	}

	@Test
	public void hasFault_true_noExtraInfo() throws UnexpectedContentException {
		SelfCheckResponseWrapper wp = SelfCheckResponseWrapper.wrap(messageFaultNoExtraInfo);
		Assert.assertTrue(wp.hasFault());
	}
	
	@Test
	public void hasFault_true_withExtraInfo() throws UnexpectedContentException {
		SelfCheckResponseWrapper wp = SelfCheckResponseWrapper.wrap(messageFaultWithExtraInfo);
		Assert.assertTrue(wp.hasFault());
	}
	
	@Test
	public void hasFault_false() throws UnexpectedContentException {
		SelfCheckResponseWrapper wp = SelfCheckResponseWrapper.wrap(messageNoFault);
		Assert.assertFalse(wp.hasFault());
	}

	@Test
	public void optionalExtraInfo_none() throws UnexpectedContentException {
		SelfCheckResponseWrapper wp = SelfCheckResponseWrapper.wrap(messageFaultNoExtraInfo);
		Assert.assertNull(wp.optionalExtraInfo());
	}
	
	@Test
	public void optionalExtraInfo_some() throws UnexpectedContentException {
		SelfCheckResponseWrapper wp = SelfCheckResponseWrapper.wrap(messageFaultWithExtraInfo);
		Assert.assertEquals((byte)wp.optionalExtraInfo(), extraInfo);
	}
}
