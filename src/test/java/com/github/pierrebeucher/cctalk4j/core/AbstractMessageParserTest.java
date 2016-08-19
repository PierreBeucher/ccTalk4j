package com.github.pierrebeucher.cctalk4j.core;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.AbstractMessageParser;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageParsingException;
import com.github.pierrebeucher.cctalk4j.core.Utils;

public class AbstractMessageParserTest {
	
	//set notes enabled
	private byte[] goodMessage = Utils.unsignedIntsToBytes(new int[]{40,2,95,231,255,255,223});
	private byte[] badDataLength = Utils.unsignedIntsToBytes(new int[]{40,4,95,231,255,255,223});
	
	@Test
	public void checkMessageLength_nominal() throws MessageParsingException {
		new MyAbstractMessageParser(goodMessage).checkMessageLength();
	}
	
	@Test
	public void checkMessageLength_error() throws MessageParsingException {
		ThrowingRunnable r = new ThrowingRunnable(){
			public void run() throws MessageParsingException {
				new MyAbstractMessageParser(new byte[]{1,2,3}).checkMessageLength();
			}
		};
		Assert.assertThrows(MessageParsingException.class, r);
	}

	@Test
	public void dataBytes_nominal() throws MessageParsingException {
		byte[] expected = Utils.unsignedIntsToBytes(new int[]{255, 255});
		byte[] actual = new MyAbstractMessageParser(goodMessage).dataBytes();
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void dataBytes_error() throws MessageParsingException {
		ThrowingRunnable r = new ThrowingRunnable(){
			public void run() throws MessageParsingException {
				new MyAbstractMessageParser(badDataLength).dataBytes();
			}
		};
		Assert.assertThrows(MessageParsingException.class, r);
	}

	@Test
	public void dataLengthByte() throws MessageParsingException {
		byte actual = new MyAbstractMessageParser(goodMessage).dataLengthByte();
		byte expected = Utils.unsignedIntToByte(2);
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void destinationByte() throws MessageParsingException {
		byte actual = new MyAbstractMessageParser(goodMessage).destinationByte();
		byte expected = Utils.unsignedIntToByte(40);
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void headerByte() throws MessageParsingException {
		byte actual = new MyAbstractMessageParser(goodMessage).headerByte();
		byte expected = Utils.unsignedIntToByte(231);
		Assert.assertEquals(actual, expected);
	}
	
	private class MyAbstractMessageParser extends AbstractMessageParser{
		public MyAbstractMessageParser(byte[] bytes) {
			super(bytes);
		}
		@Override
		protected Message doParse() throws MessageParsingException {
			return null;
		}
	}

}
