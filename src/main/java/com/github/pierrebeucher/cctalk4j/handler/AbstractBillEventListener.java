package com.github.pierrebeucher.cctalk4j.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.device.bill.Bill;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;

public abstract class AbstractBillEventListener implements BillEventListener {

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
	
	protected Bill generateBillFromBillEvent(BillValidatorHandler handler, BillEvent event) {
		byte billType = event.getResultA();
		Bill bill = handler.getBillMap().get(billType);
		if(bill == null){
			
		}
		return bill;
	}

	@Override
	public abstract void lostEvent(int lostEventCount, BillEventBuffer previousBuffer, BillEventBuffer newBuffer);
	
	/**
	 * Called when a Pending Credit event is read.
	 * @param validator validator which generated the event 
	 * @param e raw event read
	 * @param bill bill described by the event
	 */
	public abstract void pendingCredit(BillValidatorHandler handler, BillEvent event, Bill bill);
	
	/**
	 * Called when a Credit event is read.
	 * @param e event read
	 */
	public abstract void credit(BillValidatorHandler handler, BillEvent e, Bill bill);
	
	/**
	 * Called when a Reject event is read.
	 * @param e event read
	 */
	public abstract  void reject(BillValidatorHandler handler, BillEvent e);
	
	/**
	 * Called when a Fraud Attempt event is read.
	 * @param e event read
	 */
	public abstract void fraudAttempt(BillValidatorHandler handler, BillEvent e);
	
	/**
	 * Called when a Status event is read.
	 * @param e event read
	 */
	public abstract  void status(BillValidatorHandler handler, BillEvent e);
	
	/**
	 * Called when a Fatal Error event is read.
	 * @param e event read
	 */
	public abstract  void fatalError(BillValidatorHandler handler, BillEvent e);
	
	/**
	 * Called when a pendingCredit() or credit() cannot associate a Bill from
	 * the received BillEvent.
	 */
	public abstract void unknownBill(BillValidatorHandler handler, BillEvent e);
}
