package com.github.pierrebeucher.cctalk4j.core;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MessageFactoryTest {

	private byte destination = 40;
	private byte source = 1;
	private Header header = Header.MODIFY_BILL_ID; 
	private byte[] someData = new byte[]{1, 42, 0};
	
	@Test
	public void messageCRCChecksumbyteHeader() {
		Message m = MessageFactory.messageCRCChecksum(destination, header);
		Assert.assertEquals(m.getDestination(), destination);
		Assert.assertEquals(m.getHeader(), header.getValue());
		Assert.assertEquals(m.getDataBytes(), MessageFactory.EMPTY_DATA_BYTES);
	}

	@Test
	public void messageCRCChecksumbyteHeaderbyte() {
		Message m = MessageFactory.messageCRCChecksum(destination, header, someData);
		Assert.assertEquals(m.getDestination(), destination);
		Assert.assertEquals(m.getHeader(), header.getValue());
		Assert.assertEquals(m.getDataBytes(), someData);
	}

	@Test
	public void messageSimpleChecksumbytebyteHeader() {
		SimpleChecksumMessage m = MessageFactory.messageSimpleChecksum(destination, source, header);
		Assert.assertEquals(m.getDestination(), destination);
		Assert.assertEquals(m.getHeader(), header.getValue());
		Assert.assertEquals(m.getSource(), source);
		Assert.assertEquals(m.getDataBytes(), MessageFactory.EMPTY_DATA_BYTES);
	}

	@Test
	public void messageSimpleChecksumbytebyteHeaderbyte() {
		SimpleChecksumMessage m = MessageFactory.messageSimpleChecksum(destination, source, header, someData);
		Assert.assertEquals(m.getDestination(), destination);
		Assert.assertEquals(m.getHeader(), header.getValue());
		Assert.assertEquals(m.getSource(), source);
		Assert.assertEquals(m.getDataBytes(), someData);
	}
}
