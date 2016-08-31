package com.github.pierrebeucher.cctalk4j.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.device.Device;

/**
 * <p>
 * A <code>Runnable</code> periodically polling a device for credit
 * until {@link #setContinuePolling(boolean)} is set to <code>false</code>.
 * To start polling, use this <code>Runnable</code> in a <code>Thread</code>
 * and start. Then, call {@link #setContinuePolling(boolean)} with <code>false</code>
 * to stop the polling and end the <code>Thread</code>.
 * </p>
 * @author Pierre Beucher
 *
 */
public abstract class CreditPollingRunnable<E extends Device> implements Runnable{
	
	private boolean continuePolling;
	
	private long pollPeriod;
	
	protected E device;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 * @param device device to poll
	 * @param pollPeriod poll period in ms
	 */
	public CreditPollingRunnable(E device, long pollPeriod) {
		super();
		this.continuePolling = false;
		this.pollPeriod = pollPeriod;
		this.device = device;
	}
	
	@Override
	public void run() {
		if(!device.isConnected()){
			throw new RuntimeException("Device needs to be connected before starting credit poll.");
		}
		
		this.continuePolling = true;
		while(continuePolling){
			doCreditPoll();
			try {
				Thread.sleep(pollPeriod);
			} catch (InterruptedException e) {
				logger.error("Poll has been interrupted for {}: {}", this, e);
			}
		}
	}
	
	/**
	 * Do perform the credit poll. Called by {@link #run()} periodically.
	 */
	protected abstract void doCreditPoll();

	/**
	 * 
	 * @return true if credit polling is to continue.
	 */
	public synchronized boolean isContinuePolling() {
		return continuePolling;
	}

	/**
	 * This method will prevent this <code>Runnable</code> to perform
	 * any more polling. If a polling is currently being performed,
	 * it will be finished and no other polling will occur. If no polling
	 * is being perform, the <code>Runnable</code> will wait for a 
	 * maximum of a single poll period before finishing.
	 * @param continuePolling false to stop polling, true to continue
	 */
	public synchronized void setContinuePolling(boolean continuePolling) {
		this.continuePolling = continuePolling;
	}

	@Override
	public String toString() {
		return "CreditPollingRunnable [device=" + device + "]";
	}

}
