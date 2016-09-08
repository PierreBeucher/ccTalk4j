package com.github.pierrebeucher.cctalk4j.device;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.core.SerialMessagePort;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.SelfCheckResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

public class AbstractDeviceIT {
	
	private String portName = "COM6";
	private byte address = 40;
	private String deviceManufacturerId = "AST";
	private String buildCode = "0010010I";
	private String equipementCatId = "Bill Validator";
	private String productCode = "GBA_ST2";
	
	private Device buildTestDevice(){
		MessagePort port = new SerialMessagePort(portName, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		return new MyAbstractDevice(port,
				CRCChecksumMessageBuilder.class,
				address);
	}
	
	@Test
	public void connect() throws MessagePortException {
		Device device = buildTestDevice();
		try{
			device.connect();
		} finally {
			device.disconnect();
		}
	}
	
	@Test
	public void isConnected() throws MessagePortException {
		Device device = buildTestDevice();
		try {
			device.connect();
			Assert.assertTrue(device.isConnected());
		} finally {
			device.disconnect();
		}
	}
	
	@Test
	public void disconnect() throws MessagePortException {
		Device device = buildTestDevice();
		device.connect();
		device.disconnect();
		Assert.assertTrue(!device.isConnected());
	}
	
	@Test
	public void simplePoll() throws MessageIOException, UnexpectedContentException {
		Device device = buildTestDevice();
		try{
			device.connect();
			boolean pollResult = device.simplePoll();
			Assert.assertEquals(pollResult, true);
		} finally {
			device.disconnect();
		}
	}

	@Test
	public void requestBuildCode() throws MessageIOException, UnexpectedContentException {
		Device device = buildTestDevice();
		try{
			device.connect();
			String bcode = device.requestBuildCode();
			Assert.assertEquals(bcode, buildCode);
		} finally {
			device.disconnect();
		}
	}

	@Test
	public void requestEquipmentCategoryId() throws MessageIOException, UnexpectedContentException {
		Device device = buildTestDevice();
		try{
			device.connect();
			String bcode = device.requestEquipmentCategoryId();
			Assert.assertEquals(bcode, equipementCatId);
		} finally {
			device.disconnect();
		}
	}

	@Test
	public void requestManufacturerId() throws MessageIOException, UnexpectedContentException {
		Device device =	this.buildTestDevice();
		try{
			device.connect();
			String id = device.requestManufacturerId();
			Assert.assertEquals(id, deviceManufacturerId);
		} finally {
			device.disconnect();
		}
	}

	@Test
	public void requestProductCode() throws MessageIOException, UnexpectedContentException {
		Device device =	this.buildTestDevice();
		try{
			device.connect();
			String id = device.requestProductCode();
			Assert.assertEquals(id, productCode);
		} finally {
			device.disconnect();
		}
	}
	
	@Test
	public void performSelfCheck_nominal() throws MessageIOException, UnexpectedContentException{
		Device device =	this.buildTestDevice();
		try{
			device.connect();
			SelfCheckResponseWrapper wp = device.performSelfCheck();
			Assert.assertEquals(wp.faultCode(), SelfCheckResponseWrapper.NO_FAULT_DETECTED);
		} finally {
			device.disconnect();
		}
	}
	
	private class MyAbstractDevice extends AbstractDevice{

		public MyAbstractDevice(MessagePort port, Class<? extends MessageBuilder> messageBuilderClass,
				byte deviceAddress) {
			super(port, messageBuilderClass, deviceAddress);
		}
		
	}
}
