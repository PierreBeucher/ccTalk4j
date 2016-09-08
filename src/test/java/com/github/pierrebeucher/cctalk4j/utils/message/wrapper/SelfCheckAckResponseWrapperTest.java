package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageFactory;

public class SelfCheckAckResponseWrapperTest {
	
	private Message messageNoFault = MessageFactory.messageCRCChecksum((byte)40, Header.NONE, new byte[]{0});
	
	private Message messageAck = MessageFactory.messageCRCChecksum((byte)40, Header.NONE);
	
	@Test
	public void wrap_nominal() throws UnexpectedContentException {
		Assert.assertNotNull(SelfCheckAckResponseWrapper.wrap(messageNoFault));
	}
	
	@Test
	public void wrap_ack() throws UnexpectedContentException {
		Assert.assertNotNull(SelfCheckAckResponseWrapper.wrap(messageAck));
	}
}
