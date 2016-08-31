package com.github.pierrebeucher.cctalk4j.handler;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.device.Device;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.handler.AbstractDeviceHandler;
import com.github.pierrebeucher.cctalk4j.handler.DeviceHandlingException;

public class AbstractDeviceHandlerTest {

	private byte address = 40;
	private String comPort = "COM6";
	private Device device;
	private MyDeviceHandler handler;

	@BeforeClass
	public void beforeClass(){
		device = DeviceFactory.billValidatorSerialCRC(comPort, address);
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
