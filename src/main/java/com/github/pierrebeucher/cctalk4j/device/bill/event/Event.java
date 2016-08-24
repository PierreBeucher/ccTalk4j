package com.github.pierrebeucher.cctalk4j.device.bill.event;

/**
 * Bill event as per ccTalk specifications
 * @author Pierre Beucher
 *
 */
public enum Event {
	BILL_VALIDATED_CASHBOX,
	BILL_VALIDATED_ESCROW,
	MASTER_INHIBIT_ACTIVE,
	BILL_RETURNED_FROM_ESCROW,
	INVALID_BILL_VALIDATION_FAIL,
	INVALID_BILL_TRANSPORT_FAIL,
	INHIBITED_BILL_SERIAL,
	INHIBITED_BILL_DIP_SW,
	BILL_JAMMED_TRANSPORT_UNSAFE,
	BILL_JAMMED_STACKER,
	BILL_PULLED_BACKWARD,
	BILL_TAMPER,
	STACKER_OK,
	STACKER_REMOVED,
	STACKER_INSERTED,
	STACKER_FAULTY,
	STACKER_FULL,
	STACKER_JAMMED,
	BILL_JAMMED_TRANSPORT_SAFE,
	OPTO_FRAUD_DETECTED,
	STRING_FRAUD_DETECTED,
	ANTI_STRING_MECHANISM_FAULTY,
	BARCODE_DETECTED,
	UNKNOWN_BILL_TYPE_STACKED;
}
