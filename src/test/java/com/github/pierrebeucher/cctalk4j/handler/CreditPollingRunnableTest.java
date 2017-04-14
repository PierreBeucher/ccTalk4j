package com.github.pierrebeucher.cctalk4j.handler;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.device.Device;
import com.github.pierrebeucher.cctalk4j.device.DummyDevice;
import com.github.pierrebeucher.cctalk4j.handler.CreditPollingRunnable;

public class CreditPollingRunnableTest {
	
	private Device device = new DummyDevice();
	private CreditPollingRunnable<?> poller;
	private Thread pollerThread;
	
	@BeforeClass
	public void beforeClass() throws MessagePortException{
		poller = new CreditPollingRunnable<Device>(device, 200){
			@Override
			protected void doCreditPoll() {
				
			}

			@Override
			protected void beforePollStart() throws CreditPollingException {
			}
		};
		device.connect();
	}
	
	@Test
	public void isContinuePolling_beforeStart() {
		Assert.assertFalse(poller.isContinuePolling());
	}
	
	@Test(dependsOnMethods={"isContinuePolling_beforeStart"})
	public void isContinuePolling_afterStart() throws InterruptedException {
		pollerThread = new Thread(poller);
		pollerThread.start();
		Thread.sleep(1000);
		Assert.assertTrue(poller.isContinuePolling());
	}
	
	@Test(dependsOnMethods={"isContinuePolling_afterStart"})
	public void setContinuePolling() throws InterruptedException {
		poller.setContinuePolling(false);
		Assert.assertFalse(poller.isContinuePolling());
	}
	
	@Test(dependsOnMethods={"setContinuePolling"})
	public void checkThreadDied() throws InterruptedException{
		pollerThread.join(1000);
		Assert.assertFalse(pollerThread.isAlive());
	}
	
	
	
}
