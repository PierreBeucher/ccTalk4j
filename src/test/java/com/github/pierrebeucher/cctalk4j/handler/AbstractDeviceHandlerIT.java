package com.github.pierrebeucher.cctalk4j.handler;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.Device;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.handler.AbstractDeviceHandler;
import com.github.pierrebeucher.cctalk4j.handler.DeviceHandlingException;

public class AbstractDeviceHandlerIT {

	private byte address;
	private String comPort;
	
	private Device device;
	private MyDeviceHandler handler;

	@Parameters({"billValidator.comPort", "billValidator.address"})
	@BeforeClass
	public void beforeClass(String comPort, int address){
		this.address = Utils.unsignedIntToByte(address);
		this.comPort = comPort;
		device = DeviceFactory.billValidatorSerialCRC(this.comPort, this.address);
		handler = new MyDeviceHandler(device);
	}

	@Test
	public void initialise() throws DeviceHandlingException {
		handler.initialise();
		Assert.assertTrue(handler.isConnected());
		Assert.assertTrue(handler.isInitialised());
	}
	
	@Test
	public void startInputAcceptance() throws DeviceHandlingException {
		handler.startInputAcceptance();
		Assert.assertTrue(handler.acceptInput());
	}
	
	@Test
	public void stopInputAcceptance() throws DeviceHandlingException {
		handler.stopInputAcceptance();
		Assert.assertFalse(handler.acceptInput());
	}
	
	@AfterClass
	public void afterClass() throws MessagePortException{
		if(this.device.isConnected()){
			this.device.disconnect();
		}
	}
	
	private class MyDeviceHandler extends AbstractDeviceHandler<Device>{
		
		public MyDeviceHandler(Device device) {
			super(device);
		}

		@Override
		protected void doPreAcceptance() throws DeviceHandlingException {
		}

		@Override
		protected void startCreditPolling() throws DeviceHandlingException {
		}

		@Override
		protected void stopCreditPolling() throws DeviceHandlingException {
		}

		@Override
		protected void doPostAcceptance() throws DeviceHandlingException {			
		}
	}
}
