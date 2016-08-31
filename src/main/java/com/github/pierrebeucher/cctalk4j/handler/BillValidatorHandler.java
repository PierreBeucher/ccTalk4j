package com.github.pierrebeucher.cctalk4j.handler;

import java.util.HashMap;
import java.util.Map;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.InhibitMask;
import com.github.pierrebeucher.cctalk4j.device.bill.Bill;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.CountryScalingFactorWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

/**
 * <p>The <code>BillValidatorHandler</code> is used to handle a <code>BillValidator</code>.
 * It can initialise/terminate, start/stop acceptance and trigger callbacks 
 * when event are reads.</p>
 * <p> Example usage:
 * <pre>
 * {@code
 * //create a new device and handler
 * device = DeviceFactory.billValidatorSerialCRC(comPort, address);
 * handler = new BillValidatorHandler(device);
 * 
 * //register an event listener
 * handler.addListener(new BillEventListener(){
 * 	public void newEvent(BillValidatorHandler handler, BillEvent event){
 * 		//do something...
 * 	}
 * 
 * 	void lostEvent(int lostEventCount, BillEventBuffer previousBuffer, BillEventBuffer newBuffer){
 * 		//do something...
 * 	}
 * });
 * 
 * //start accepting
 * //this will launch a Thread polling our device regularly for new events
 * handler.startInputAcceptance();
 * 
 * //the device now accepts bills
 * //each bill inserted, or any event, will trigger a call back to the previously added listener
 * //...
 * 
 * //once finishied, stop accepting and terminate
 * handler.stopInputAcceptance();
 * handler.terminate();
 * }
 * </pre>
 * @author Pierre Beucher
 *
 */
public class BillValidatorHandler extends AbstractDeviceHandler<BillValidator> {

	/**
	 * Default credit poll period for a <code>BillValidatorHandler</code>
	 */
	public static final long DEFAULT_CREDIT_POLL_PERIOD = 200;
	
	/*
	 * Bill map programmed in the device
	 * key is bill type (as returned with a bill event buffer)
	 */
	private Map<Byte, Bill> billMap;
	
	/*
	 * Country code handled with the device
	 */
	private String deviceCountryCode;
	
	/*
	 * Scaling factor request from device
	 */
	private int deviceScalingFactor;

	/*
	 * Decimal place request from device
	 */
	private int deviceDecimalPlace;
	
	/*
	 * Event handler used when credit polling
	 */
	private BillEventHandler eventHandler;
	
	/*
	 * Credit poll period when credit polling
	 */
	private long creditPollPeriod;
	
	/*
	 * Current credit poller runnable
	 */
	private BillValidatorCreditPoller creditPoller;
	
	/*
	 * Thread running the credit poller runnable
	 */
	private Thread creditPollerThread;
	
	/**
	 * Create a new <code>BillValidatorHandler</code> for the given
	 * validator.
	 * @param validator validator to handle
	 */
	public BillValidatorHandler(BillValidator validator) {
		super(validator);
		this.billMap = new HashMap<Byte, Bill>();
		this.eventHandler = new BillEventHandler(this);
		this.creditPollPeriod = DEFAULT_CREDIT_POLL_PERIOD;
	}
	
