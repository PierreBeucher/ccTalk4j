package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
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
	public void getAsciiData() throws MessageBuildException, UnexpectedContentException {
		Message message = new CRCChecksumMessageBuilder()
			.destination(destination)
			.header(header)
			.data(data)
			.build();
			
		String actual = new AsciiDataResponseWrapper(message).getAsciiData();
		Assert.assertEquals(actual, asciiData);
	}
}
