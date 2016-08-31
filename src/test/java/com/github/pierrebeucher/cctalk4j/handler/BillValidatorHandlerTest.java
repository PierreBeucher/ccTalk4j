package com.github.pierrebeucher.cctalk4j.handler;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;
import com.github.pierrebeucher.cctalk4j.handler.BillValidatorHandler;
import com.github.pierrebeucher.cctalk4j.handler.DeviceHandlingException;

public class BillValidatorHandlerTest {
	
	private byte address = 40;
	private String comPort = "COM6";
	private BillValidator device;
	private BillValidatorHandler handler;
	
	private int scalingFactor = 1000;
	private int decimalPlace = 0;
	private String countryCode = "XO";
	
	
	@BeforeClass
	public void beforeClass(){
		device = DeviceFactory.billValidatorSerialCRC(comPort, address);
		handler = new BillValidatorHandler(device);
		handler.addListener(new SimpleBillEventListener());
	}
	
	@Test
	public void initialise() throws DeviceHandlingException{
		handler.initialise();
		Assert.assertTrue(handler.isInitialised());
	}
	
	@Test
	public void doPreAcceptance() throws DeviceHandlingException{
		handler.doPreAcceptance();
	}
	
	@Test
	public void getDeviceScalingFactor(){
		Assert.assertEquals(handler.getDeviceScalingFactor(), scalingFactor);
	}
	
	@Test
	public void getDeviceDecimalPlace(){
		Assert.assertEquals(handler.getDeviceDecimalPlace(), decimalPlace);
	}
	
	@Test
	public void getDeviceCountryCode(){
		Assert.assertEquals(handler.getDeviceCountryCode(), countryCode);
	}
	
	@Test
	public void startInputAcceptance() throws DeviceHandlingException, InterruptedException{
		handler.startInputAcceptance();
	//	Thread.sleep(10000);
	}

	@AfterClass
	public void afterClass() throws MessagePortException{
		if(this.device.isConnected()){
			this.device.disconnect();
		}
	}
}