	/**
	 * Add a <code>BillEventListener</code> to this <code>BillValidatorHandler</code>.
	 * The added listener will be notified of any event read from the handle device. 
	 * @param listener listener to add
	 */
	public void addListener(BillEventListener listener){
		this.eventHandler.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * <p>Perform the following operations:
	 * <ul>
	 * <li>Request and log (debug level) bill id 1 to n (until a not programmed bill is encountered)</li>
	 * <li>Request country scaling factor</li>
	 * <li>Request currency revision</li>
	 * <li>Modify bill operating mode (stacker &amp; escrow enabled)</li>
	 * <li>Modify inhibit status (accept all programmed bills)</li>
	 * <li>Modify master inhibit status</li>
	 * </ul>
	 * <i>Note: a maximum of 128 bill ids are requested and logged </i>
	 * @throws DeviceHandlingException 
	 */
	@Override
	protected void doPreAcceptance() throws DeviceHandlingException {
		try {
			requestProgrammedBills();
			requestCountryScalingFactor();
			requestCurrencyRevision();
			modifyBillOperatingMode(true);
			modifyInhibitStatus(false); 
			modifyMasterInhibit(false);
		} catch (MessageIOException | UnexpectedContentException e) {
			throw new DeviceHandlingException("Pre-acceptance error.", e);
		}
		
	}
	
	@Override
	protected void doPostAcceptance() throws DeviceHandlingException {
		try{
			modifyBillOperatingMode(false);
			modifyInhibitStatus(true); 
			modifyMasterInhibit(true);
		} catch (MessageIOException | UnexpectedContentException e) {
			throw new DeviceHandlingException("Post-acceptance error.", e);
		}
	}

	private void requestProgrammedBills() throws DeviceHandlingException, MessageIOException, UnexpectedContentException{
		//request, keep in map and log bills
		//deduce country code from the accepted bills
		//only 1 country code at a time can be handled
		deviceCountryCode = null;
		for(byte billType=1; billType<=128; billType++){
			Bill bill = device.requestBillId(billType);
			if(Bill.isProgrammed(bill)){
				logger.debug("Request bill id for bill {}: {}", billType, bill);
				mapBill(bill);
				
				//check the country code to ensure each bill have the same country
				if(deviceCountryCode == null){
					deviceCountryCode = bill.countryCode();
				} else if(!deviceCountryCode.equals(bill.countryCode())){
					throw new DeviceHandlingException("Device seems to accept bills for country code " + deviceCountryCode +
							" and " + bill.countryCode() + ", but only one country code can be handled at a time.");
				}
			} else {
				break;
			}
		}
	}
	
	private void mapBill(Bill bill){
		billMap.put(bill.billType(), bill);
	}
	
	
	private void requestCountryScalingFactor() throws MessageIOException, UnexpectedContentException, IllegalArgumentException{
		CountryScalingFactorWrapper scalingFactorWrapper = device.requestCountryScalingFactor(deviceCountryCode);
		this.deviceScalingFactor = scalingFactorWrapper.getScalingFactor();
		this.deviceDecimalPlace = Utils.byteToUnsignedInt(scalingFactorWrapper.getDecimalPlace());
	}
	
	private void requestCurrencyRevision(){
		//TODO
	}
	
	private void modifyBillOperatingMode(boolean enabled) throws MessageIOException, UnexpectedContentException{
		device.modifyBillOperatingMode(enabled, enabled);
	}
	
	private void modifyInhibitStatus(boolean inhibitEnabled) throws MessageIOException, UnexpectedContentException{
		InhibitMask mask = inhibitEnabled ? InhibitMask.FULLY_INHIBITING_MASK : InhibitMask.FULLY_ENABLING_MASK;
		device.modifyInhibitStatus(mask);
	}
	
	private void modifyMasterInhibit(boolean inhibit) throws MessageIOException, UnexpectedContentException{
		device.modifyMasterInhibitStatus(!inhibit);
	}

	@Override
	protected void startCreditPolling() {
		logger.info("Starting credit polling for {}", device);
		
		this.creditPoller = new BillValidatorCreditPoller(device, creditPollPeriod, eventHandler);
		this.creditPollerThread = new Thread(creditPoller);
		this.creditPollerThread.start();
	}

	@Override
	protected void stopCreditPolling() throws DeviceHandlingException {
		this.creditPoller.setContinuePolling(false);
		try {
			this.creditPollerThread.join();
		} catch (InterruptedException e) {
			throw new DeviceHandlingException("Error while waiting for poller thread to die.", e);
		}
	}

	@Override
	public BillValidator getDevice() {
		return (BillValidator) super.getDevice();
	}

	/**
	 * 
	 * @return the handled <code>Device</code> country code.
	 */
	public String getDeviceCountryCode() {
		return deviceCountryCode;
	}

	/**
	 * 
	 * @return the handled <code>Device</code> scaling factor.
	 */
	public int getDeviceScalingFactor() {
		return deviceScalingFactor;
	}

	/**
	 * 
	 * @return the handled <code>Device</code> scaling factor decimal place.
	 */
	public int getDeviceDecimalPlace() {
		return deviceDecimalPlace;
	}
	
	/**
	 * 
	 * @return the credit poll period when accepting input
	 */
	public long getCreditPollPeriod() {
		return creditPollPeriod;
	}

	/**
	 * Return a Map containg all Bill programmed in the handled device.
	 * Note that <code>null</code> will be returned unless {@link #doPreAcceptance()}
	 * as been called at least once, otherwise the bill list will not have
	 * been queried yet.
	 * @return a Map representing device's programmed bills
	 */
	public Map<Byte, Bill> getBillMap() {
		return billMap;
	}

	public class BillValidatorCreditPoller extends CreditPollingRunnable<BillValidator>{

		private BillEventHandler eventHandler;
		
		public BillValidatorCreditPoller(BillValidator device, long pollPeriod, BillEventHandler eventHandler) {
			super(device, pollPeriod);
			this.eventHandler = eventHandler;
		}

		@Override
		protected void doCreditPoll() {
			try {
				eventHandler.feed(device.readBufferedNoteEvents());
			} catch (MessageIOException | UnexpectedContentException e) {
				logger.error("Error during credit poll: " + e, e);
			}
		}
		
	}

}
