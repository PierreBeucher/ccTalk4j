package com.github.pierrebeucher.cctalk4j.device.bill.event;

/**
 * Event types as per ccTalk specifications
 * @author Pierre Beucher
 *
 */
public enum EventType {
	CREDIT,
	PENDING_CREDIT,
	STATUS,
	REJECT,
	FATAL_ERROR,
	FRAUD_ATTEMPT;
}
