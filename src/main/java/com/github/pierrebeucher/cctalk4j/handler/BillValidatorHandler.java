package com.github.pierrebeucher.cctalk4j.handler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.device.InhibitMask;
import com.github.pierrebeucher.cctalk4j.device.bill.Bill;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.BillIdResponseWrapper;
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
 * </p>
 * <p><b>How does it works?</b><br>
 * On instantiation, a new <code>BillEventHandler</code> is created for
 * this <code>BillValidatorHandler</code> and the handled <code>BillValidator</code>.
 * When {@link #startCreditPolling()} is called, a thread is started and will periodically
 * poll the <code>BillValidator</code> events and feed them to the <code>BillEventHandler</code>.
 * The <code>BillEventHandler</code> will then notify any listener of the incoming events using
 * defined callbacks functions. </p>
 * @author Pierre Beucher
 *
 */
public class BillValidatorHandler extends AbstractDeviceHandler<BillValidator> {

	/**
	 * Default credit poll period for a <code>BillValidatorHandler</code>
	 */
	public static final long DEFAULT_CREDIT_POLL_PERIOD = 200;
	
	/**
	 * <p>Calculate a bill id real currency value using a country scaling factor.</p>
	 * <p>This function perform the following operation: valudeCode * scalingFactor * 10^(-decimalPlace).
	 * For example, with valueCode=0001, scalingFactor=100, decimalPlace=2, 1*100*10^(-2) = 1.00. </p>
	 * 
	 * @param billValueCode bill's value code
	 * @param scalingFactor bill's country scaling factor
	 * @param decimalPlace bill's country decimal place
	 * @return the real currency value for the given bill value code and country
	 */
	public static BigDecimal calculateBillCurrencyValue(String billValueCode, int scalingFactor, int decimalPlace){
		return BigDecimal.valueOf(Long.parseLong(billValueCode) * scalingFactor, decimalPlace);
	}
	
	/*
	 * Bill map programmed in the device
	 * key is bill type (as returned with a bill event buffer)
	 */
	private Map<Byte, Bill> billMap;
	
	/*
	 * Map holding known country codes and scaling factors
	 */
	private Map<String, CountryScalingFactorWrapper> countryScalingFactorMap;
	
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
		this.countryScalingFactorMap = new HashMap<String, CountryScalingFactorWrapper >();
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
	 * <p>Calling this method will trigger a sequence of commande which will
	 * request available bills and countries from the device before updating
	 * master inhibit status to accept bills. As a result, the Map returned by
	 * {@link #getBillMap()} will be reset and populated with new bills. 
	 * {@link #getBillMap()} can be used to retrieve available bills by their ID
	 * and extract the real bill value when handling <code>BillEvent</code>s with
	 * <code>BillEventListener</code>.</p>
	 * @see #getBillMap()
	 * @see #addListener(BillEventListener)
	 */
	@Override //override for javadoc details
	public void startInputAcceptance() throws DeviceHandlingException {
		super.startInputAcceptance();
	}

	/**
	 * {@inheritDoc}
	 * <p>Perform the following operations:
	 * <ul>
	 * <li>Request all available bills and their country scaling factor, calculate
	 * each bills currency value and feed a <code>Map </code> with <code>Bill</code> instances</li>
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
			requestBillsAndCountries();
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
	
	/**
	 * Internal function which will request all available bytes, the country scaling factor for the countries,
	 * related, and feed a <code>Map</code> with created <code>Bill</code> instances. 
	 * @throws DeviceHandlingException 
	 */
	private void requestBillsAndCountries() throws DeviceHandlingException{
		//request all bills and there related country code (if not already requested)
		//and create a new Bill instances for bill Map
		//stop requesting when an unprogrammed bill is encountered
		clearBillMap();
		try{
			for(byte billType=1; billType<=128; billType++){
				BillIdResponseWrapper billWp = device.requestBillId(billType);
				if(!BillIdResponseWrapper.isProgrammed(billWp)){
					break; //stop at first unprogrammed bill encountered
				}
				
				//build concrete Bill using BillId and CountryScalingFactor
				CountryScalingFactorWrapper csfWp = retrieveCountryScalingFactorFor(billWp.getCountryCode());
				
				//calculate currency value and put new Bill in Map
				try{
					BigDecimal billCurrencyValue = calculateBillCurrencyValue(
							billWp.getValueCode(), csfWp.getScalingFactor(), csfWp.getScalingFactor());
					mapBill(new Bill(billType, billWp.getCountryCode(), billWp.getValueCode(),
							billWp.getIssueCode(), billCurrencyValue));
				} catch (NumberFormatException e){
					//skip bill if cannot calculate currency value...
					logger.error("Cannot calculate bill currency value for {} with {}. Skipping bill.", billWp, csfWp, e);
					continue;
				}
			}
		} catch(MessageIOException | UnexpectedContentException e){
			throw new DeviceHandlingException("Error requesting available bills and countries", e);
		}
	}

//	private void requestProgrammedBills() throws DeviceHandlingException, MessageIOException, UnexpectedContentException{
//		//request all bills and keep them in a Map
//		//deduce the Bill value from previously requested scaling factor
//		//deduce country code from the accepted bills
//		//only 1 country code at a time can be handled
//		deviceCountryCode = null;
//		for(byte billType=1; billType<=128; billType++){
//			
//			//get bill and wrap around a Bill instance
//			BillIdResponseWrapper billWp = device.requestBillId(billType);
//			
//			if(Bill.isProgrammed(bill)){
//				logger.debug("Request bill id for bill {}: {}", billType, bill);
//				mapBill(bill);
//				
//				//check the country code to ensure each bill have the same country
//				if(deviceCountryCode == null){
//					deviceCountryCode = bill.countryCode();
//				} else if(!deviceCountryCode.equals(bill.countryCode())){
//					throw new DeviceHandlingException("Device seems to accept bills for country code " + deviceCountryCode +
//							" and " + bill.countryCode() + ", but only one country code can be handled at a time.");
//				}
//			} else {
//				break;
//			}
//		}
//	}
	
	private void clearBillMap(){
		this.billMap.clear();
	}
	
	private void mapBill(Bill bill){
		billMap.put(bill.billType(), bill);
	}
	
	/**
	 * Retrieve the country scaling factor for the given country code.
	 * Request the validator of the given country code only if it has not
	 * already been requested.
	 * @param countryCode country code to request
	 * @return the country scaling factor wrapper
	 * @throws IllegalArgumentException 
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException 
	 */
	private CountryScalingFactorWrapper retrieveCountryScalingFactorFor(String countryCode) throws MessageIOException, UnexpectedContentException, IllegalArgumentException{
		if(countryScalingFactorMap.containsKey(countryCode)){
			return countryScalingFactorMap.get(countryCode);
		} else {
			CountryScalingFactorWrapper csfWp = device.requestCountryScalingFactor(countryCode);
			countryScalingFactorMap.put(countryCode, csfWp);
			return csfWp;
		}
	}
	
//	private void requestCountryScalingFactor() throws MessageIOException, UnexpectedContentException, IllegalArgumentException{
//		CountryScalingFactorWrapper scalingFactorWrapper = device.requestCountryScalingFactor(deviceCountryCode);
//		this.deviceScalingFactor = scalingFactorWrapper.getScalingFactor();
//		this.deviceDecimalPlace = Utils.byteToUnsignedInt(scalingFactorWrapper.getDecimalPlace());
//	}
	
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
	
	/**
	 * Set the credit poll period, the interval of time between each credit when
	 * polling the device.
	 * @see #startCreditPolling()
	 * @see #stopCreditPolling()
	 * @param creditPollPeriod perido to set in ms
	 */
	public void setCreditPollPeriod(long creditPollPeriod) {
		this.creditPollPeriod = creditPollPeriod;
	}

	@Override
	public BillValidator getDevice() {
		return (BillValidator) super.getDevice();
	}

//	/**
//	 * 
//	 * @return the handled <code>Device</code> country code.
//	 */
//	public String getDeviceCountryCode() {
//		return deviceCountryCode;
//	}
//
//	/**
//	 * 
//	 * @return the handled <code>Device</code> scaling factor.
//	 */
//	public int getDeviceScalingFactor() {
//		return deviceScalingFactor;
//	}
//
//	/**
//	 * 
//	 * @return the handled <code>Device</code> scaling factor decimal place.
//	 */
//	public int getDeviceDecimalPlace() {
//		return deviceDecimalPlace;
//	}
	
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

	/**
	 * This <code>Runnable</code> is used to perform the periodical credit poll. It will
	 * perform calls to the <code>BillValidator</code> method <code>readBufferedNoteEvents()</code>
	 * and feed the result to a <code>BillEventHandler</code> which may trigger related callbacks.
	 * @author Pierre Beucher
	 *
	 */
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
