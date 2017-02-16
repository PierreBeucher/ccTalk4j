package com.github.pierrebeucher.cctalk4j.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.device.Device;
import com.github.pierrebeucher.cctalk4j.device.DeviceRequestException;
import com.github.pierrebeucher.cctalk4j.device.DeviceRequestTimeoutException;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.SelfCheckResponseWrapper;

/**
 * <p>Abstract implement of <code>DeviceHandler</code>. Provide basic
 * functionality to initialize/terminate and start/stop input acceptance.
 * This class needs to be specialized for each device type, depending
 * on the device initialisation and acceptance requirements. </p>
 * @author Pierre Beucher
 *
 * @param <E>
 */
public abstract class AbstractDeviceHandler<E extends Device> implements DeviceHandler {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/*
	 * Device handled
	 */
	protected E device;
	
	/*
	 * Whether this handler has been initialised
	 */
	private boolean initialised;
	
	/*
	 * Whether this handler's device accept input
	 */
	private boolean acceptInput;
	
	/**
	 * @param device device to handle
	 */
	public AbstractDeviceHandler(E device) {
		super();
		this.device = device;
		this.initialised = false;
	}
	
	@Override
	public Device getDevice() {
		return device;
	}
	
	/**
	 * <p>
	 * Connect and initialize the handled <code>Device</code>.
	 * Post connection initialization procedure performs the following tasks:
	 * <ul>
	 * <li>Simple poll</li>
	 * <li>Perform self check</li>
	 * <li>Request product code</li>
	 * <li>Request build code</li>
	 * </ul>
	 * TODO:
	 * <ul>
	 * <li>Request manufacturer id</li>
	 * <li>Request serial number</li>
	 * <li>Request software revision</li>
	 * <li>Request comms revision</li>
	 * </ul>
	 * <i>Note: current implementation does not check the returned values. Future implementation
	 * will provide a way to check the returned values.</i>
	 * </p>
	 * @throws DeviceHandlingException if connection or initialization procedure fail.
	 */
	public void initialise() throws DeviceHandlingException{
		doConnect();
		doInitialisation();
	}
	
	/**
	 * Called by {@link #initialise()} to perform initialization tasks.
	 * @throws DeviceHandlingException
	 */
	protected void doConnect() throws DeviceHandlingException{
		try {
			device.connect();
		} catch (MessagePortException e){
			throw new DeviceHandlingException("Cannot connect to device.", e);
		}
	}
	
	/**
	 * Called by {@link #initialise()} to perform initialization tasks.
	 * @throws DeviceHandlingException
	 * @throws DeviceRequestException 
	 */
	protected void doInitialisation() throws DeviceHandlingException{
		try{
			checkSimplePoll();
			
			SelfCheckResponseWrapper selfCheck = device.performSelfCheck();
			if(selfCheck.hasFault()){
				throw new DeviceHandlingException("Self check reported a fault: faultCode=" + selfCheck.faultCode() +
						", optionalExtraInfo=" + selfCheck.optionalExtraInfo() + ". Refer to ccTalk specifications for details.");
			}
			
			logger.debug("Request equipment category id: {}", device.requestEquipmentCategoryId());
			logger.debug("Request product code: {}", device.requestProductCode());
			logger.debug("Request build coded: {}", device.requestBuildCode());
			logger.debug("Request manufacturer id: {}", device.requestManufacturerId());
			
			logger.debug("Initialisation succes.");
			this.initialised = true;
		} catch (DeviceRequestException e){
			throw new DeviceHandlingException("Cannot perform initialisation: " + e, e);
		}
	}
	
	/**
	 * Perform a simple poll with N retries to ensure device 
	 * is responding
	 * @throws DeviceRequestException 
	 * @throws DeviceHandlingException 
	 */
	private void checkSimplePoll() throws DeviceHandlingException{
		int tryCount = 3;
		for(int i=0; i<tryCount; i++){
			try {
				device.simplePoll();
				return; //simple poll OK
			} catch (DeviceRequestException e) {
				if(e instanceof DeviceRequestTimeoutException){
					logger.warn("Cannot simple a simple poll: timeout. Try count is: {}", i);
				} else {
					throw new DeviceHandlingException("Simple poll error: " + e, e);
				}
			}
		}
		throw new DeviceHandlingException("Cannot perform a simple poll after " + tryCount + " attempt ended in timeout.");
	}
	
	/**
	 * @return true if handled device is connected
	 */
	public boolean isConnected(){
		return device.isConnected();
	}
	
	/**
	 * 
	 * @return true if this <code>DeviceHandler</code> has been initialised through {@link #initialise()}
	 */
	public boolean isInitialised(){
		return this.initialised;
	}
	
	/**
	 * Terminate and disconnect the handled <code>Device</code> 
	 * @throws MessagePortException 
	 */
	public void terminate() throws MessagePortException{
		device.disconnect();
	}
	
	/**
	 * <p>Start the handled <code>Device</code> input acceptance,
	 * and start polling the device periodically for credit.</p>
	 * @throws DeviceHandlingException 
	 * @see #stopInputAcceptance() 
	 */
	public void startInputAcceptance() throws DeviceHandlingException{
		logger.debug("Start accepting input for {}.", device);
		
		doPreAcceptance();
		startCreditPolling();
		this.acceptInput = true;
	}
	
	/**
	 * Called by {@link #startInputAcceptance()} to perform
	 * pre acceptance tasks before starting credit polling
	 * with {@link #startCreditPolling()}.
	 * @throws DeviceHandlingException 
	 */
	protected abstract void doPreAcceptance() throws DeviceHandlingException;
	
	/**
	 * Called by {@link #stopInputAcceptance()} to perform
	 * post acceptance tasks before stopping credit polling
	 * with {@link #stopCreditPolling()}.
	 * @throws DeviceHandlingException 
	 */
	protected abstract void doPostAcceptance() throws DeviceHandlingException;
	
	/**
	 * Called by {@link #startInputAcceptance()} to launch
	 * the credit polling, right after {@link #doPreAcceptance()}.
	 */
	protected abstract void startCreditPolling() throws DeviceHandlingException;
	
	/**
	 * Called by {@link #stopInputAcceptance()} to 
	 * stop credit polling.
	 */
	protected abstract void stopCreditPolling() throws DeviceHandlingException;
	
	/**
	 * <p>Stop the handled <code>Device</code> input acceptance,
	 * disabling </p>
	 * @see #startInputAcceptance()
	 */
	public void stopInputAcceptance() throws DeviceHandlingException{
		logger.debug("Stop accepting input for {}.", device);
		
		stopCreditPolling();
		this.acceptInput = false;
	}

	/**
	 * 
	 * @return true if this handler's <code>Device</code> has been set to accept input, false otherwise.
	 * @see #startInputAcceptance()
	 * @see #stopInputAcceptance()
	 */
	public boolean acceptInput() {
		return acceptInput;
	}

	@Override
	public String toString() {
		return "DeviceHandler[" + device + "]";
	}
}
