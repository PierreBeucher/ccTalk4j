package com.github.pierrebeucher.cctalk4j.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.SimpleChecksumMessage;
import com.github.pierrebeucher.cctalk4j.core.Utils;

public class SimpleChecksumMessageTest {

	//private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void getChecksum_1() {
		//expected a checksum of 253
		//-3 is ‭1111 1101‬ in JVM (253 considering unsigned byte)
		byte expectedChecksum = -3; 
		SimpleChecksumMessage m = new SimpleChecksumMessage((byte)1, (byte)2, (byte)0, new byte[]{});
		
		Assert.assertEquals(m.getChecksum()[0], expectedChecksum);
	}
	
	@Test
	public void getChecksum_2() {
		byte expectedChecksum = 56; 
		SimpleChecksumMessage m = new SimpleChecksumMessage((byte)40, (byte)1, (byte)159, new byte[]{});
		
		Assert.assertEquals(m.getChecksum()[0], expectedChecksum);
	}
	
	@Test
	public void getChecksum_3() {
		byte expectedChecksum = 8; 
		SimpleChecksumMessage m = new SimpleChecksumMessage((byte)2, (byte)1, (byte)245, new byte[]{});
		
		Assert.assertEquals(m.getChecksum()[0], expectedChecksum);
	}

	@Test
	public void getRawMessage() {
		byte[] expectedRaw = new byte[]{1, 0, 40, 127, 88};
		SimpleChecksumMessage m = new SimpleChecksumMessage((byte)1, (byte)40, (byte)127, new byte[]{});
		
		Assert.assertEquals(m.bytes(), expectedRaw);
	}
	
	@Test
	public void equals_true(){
		SimpleChecksumMessage m1 = new SimpleChecksumMessage(
				Utils.unsignedIntToByte(1),
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(127),
				new byte[]{});
		SimpleChecksumMessage m2 = new SimpleChecksumMessage(
				Utils.unsignedIntToByte(1),
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(127),
				new byte[]{});
		Assert.assertEquals(m1, m2);
	}
	
	@Test
	public void equals_false_source(){
		SimpleChecksumMessage m1 = new SimpleChecksumMessage(
				Utils.unsignedIntToByte(1),
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(127),
				new byte[]{});
		SimpleChecksumMessage m2 = new SimpleChecksumMessage(
				Utils.unsignedIntToByte(1),
				Utils.unsignedIntToByte(42),
				Utils.unsignedIntToByte(127),
				new byte[]{});
		Assert.assertNotEquals(m1, m2);
	}
	
	@Test
	public void equals_false_data(){
		SimpleChecksumMessage m1 = new SimpleChecksumMessage(
				Utils.unsignedIntToByte(1),
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(127),
				new byte[]{});
		SimpleChecksumMessage m2 = new SimpleChecksumMessage(
				Utils.unsignedIntToByte(1),
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(127),
				new byte[]{1,2});
		Assert.assertNotEquals(m1, m2);
	}
}
