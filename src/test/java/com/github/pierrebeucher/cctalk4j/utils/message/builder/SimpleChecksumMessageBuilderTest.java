package com.github.pierrebeucher.cctalk4j.utils.message.builder;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.SimpleChecksumMessage;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.SimpleChecksumMessageBuilder;

public class SimpleChecksumMessageBuilderTest {

	private byte destination = 40;
	private byte source = 1;
	private byte[] data = new byte[]{1,2,3};
	private Header header = Header.SIMPLE_POLL;
	
	@Test
	public void build_nominal() throws MessageBuildException {
		Message expected = new SimpleChecksumMessage(destination, source, header.getValue(), data);
		Message actual = new SimpleChecksumMessageBuilder()
				.destination(destination)
				.header(header)
				.data(data)
				.source(source)
				.build();

		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void build_no_data() throws MessageBuildException {
		Message expected = new SimpleChecksumMessage(destination, source, header.getValue(), new byte[]{});
		Message actual = new SimpleChecksumMessageBuilder()
				.destination(destination)
				.header(header)
				.source(source)
				.build();

		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void build_err_no_source() throws MessageBuildException {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				new SimpleChecksumMessageBuilder()
						.destination(destination)
						.header(header)
						.data(data)
						.build();
			}
		};
		Assert.assertThrows(MessageBuildException.class, r);
	}
	
	@Test
	public void build_err_no_header() throws MessageBuildException {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				new SimpleChecksumMessageBuilder()
						.destination(destination)
						.source(source)
						.data(data)
						.build();
			}
		};
		Assert.assertThrows(MessageBuildException.class, r);
	}
	
	@Test
	public void build_err_no_dest() throws MessageBuildException {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				new SimpleChecksumMessageBuilder()
						.source(source)
						.data(data)
						.header(header)
						.build();
			}
		};
		Assert.assertThrows(MessageBuildException.class, r);
	}
}
