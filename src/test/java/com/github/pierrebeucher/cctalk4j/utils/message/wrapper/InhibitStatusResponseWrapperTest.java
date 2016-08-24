package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import java.util.BitSet;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;

public class InhibitStatusResponseWrapperTest {

	@Test
	public void wrap_nominal() throws MessageBuildException, UnexpectedContentException {
		byte[] data = new byte[]{1,0};
		BitSet expected = BitSet.valueOf(data);
		Message m = new CRCChecksumMessageBuilder()
			.header(Header.NONE)
			.destination((byte)40)
			.data(data)
			.build();
		Assert.assertEquals(InhibitStatusResponseWrapper.wrap(m).bitSet(), expected);
	}
	
	@Test
	public void wrap_err_length_too_short() throws MessageBuildException {
		byte[] data = new byte[]{1};
		final Message m = new CRCChecksumMessageBuilder()
			.header(Header.NONE)
			.destination((byte)40)
			.data(data)
			.build();
		
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				InhibitStatusResponseWrapper.wrap(m);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
}
