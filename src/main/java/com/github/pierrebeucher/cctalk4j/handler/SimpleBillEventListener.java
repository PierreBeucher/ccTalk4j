package com.github.pierrebeucher.cctalk4j.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.core.CcTalkException;
import com.github.pierrebeucher.cctalk4j.device.DeviceRequestException;
import com.github.pierrebeucher.cctalk4j.device.bill.Bill;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillRoutingException;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;

/**
 * <p>Simple implementation of <code>BillEventListener</code>,
 * providing callbacks methods for each event types. This
 * implementation should not be used as-is, but provide basic
 * examples of how to extends <code>BillEventListener</code> 
 * and may be extended for more advanced usages. </p>
 * <p>
 * The current implementation provides various callbacks methods,
 * each having a default behavior implemented:
 * <ul>
 * <li>{@link #pendingCredit(BillEvent)} - Called upon read of a Pending Credit event. 
 * Default implementation will send the note to cashbox/stacker. </li>
 * <li>{@link #credit(BillEvent)} - Called upon read of a Credit event.
 * Default implementation simply log the event.
 * and calls {@link #paymentReceived(Bill, double)}. </li>
 * <li> {@link #fraudAttempt(BillEvent)} - Called upon read of a Fraud Attempt event.
 * Default implementation does nothing. </li>
 * <li> {@link #reject(BillEvent)} - Called upon read of a Reject event.
 * Default implementation does nothing. </li>
 * <li> {@link #status(BillEvent)} - Called upon read of a Status event.
 * Default implementation does nothing. </li>
 * <li> {@link #fatalError(BillEvent)} - Called upon read of a Fatal Error event.
 * Default implementation does nothing. </li>
 * </ul>
 * Implements empty callbacks methods which may
 * be overriden. 
 * @author Pierre Beucher
 *
 */
public class SimpleBillEventListener extends AbstractBillEventListener {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void newEvent(BillValidatorHandler handler, BillEvent event) {
		logger.info("New event: {}", event);
		switch(event.getEventType()){
		case PENDING_CREDIT:
			pendingCredit(handler, event, generateBillFromBillEvent(handler, event));
			break;
		case CREDIT:
			credit(handler, event, generateBillFromBillEvent(handler, event));
			break;
		case REJECT:
			reject(handler, event);
			break;
		case STATUS:
			status(handler, event);
			break;
		case FRAUD_ATTEMPT:
			fraudAttempt(handler, event);
			break;
		case FATAL_ERROR:
			fatalError(handler, event);
			break;
		default:
			logger.error("Unknown event type for {} : {}", event, event.getEventType());
		}
	}
	
	/**
	 * Called when a Pending Credit event is read.
	 * @param validator validator which generated the event 
	 * @param e raw event read
	 * @param bill bill described by the event
	 */
	public void pendingCredit(BillValidatorHandler handler, BillEvent event, Bill bill) {
			logger.info("Bill in escrow: {}. Accepting.", bill);
			try {
				handler.getDevice().requestProductCode();
				handler.getDevice().routeBill(BillValidator.ROUTE_CODE_SEND_BILL_CASHBOX_STACKER);
			} catch (DeviceRequestException | BillRoutingException e) {
				logger.error("Cannot route bill: {}", e);
			}
	}
	
	/**
	 * Called when a Credit event is read.
	 * @param e event read
	 */
	public void credit(BillValidatorHandler handler, BillEvent e, Bill bill) {
		logger.info("Bill stacked: {}", bill);
	}
	
	/**
	 * Called when a Reject event is read.
	 * @param e event read
	 */
	public void reject(BillValidatorHandler handler, BillEvent e) {
	}
	
	/**
	 * Called when a Fraud Attempt event is read.
	 * @param e event read
	 */
	public void fraudAttempt(BillValidatorHandler handler, BillEvent e) {
	}
	
	/**
	 * Called when a Status event is read.
	 * @param e event read
	 */
	public void status(BillValidatorHandler handler, BillEvent e) {
	}
	
	/**
	 * Called when a Fatal Error event is read.
	 * @param e event read
	 */
	public void fatalError(BillValidatorHandler handler, BillEvent e) {
	}
	
	@Override
	public void lostEvent(int lostEventCount, BillEventBuffer previousBuffer, BillEventBuffer newBuffer) {
		
	}
	
	/**
	 * Called when an unhandled error is detected. 
	 * //TODO handle events such as routing failure (stcker full) and other things... 
	 * @param handler
	 * @param e
	 */
	public void unhandledCcTalkException(BillValidatorHandler handler, BillEvent event, CcTalkException error){
		
	}

	@Override
	public void unknownBill(BillValidatorHandler handler, BillEvent e) {
		
	}
}

