package com.github.pierrebeucher.cctalk4j.core;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.CRCChecksumMessage;
import com.github.pierrebeucher.cctalk4j.core.CRCChecksumMessageParser;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageParsingException;
import com.github.pierrebeucher.cctalk4j.core.Utils;

public class CRCChecksumMessageParserTest {

	//Manufacturer ID response
	private byte[] correctManIDResp = Utils.unsignedIntsToBytes(new int[]{1,3,114,0,65,83,84,192});	
	private byte[] badCrcLsbManIDResp = Utils.unsignedIntsToBytes(new int[]{1,3,114,0,63,83,84,192});
	private byte[] badCrcMsbManIDResp = Utils.unsignedIntsToBytes(new int[]{1,3,114,0,65,83,84,19});
	private byte[] badLengthManIDResp = Utils.unsignedIntsToBytes(new int[]{1,2,114,0,65,83,84,192});

	@Test
	public void crcLsb() {
		byte expected = Utils.unsignedIntToByte(114);
		byte actual = new CRCChecksumMessageParser(correctManIDResp).crcLsb();
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void crcMsb() {
		byte expected = Utils.unsignedIntToByte(192);
		byte actual = new CRCChecksumMessageParser(correctManIDResp).crcMsb();
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void parse_nominal() throws MessageParsingException {
		Message actual = new CRCChecksumMessageParser(correctManIDResp).parse();
		Message expected = new CRCChecksumMessage(
				Utils.unsignedIntToByte(1),
				Utils.unsignedIntToByte(0),
				Utils.unsignedIntsToBytes(new int[]{65, 83, 84}));
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void parse_error_crcLsb() throws MessageParsingException {
		ThrowingRunnable r = new ThrowingRunnable(){
			public void run() throws Throwable {
				new CRCChecksumMessageParser(badCrcLsbManIDResp).parse();
			}
		};
		Assert.assertThrows(MessageParsingException.class, r);
	}
	
	@Test
	public void parse_error_crcMsb() throws MessageParsingException {
		ThrowingRunnable r = new ThrowingRunnable(){
			public void run() throws Throwable {
				new CRCChecksumMessageParser(badCrcMsbManIDResp).parse();
			}
		};
		Assert.assertThrows(MessageParsingException.class, r);
	}
	
	@Test
	public void parse_error_dataLength() throws MessageParsingException {
		ThrowingRunnable r = new ThrowingRunnable(){
			public void run() throws Throwable {
				new CRCChecksumMessageParser(badLengthManIDResp).parse();
			}
		};
		Assert.assertThrows(MessageParsingException.class, r);
	}
}
