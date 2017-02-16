package com.github.pierrebeucher.cctalk4j.device;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.core.SerialMessagePort;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.SelfCheckResponseWrapper;

public class AbstractDeviceIT {
	
	private String portName;
	private byte address;
	private String deviceManufacturerId;
	private String buildCode;
	private String equipmentCatId;
	private String productCode;
	
	@Parameters({"billValidator.comPort", "billValidator.address",
		"billValidator.manufacturerId", "billValidator.buildCode",
		"billValidator.equipmentCatId", "billValidator.productCode"})
	@BeforeClass
	public void beforeClass(String portName, int address,
			String deviceManufacturerId, String buildCode,
			String equipmentCatId, String productCode){
		this.portName = portName;
		this.address = Utils.unsignedIntToByte(address);
		this.deviceManufacturerId = deviceManufacturerId;
		this.buildCode = buildCode;
		this.equipmentCatId = workaround_equipementCatId(equipmentCatId);
		this.productCode = productCode;
	}
	
	/**
	 * Ugly workaround for <billValidator.equipmentCatId>Bill Validator</billValidator.equipmentCatId>
	 * Maven alone works fine but running it in Eclipse the space is badly handled
	 * and gives error like "Could not find or load main class Validator".
	 * So we had to put quotes... <billValidator.equipmentCatId>"Bill Validator"</billValidator.equipmentCatId>
	 * 
	 * @param equipmentCatId
	 */
	private String workaround_equipementCatId(String equipementCatId){
		if(equipementCatId != null && equipementCatId.startsWith("\"")
				&& equipementCatId.endsWith("\"") && equipementCatId.length() >= 3){
			return equipementCatId.substring(1, equipementCatId.length()-1);
		} else {
			return equipementCatId;
		}
	}
	
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
	public void simplePoll() throws DeviceRequestException, MessagePortException {
		Device device = buildTestDevice();
		try{
			device.connect();
			device.simplePoll();
		} finally {
			device.disconnect();
		}
	}

	@Test
	public void requestBuildCode() throws DeviceRequestException, MessagePortException {
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
	public void requestEquipmentCategoryId() throws DeviceRequestException, MessagePortException {
		Device device = buildTestDevice();
		try{
			device.connect();
			String bcode = device.requestEquipmentCategoryId();
			Assert.assertEquals(bcode, equipmentCatId);
		} finally {
			device.disconnect();
		}
	}

	@Test
	public void requestManufacturerId() throws DeviceRequestException, MessagePortException {
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
	public void requestProductCode() throws DeviceRequestException, MessagePortException {
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
	public void performSelfCheck_nominal() throws DeviceRequestException, MessagePortException{
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
