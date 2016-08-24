package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;

public class BooleanResponseWrapperTest {

	@Test
	public void booleanValue_true() throws MessageBuildException, UnexpectedContentException {
		Message m = new CRCChecksumMessageBuilder()
				.header(Header.NONE)
				.destination((byte)1)
				.data(new byte[]{1})
				.build();
		Assert.assertEquals(BooleanResponseWrapper.wrap(m).booleanValue(), true);
	}
	
	@Test
	public void booleanValue_false() throws MessageBuildException, UnexpectedContentException {
		Message m = new CRCChecksumMessageBuilder()
				.header(Header.NONE)
				.destination((byte)1)
				.data(new byte[]{0})
				.build();
		Assert.assertEquals(BooleanResponseWrapper.wrap(m).booleanValue(), false);
	}
	
	@Test
	public void wrap_err_data_length() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				Message m = new CRCChecksumMessageBuilder()
						.header(Header.NONE)
						.destination((byte)1)
						.data(new byte[]{0, 1})
						.build();
					BooleanResponseWrapper.wrap(m);
			}
		};
		
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void wrap_err_data_value() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				Message m = new CRCChecksumMessageBuilder()
						.header(Header.NONE)
						.destination((byte)1)
						.data(new byte[]{2})
						.build();
					BooleanResponseWrapper.wrap(m);
			}
		};
		
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
}
