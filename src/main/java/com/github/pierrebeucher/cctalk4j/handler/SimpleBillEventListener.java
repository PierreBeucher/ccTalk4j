package com.github.pierrebeucher.cctalk4j.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.device.bill.Bill;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillRoutingException;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

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
public class SimpleBillEventListener implements BillEventListener {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void newEvent(BillValidatorHandler handler, BillEvent e) {
		logger.info("New event: {}", e);
		
		switch(e.getEventType()){
		case PENDING_CREDIT:
			pendingCredit(handler, e);
			break;
		case CREDIT:
			credit(handler, e);
			break;
		case REJECT:
			reject(handler, e);
			break;
		case STATUS:
			status(handler, e);
			break;
		case FRAUD_ATTEMPT:
			fraudAttempt(handler, e);
			break;
		case FATAL_ERROR:
			fatalError(handler, e);
			break;
		default:
			logger.error("Unknown event type for {} : {}", e, e.getEventType());
		}
	}
	
	/**
	 * Called when a Pending Credit event is read.
	 * @param validator validator which generated the event 
	 * @param e raw event read
	 * @param bill bill described by the event
	 */
	public void pendingCredit(BillValidatorHandler handler, BillEvent event){
		try {
			logger.info("Bill in escrow. Accepting.");
			handler.getDevice().routeBill(BillValidator.ROUTE_CODE_SEND_BILL_CASHBOX_STACKER);
		} catch (BillRoutingException | MessageIOException | UnexpectedContentException e) {
			logger.error("Cannot route bill.", e);
		}
	}
	
	/**
	 * Called when a Credit event is read.
	 * @param e event read
	 */
	public void credit(BillValidatorHandler handler, BillEvent e){
		logger.info("Bill stacked {}", e);
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
}

