package com.github.pierrebeucher.cctalk4j.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.AbstractMessage;
import com.github.pierrebeucher.cctalk4j.core.Utils;

public class AbstractMessageTest {

	@Test
	public void equals_true() {
		AbstractMessage m1 = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{});
		AbstractMessage m2 = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{});
		Assert.assertEquals(m1, m2);
	}
	
	@Test
	public void equals_false_dest() {
		AbstractMessage m1 = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{});
		AbstractMessage m2 = new MyAbstractMessage(Utils.unsignedIntToByte(41),
				Utils.unsignedIntToByte(254),
				new byte[]{});
		Assert.assertNotEquals(m1, m2);
	}
	
	@Test
	public void equals_false_header() {
		AbstractMessage m1 = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(253),
				new byte[]{});
		AbstractMessage m2 = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{});
		Assert.assertNotEquals(m1, m2);
	}
	
	@Test
	public void equals_false_data() {
		AbstractMessage m1 = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{2, 1});
		AbstractMessage m2 = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{1, 2});
		Assert.assertNotEquals(m1, m2);
	}

	@Test
	public void getDataBytes() {
		AbstractMessage m = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{2, 1});
		Assert.assertEquals(m.getDataBytes(), new byte[]{2, 1});
	}

	@Test
	public void getDestination() {
		AbstractMessage m = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{2, 1});
		Assert.assertEquals(m.getDestination(), Utils.unsignedIntToByte(40));
	}

	@Test
	public void getHeader() {
		AbstractMessage m = new MyAbstractMessage(Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(254),
				new byte[]{2, 1});
		Assert.assertEquals(m.getHeader(), Utils.unsignedIntToByte(254));
	}
	
	private class MyAbstractMessage extends AbstractMessage{
		public MyAbstractMessage(byte destination, byte header, byte[] data) {
			super(destination, header, data);
		}
		
		public byte[] bytes() {
			return null;
		}

		public String getHexMessage() {
			return null;
		}
	}
}
