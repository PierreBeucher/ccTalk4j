package com.github.pierrebeucher.cctalk4j.utils.message.builder;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.AbstractMessageBuilder;

public class AbstractMessageBuilderTest {

	@Test
	public void data() {
		AbstractMessageBuilder builder = new MyAbstractMessageBuilder();
		byte[] data = new byte[]{1, 2, 3};
		builder.data(data);
		Assert.assertEquals(builder.data, data);
	}

	@Test
	public void destination() {
		AbstractMessageBuilder builder = new MyAbstractMessageBuilder();
		Byte dest = 40;
		builder.destination(dest);
		Assert.assertEquals(builder.destination, dest);
	}

	@Test
	public void headerbyte() {
		AbstractMessageBuilder builder = new MyAbstractMessageBuilder();
		Byte header = Header.SIMPLE_POLL.getValue();
		builder.header(header);
		Assert.assertEquals(builder.header, header);
	}

	@Test
	public void headerHeader() {
		AbstractMessageBuilder builder = new MyAbstractMessageBuilder();
		Header header = Header.SIMPLE_POLL;
		builder.header(header);
		Assert.assertEquals(builder.header, (Byte)header.getValue());
	}

	@Test
	public void source() {
		AbstractMessageBuilder builder = new MyAbstractMessageBuilder();
		Byte source = 1;
		builder.source(source);
		Assert.assertEquals(builder.source, source);
	}
	
	private class MyAbstractMessageBuilder extends AbstractMessageBuilder{
		@Override
		protected Message doBuild() {
			return null;
		}
	}
}
