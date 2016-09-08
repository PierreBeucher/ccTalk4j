package com.github.pierrebeucher.cctalk4j.handler;

import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;

public class BillValidatorHandlerWithSimpleEventListenerIT {
	
	private byte address = 40;
	private String comPort = "COM6";
	private BillValidator device;
	private BillValidatorHandler handler;
	
	/**
	 * Used for manual testing to ensure bill events are properly handled.
	 * @throws DeviceHandlingException 
	 * @throws InterruptedException 
	 * @throws MessagePortException 
	 */
	//@Test
	public void acceptanceTest() throws DeviceHandlingException, InterruptedException, MessagePortException{
		device = DeviceFactory.billValidatorSerialCRC(comPort, address);
		handler = new BillValidatorHandler(device);
		handler.addListener(new SimpleBillEventListener());
		handler.initialise();
		handler.startInputAcceptance();
		Thread.sleep(30000);
		handler.stopInputAcceptance();
		handler.terminate();
	}
}
