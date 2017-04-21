package com.github.pierrebeucher.cctalk4j.device.bill.event;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BillEventTest {

	private byte resultA = 0;
	private byte resultB = 42;
	private Event event = Event.BARCODE_DETECTED;
	private EventType eventType = EventType.CREDIT;
	private BillEvent e1 = new BillEvent(resultA, resultB, event, eventType);
	
	private BillEvent e2_resultA = new BillEvent((byte)1, resultB, event, eventType);
	private BillEvent e3_resultB = new BillEvent(resultA, (byte)55, event, eventType);
	private BillEvent e4_event = new BillEvent(resultA, resultB, Event.BILL_TAMPER, eventType);
	private BillEvent e5_eventType = new BillEvent(resultA, resultB, event, EventType.STATUS);
	
	private void _event_test(byte resultA, byte resultB, Event event, EventType eventType) throws UnrecognizedEventException{
		BillEvent actual = BillEvent.event(resultA, resultB);
		BillEvent expected = new BillEvent(resultA, resultB, event, eventType);
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void event_credit_1_0() throws UnrecognizedEventException {
		_event_test((byte)1, (byte)0, Event.BILL_VALIDATED_CASHBOX, EventType.CREDIT);
	}
	
	@Test
	public void event_credit_255_0() throws UnrecognizedEventException {
		_event_test((byte)-127, (byte)0, Event.BILL_VALIDATED_CASHBOX, EventType.CREDIT);
	}
	
	@Test
	public void event_credit_1_1() throws UnrecognizedEventException {
		_event_test((byte)1, (byte)1, Event.BILL_VALIDATED_ESCROW, EventType.PENDING_CREDIT);
	}
	
	@Test
	public void event_credit_255_1() throws UnrecognizedEventException {
		_event_test((byte)-127, (byte)1, Event.BILL_VALIDATED_ESCROW, EventType.PENDING_CREDIT);
	}
	
	@Test
	public void event_master_inhibit() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)0, Event.MASTER_INHIBIT_ACTIVE, EventType.STATUS);
	}
	
	@Test
	public void event_bill_returned_escrow() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)1, Event.BILL_RETURNED_FROM_ESCROW, EventType.STATUS);
	}
	
	@Test
	public void event_invalid_bill_validation() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)2, Event.INVALID_BILL_VALIDATION_FAIL, EventType.REJECT);
	}
	
	@Test
	public void event_invalid_bill_transport() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)3, Event.INVALID_BILL_TRANSPORT_FAIL, EventType.REJECT);
	}
	
	@Test
	public void event_inhibited_bill_serial() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)4, Event.INHIBITED_BILL_SERIAL, EventType.STATUS);
	}
	
	@Test
	public void event_inhibited_bill_dip_sw() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)5, Event.INHIBITED_BILL_DIP_SW, EventType.STATUS);
	}
	
	@Test
	public void event_bill_jammed_transport_unsafe() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)6, Event.BILL_JAMMED_TRANSPORT_UNSAFE, EventType.FATAL_ERROR);
	}
	
	@Test
	public void event_bill_jammed_stacker() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)7, Event.BILL_JAMMED_STACKER, EventType.FATAL_ERROR);
	}
	
	@Test
	public void event_bill_pulled() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)8, Event.BILL_PULLED_BACKWARD, EventType.FRAUD_ATTEMPT);
	}
	
	@Test
	public void event_bill_tamper() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)9, Event.BILL_TAMPER, EventType.FRAUD_ATTEMPT);
	}
	
	@Test
	public void event_stacker_ok() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)10, Event.STACKER_OK, EventType.STATUS);
	}
	
	@Test
	public void event_stacker_removed() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)11, Event.STACKER_REMOVED, EventType.STATUS);
	}
	
	@Test
	public void event_stacker_inserted() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)12, Event.STACKER_INSERTED, EventType.STATUS);
	}
	
	@Test
	public void event_stacker_faulty() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)13, Event.STACKER_FAULTY, EventType.FATAL_ERROR);
	}
	
	@Test
	public void event_stacker_full() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)14, Event.STACKER_FULL, EventType.STATUS);
	}
	
	@Test
	public void event_stacker_jammed() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)15, Event.STACKER_JAMMED, EventType.FATAL_ERROR);
	}
	
	@Test
	public void event_bill_jammed_transport_safe() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)16, Event.BILL_JAMMED_TRANSPORT_SAFE, EventType.FATAL_ERROR);
	}
	
	@Test
	public void event_opto_fraud() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)17, Event.OPTO_FRAUD_DETECTED, EventType.FRAUD_ATTEMPT);
	}
	
	@Test
	public void event_string_fraud() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)18, Event.STRING_FRAUD_DETECTED, EventType.FRAUD_ATTEMPT);
	}
	
	@Test
	public void event_anti_string_mechanism_fault() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)19, Event.ANTI_STRING_MECHANISM_FAULTY, EventType.FATAL_ERROR);
	}
	
	@Test
	public void event_barcode_detected() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)20, Event.BARCODE_DETECTED, EventType.STATUS);
	}
	
	@Test
	public void event_unknown_bill_type_stacked() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)21, Event.UNKNOWN_BILL_TYPE_STACKED, EventType.STATUS);
	}
	
	@Test
	public void event_note_jam_cleared() throws UnrecognizedEventException {
		_event_test((byte)0, (byte)22, Event.NOTE_JAM_CLEARED, EventType.STATUS);
	}
	
	@Test
	public void equals_true() {
		BillEvent e1 = new BillEvent(resultA, resultB, event, eventType);
		BillEvent e2 = new BillEvent(resultA, resultB, event, eventType);
		Assert.assertEquals(e1, e2);
	}
	
	private void _equals_false(BillEvent a, BillEvent b){
		Assert.assertNotEquals(a, b);
	}
	
	@Test
	public void equals_false_resultA() {
		_equals_false(e1, e2_resultA);
	}
	
	@Test
	public void equals_false_resultB() {
		_equals_false(e1, e3_resultB);
	}
	
	@Test
	public void equals_false_event() {
		_equals_false(e1, e4_event);
	}
	
	@Test
	public void equals_false_eventType() {
		_equals_false(e1, e5_eventType);
	}

	@Test
	public void getEvent() {
		Assert.assertEquals(e1.getEvent(), event);
	}

	@Test
	public void getEventType() {
		Assert.assertEquals(e1.getEventType(), eventType);
	}

	@Test
	public void getResultA() {
		Assert.assertEquals(e1.getResultA(), resultA);
	}

	@Test
	public void getResultB() {
		Assert.assertEquals(e1.getResultB(), resultB);
	}
}
