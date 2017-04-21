package com.github.pierrebeucher.cctalk4j.device.bill.event;

import com.github.pierrebeucher.cctalk4j.core.Utils;

/**
 * <code>BillEvent</code> represents a single bill event returned
 * when reading buffered bill events (Header 159).
 * @author Pierre Beucher
 *
 */
public class BillEvent {
	
	public static BillEvent event(byte resultA, byte resultB) throws UnrecognizedEventException{
		int resultAInt = Utils.byteToUnsignedInt(resultA);
		int resultBInt = Utils.byteToUnsignedInt(resultB);
		Event event = null;
		EventType eventType = null;
		
		if(resultAInt > 0){
			if(resultBInt == 0){
				eventType = EventType.CREDIT;
				event = Event.BILL_VALIDATED_CASHBOX;
			} else if (resultBInt == 1){
				eventType = EventType.PENDING_CREDIT;
				event = Event.BILL_VALIDATED_ESCROW;
			} else {
				throw new UnrecognizedEventException("ResultB " + resultBInt +
						" is not recognized for resultA " + resultAInt +
						" (bill inserted).");
			}
		} else {
			switch(resultBInt){
			case 0:
				eventType = EventType.STATUS;
				event = Event.MASTER_INHIBIT_ACTIVE;
				break;
			case 1:
				eventType = EventType.STATUS;
				event = Event.BILL_RETURNED_FROM_ESCROW;
				break;
			case 2:
				eventType = EventType.REJECT;
				event = Event.INVALID_BILL_VALIDATION_FAIL;
				break;
			case 3:
				eventType = EventType.REJECT;
				event = Event.INVALID_BILL_TRANSPORT_FAIL;
				break;
			case 4:
				eventType = EventType.STATUS;
				event = Event.INHIBITED_BILL_SERIAL;
				break;
			case 5:
				eventType = EventType.STATUS;
				event = Event.INHIBITED_BILL_DIP_SW;
				break;
			case 6:
				eventType = EventType.FATAL_ERROR;
				event = Event.BILL_JAMMED_TRANSPORT_UNSAFE;
				break;
			case 7:
				eventType = EventType.FATAL_ERROR;
				event = Event.BILL_JAMMED_STACKER;
				break;
			case 8:
				eventType = EventType.FRAUD_ATTEMPT;
				event = Event.BILL_PULLED_BACKWARD;
				break;
			case 9:
				eventType = EventType.FRAUD_ATTEMPT;
				event = Event.BILL_TAMPER;
				break;
			case 10:
				eventType = EventType.STATUS;
				event = Event.STACKER_OK;
				break;
			case 11:
				eventType = EventType.STATUS;
				event = Event.STACKER_REMOVED;
				break;
			case 12:
				eventType = EventType.STATUS;
				event = Event.STACKER_INSERTED;
				break;
			case 13:
				eventType = EventType.FATAL_ERROR;
				event = Event.STACKER_FAULTY;
				break;
			case 14:
				eventType = EventType.STATUS;
				event = Event.STACKER_FULL;
				break;
			case 15:
				eventType = EventType.FATAL_ERROR;
				event = Event.STACKER_JAMMED;
				break;
			case 16:
				eventType = EventType.FATAL_ERROR;
				event = Event.BILL_JAMMED_TRANSPORT_SAFE;
				break;
			case 17:
				eventType = EventType.FRAUD_ATTEMPT;
				event = Event.OPTO_FRAUD_DETECTED;
				break;
			case 18:
				eventType = EventType.FRAUD_ATTEMPT;
				event = Event.STRING_FRAUD_DETECTED;
				break;
			case 19:
				eventType = EventType.FATAL_ERROR;
				event = Event.ANTI_STRING_MECHANISM_FAULTY;
				break;
			case 20:
				eventType = EventType.STATUS;
				event = Event.BARCODE_DETECTED;
				break;
			case 21:
				eventType = EventType.STATUS;
				event = Event.UNKNOWN_BILL_TYPE_STACKED;
				break;
			case 22:
				eventType = EventType.STATUS;
				event = Event.NOTE_JAM_CLEARED;
				break;
			default:
				throw new UnrecognizedEventException("Unrecognized event: resultA=" + resultAInt
						+ ", resultB=" + resultB);
			}
		}
	
		if(event == null || eventType == null){
			throw new UnrecognizedEventException("Incorrect event or eventType defined for "
					+ " resultA=" + resultA + ", " + "resultB=" + resultB
					+ ": event=" + event + ", eventType=" + eventType);
		}
		
		return new BillEvent(resultA, resultB, event, eventType);
	}
	
	/*
	 * event byte A
	 */
	private byte resultA;
	
	/*
	 * event byte B
	 */
	private byte resultB;
	
	private Event event;
	
	private EventType eventType;
	
	/**
	 * @param resultA
	 * @param resultB
	 * @param event
	 * @param eventType
	 */
	public BillEvent(byte resultA, byte resultB, Event event, EventType eventType) {
		super();
		if(event == null){
			throw new NullPointerException("Event cannot be null.");
		}
		
		if(eventType == null){
			throw new NullPointerException("EventType cannot be null.");
		}
		
		this.resultA = resultA;
		this.resultB = resultB;
		this.event = event;
		this.eventType = eventType;
	}

	public byte getResultA() {
		return resultA;
	}

	public byte getResultB() {
		return resultB;
	}

	public Event getEvent() {
		return event;
	}

	public EventType getEventType() {
		return eventType;
	}

	@Override
	public String toString() {
		return "[resultA=" + resultA + ", resultB=" + resultB + ", event=" + event.name() + ", eventType="
				+ eventType.name() + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		BillEvent other = (BillEvent) obj;
		if (event != other.event)
			return false;
		if (eventType != other.eventType)
			return false;
		if (resultA != other.resultA)
			return false;
		if (resultB != other.resultB)
			return false;
		return true;
	}
}
