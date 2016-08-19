package com.github.pierrebeucher.cctalk4j.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.CRCChecksumMessage;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.Utils;

public class CRCChecksumMessageTest {

	//private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void getChecksum() {
		// [40, 0, CRC_LSB, 1, CRC_MSB]
		// expect CRC 3F 46 (63 70)
		CRCChecksumMessage m = new CRCChecksumMessage(
				(byte)40,
				(byte)1,
				new byte[]{});
		
		byte[] checksumBytes = m.getChecksum();
		String checksumHex = Utils.bytesToHex(checksumBytes);
		
		Assert.assertEquals("3F46", checksumHex);
	}

	@Test
	public void getRawMessage() {
		byte[] byteMsg = new byte[]{40, 0, 70, 1, 63};
		Message m = new CRCChecksumMessage(
				(byte)40,
				(byte)1,
				new byte[]{});
		byte[] rawMsg = m.bytes();
		
		Assert.assertEquals(rawMsg, byteMsg);
	}
	
	@Test
	public void getCrcLsb(){
		CRCChecksumMessage m = new CRCChecksumMessage(
				(byte)40,
				(byte)1,
				new byte[]{});
		Assert.assertEquals(70, m.getCrcLsb());
	}
	
	@Test
	public void getCrcMsb(){
		CRCChecksumMessage m = new CRCChecksumMessage(
				(byte)40,
				(byte)1,
				new byte[]{});
		Assert.assertEquals(63, m.getCrcMsb());
	}
	
	@Test
	public void equals_true(){
		CRCChecksumMessage m1 = new CRCChecksumMessage(
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(1),
				new byte[]{});
		CRCChecksumMessage m2 = new CRCChecksumMessage(
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(1),
				new byte[]{});
		Assert.assertEquals(m1, m2);
	}
	
	@Test
	public void equals_false(){
		CRCChecksumMessage m1 = new CRCChecksumMessage(
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(1),
				new byte[]{1,2});
		CRCChecksumMessage m2 = new CRCChecksumMessage(
				Utils.unsignedIntToByte(40),
				Utils.unsignedIntToByte(1),
				new byte[]{});
		Assert.assertNotEquals(m1, m2);
	}
}
