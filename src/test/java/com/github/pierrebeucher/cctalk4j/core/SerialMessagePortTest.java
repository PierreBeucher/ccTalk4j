package com.github.pierrebeucher.cctalk4j.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.CRCChecksumMessage;
import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.core.SerialMessagePort;
import com.github.pierrebeucher.cctalk4j.core.Utils;

public class SerialMessagePortTest {

	/*
	 * Com port used for test
	 */
	private String comPort = "COM6";
	
	/*
	 * Address of the tested device
	 */
	private byte testDeviceDestination = Utils.unsignedIntToByte(40);
	
	@Test
	public void isClosed_nominal() throws MessagePortException {
		MessagePort port = new SerialMessagePort(comPort, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		port.open();
		port.close();
		Assert.assertTrue(port.isClosed());
	}
	
	@Test
	public void isClosed_open() throws MessagePortException {
		MessagePort port = new SerialMessagePort(comPort, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		try{
			port.open();
			Assert.assertFalse(port.isClosed());
		} finally {
			port.close();
		}
		
	}
	
	@Test
	public void isClosed_never_used() throws MessagePortException {
		MessagePort port = new SerialMessagePort(comPort, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		Assert.assertTrue(port.isClosed());
	}

	@Test
	public void isOpen_nominal() throws MessagePortException {
		MessagePort port = new SerialMessagePort(comPort, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		try{
			port.open();
			Assert.assertTrue(port.isOpen());
		} finally {
			port.close();
		}
	}

	@Test
	public void isOpen_closed() throws MessagePortException {
		MessagePort port = new SerialMessagePort(comPort, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		port.open();
		port.close();
		Assert.assertFalse(port.isOpen());
	}
	
	@Test
	public void isOpen_never_open() throws MessagePortException {
		MessagePort port = new SerialMessagePort(comPort, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		Assert.assertFalse(port.isOpen());
	}
	
	@Test
	public void write() throws MessagePortException{
		MessagePort port = new SerialMessagePort(comPort, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		Message writeMessage = new CRCChecksumMessage(
				testDeviceDestination,
				Header.SIMPLE_POLL.getValue(),
				new byte[]{});
		try{
			port.open();
			port.write(writeMessage); //simple poll
		} finally {
			port.close();
		}
	}

	@Test
	public void read() throws MessagePortException, InterruptedException, MessageParsingException {
		MessagePort port = new SerialMessagePort(comPort, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		
		Message writeMessage = new CRCChecksumMessage(
				testDeviceDestination,
				Header.SIMPLE_POLL.getValue(),
				new byte[]{});
		Message expected = new CRCChecksumMessage(
				Utils.unsignedIntToByte(1),
				Header.NONE.getValue(),
				new byte[]{});
		try{
			port.open();
			port.write(writeMessage); //simple poll
			Message readMessage = port.read(5000);
			Assert.assertEquals(readMessage, expected);
		} finally {
			port.close();
		}
	}
}
