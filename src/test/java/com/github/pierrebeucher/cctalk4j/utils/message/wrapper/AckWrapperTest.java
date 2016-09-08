package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;

public class AckWrapperTest {
	
	private Message buildMessage(Header h, byte dest, byte[] data) throws MessageBuildException{
		return new CRCChecksumMessageBuilder()
			.header(h)
			.data(data)
			.destination(dest)
			.build();
	}
	
	@Test
	public void wrap_nominal() throws UnexpectedContentException, MessageBuildException {
		Message msg = buildMessage(Header.NONE, Utils.unsignedIntToByte(1), new byte[]{});
		AckWrapper wrapper = AckWrapper.wrap(msg);
		Assert.assertEquals(wrapper.getWrappedMessage(), msg);
	}
	
	@Test
	public void wrap_nonack_header() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				Message msg = buildMessage(Header.SIMPLE_POLL, Utils.unsignedIntToByte(40), new byte[]{});
				AckWrapper.wrap(msg);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void wrap_nonack_data() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				Message msg = buildMessage(Header.NONE, Utils.unsignedIntToByte(40), new byte[]{1, 2});
				AckWrapper.wrap(msg);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void destination() throws UnexpectedContentException, MessageBuildException {
		Message msg = buildMessage(Header.NONE, Utils.unsignedIntToByte(1), new byte[]{});
		AckWrapper wrapper = AckWrapper.wrap(msg);
		Assert.assertEquals(wrapper.destination(), 1);
	}
	
	@Test
	public void isAck_true() throws MessageBuildException, IllegalArgumentException{
		Message msg = buildMessage(Header.NONE, Utils.unsignedIntToByte(1), new byte[]{});
		Assert.assertEquals(AckWrapper.isAck(msg), true);
	}
	
	@Test
	public void isAck_false_header() throws MessageBuildException, IllegalArgumentException{
		Message msg = buildMessage(Header.MODIFY_BILL_ID, Utils.unsignedIntToByte(1), new byte[]{});
		Assert.assertEquals(AckWrapper.isAck(msg), false);
	}
	
	@Test
	public void isAck_false_data() throws MessageBuildException, IllegalArgumentException{
		Message msg = buildMessage(Header.NONE, Utils.unsignedIntToByte(1), new byte[]{42});
		Assert.assertEquals(AckWrapper.isAck(msg), false);
	}
}
