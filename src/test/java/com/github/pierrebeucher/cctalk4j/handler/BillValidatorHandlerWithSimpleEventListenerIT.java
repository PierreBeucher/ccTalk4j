package com.github.pierrebeucher.cctalk4j.handler;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.DeviceConfigurationException;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.device.DeviceRequestException;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;

public class BillValidatorHandlerWithSimpleEventListenerIT {
	
	private byte address;
	private String comPort;
	
	private BillValidator device;
	private BillValidatorHandler handler;
	
	@Parameters({"billValidator.comPort", "billValidator.address"})
	@BeforeClass
	public void beforeClass(String comPort, int address) throws DeviceConfigurationException{
		this.address = Utils.unsignedIntToByte(address);
		this.comPort = comPort;
		
		device = DeviceFactory.billValidatorSerialCRC(this.comPort, this.address);
		handler = new BillValidatorHandler(device);
	}
	
	/**
	 * Used for manual testing to ensure bill events are properly handled.
	 * @throws DeviceHandlingException 
	 * @throws InterruptedException 
	 * @throws MessagePortException 
	 * @throws DeviceConfigurationException 
	 */
	//@Test
	public void acceptanceTest() throws DeviceHandlingException, InterruptedException, MessagePortException, DeviceConfigurationException{
		device = DeviceFactory.billValidatorSerialCRC(comPort, address);
		handler = new BillValidatorHandler(device);
		handler.addListener(new SimpleBillEventListener());
		handler.initialise();
		handler.startInputAcceptance();
		Thread.sleep(30000);
		handler.stopInputAcceptance();
		handler.terminate();
	}
	
	/**
	 * Manual test to check an hypothetical payment and monitoring threads are running
	 * concurrently, one to handle payment from user, the other to check device state.
	 * Tets the synchronization of the Device class.
	 * @throws DeviceHandlingException
	 * @throws InterruptedException
	 * @throws MessagePortException
	 * @throws DeviceConfigurationException 
	 */
	//@Test
	public void syncTest() throws DeviceHandlingException, InterruptedException, MessagePortException, DeviceConfigurationException{
		device = DeviceFactory.billValidatorSerialCRC(comPort, address);
		handler = new BillValidatorHandler(device);
		handler.addListener(new SimpleBillEventListener());
		handler.initialise();
		
		//time during which Threads will run, in ms
		final long testTime = 30000;
		
		Runnable paymentRunnable = new Runnable(){
			@Override
			public void run() {
				try{
					handler.startInputAcceptance();
					Thread.sleep(testTime);
					handler.stopInputAcceptance();
					handler.terminate();
				} catch (Exception e){
					
				}
			}
		};
		
		Runnable monitoringRunnable = new Runnable(){
			@Override
			public void run() {
				try {
					for(int i=0; i<=(testTime/1000); i++){
						handler.getDevice().performSelfCheck();
						Thread.sleep(1000);
					}
				} catch ( InterruptedException | DeviceRequestException e) {
					throw new RuntimeException(e);
				}
			}
		};
		
		Thread monitoringThread = new Thread(monitoringRunnable);
		Thread paymentThread = new Thread(paymentRunnable);
		
		monitoringThread.start();
		paymentThread.start();
		
		monitoringThread.join((long) (testTime * 1.3));
		paymentThread.join(5000);
	}
}
