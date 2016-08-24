package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;

public class AsciiDataResponseWrapperTest {

	private byte destination = 40;
	private Header header = Header.SIMPLE_POLL;
	private String asciiData = "This is Sparta!"; 
	private byte[] data = asciiData.getBytes();
	
	@Test
	public void wrap_nominal() throws MessageBuildException, UnexpectedContentException {
		Message message = new CRCChecksumMessageBuilder()
			.destination(destination)
			.header(header)
			.data(data)
			.build();
			
		String actual = AsciiDataResponseWrapper.wrap(message).getAsciiData();
		Assert.assertEquals(actual, asciiData);
	}
	
	@Test
	public void wrap_err_no_data() throws MessageBuildException, UnexpectedContentException {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				Message message = new CRCChecksumMessageBuilder()
						.destination(destination)
						.header(header)
						.build();
						
				AsciiDataResponseWrapper.wrap(message);
			}
		};
		
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
}
