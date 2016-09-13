package com.github.pierrebeucher.cctalk4j.handler;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;
import com.github.pierrebeucher.cctalk4j.handler.BillValidatorHandler;
import com.github.pierrebeucher.cctalk4j.handler.DeviceHandlingException;

public class BillValidatorHandlerIT {
	
	private byte address;
	private String comPort;
	
	private BillValidator device;
	private BillValidatorHandler handler;
	
	@Parameters({"billValidator.comPort", "billValidator.address"})
	@BeforeClass
	public void beforeClass(String comPort, int address){
		this.address = Utils.unsignedIntToByte(address);
		this.comPort = comPort;
		
		device = DeviceFactory.billValidatorSerialCRC(this.comPort, this.address);
		handler = new BillValidatorHandler(device);
	}
	
	@Test
	public void setCreditPollPeriod(){
		handler.setCreditPollPeriod(424);
		Assert.assertEquals(handler.getCreditPollPeriod(), 424);
	}
	
	@Test(priority=1)
	public void initialise() throws DeviceHandlingException{
		handler.initialise();
		Assert.assertTrue(handler.isInitialised());
	}
	
	@Test(priority=2)
	public void doPreAcceptance() throws DeviceHandlingException{
		handler.doPreAcceptance();
	}
	
	/**
	 * Simple test making sure at least 1 event is read when accepting input
	 * @throws DeviceHandlingException
	 * @throws InterruptedException
	 */
	@Test(priority=4)
	public void startInputAcceptance() throws DeviceHandlingException, InterruptedException{
		final List<BillEvent> events = new ArrayList<BillEvent>();
		BillEventListener listener = new BillEventListener(){
			@Override
			public void newEvent(BillValidatorHandler handler, BillEvent event) {
				events.add(event);
			}
			@Override
			public void lostEvent(int lostEventCount, BillEventBuffer previousBuffer, BillEventBuffer newBuffer) {}
		};
		handler.addListener(listener);
		handler.setCreditPollPeriod(50);
		handler.startInputAcceptance();
		Thread.sleep(200);
		Assert.assertTrue(events.size() > 0);
		
	}
	
	@Test(priority=5)
	public void stopInputAcceptance() throws DeviceHandlingException, InterruptedException{
		handler.stopInputAcceptance();
	}

	@AfterClass
	public void afterClass() throws MessagePortException{
		if(this.device.isConnected()){
			this.device.disconnect();
		}
	}
}
