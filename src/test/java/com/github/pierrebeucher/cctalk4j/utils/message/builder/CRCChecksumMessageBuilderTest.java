package com.github.pierrebeucher.cctalk4j.utils.message.builder;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.CRCChecksumMessage;
import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;

public class CRCChecksumMessageBuilderTest {
	
	private Byte destination = 40;
	private byte[] data = new byte[]{1,2,3};
	private Header header = Header.SIMPLE_POLL;
	
	@Test
	public void build_nominal() throws MessageBuildException {
		Message expected = new CRCChecksumMessage(destination, header.getValue(), data);
		Message actual = new CRCChecksumMessageBuilder()
			.destination(destination)
			.header(header)
			.data(data)
			.build();
		
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void build_no_data() throws MessageBuildException {
		Message expected = new CRCChecksumMessage(destination, header.getValue(), new byte[]{});
		Message actual = new CRCChecksumMessageBuilder()
			.destination(destination)
			.header(header)
			.build();
		
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void build_err_no_header() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				new CRCChecksumMessageBuilder()
						.destination(destination)
						.data(data)
						.build();
			}
		};
		Assert.assertThrows(MessageBuildException.class, r);
	}
	
	@Test
	public void build_err_no_dest() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				new CRCChecksumMessageBuilder()
						.header(header)
						.data(data)
						.build();
			}
		};
		Assert.assertThrows(MessageBuildException.class, r);
	}
}
