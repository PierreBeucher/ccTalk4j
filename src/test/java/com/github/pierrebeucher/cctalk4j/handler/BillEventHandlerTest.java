package com.github.pierrebeucher.cctalk4j.handler;

import java.util.LinkedList;
import java.util.Queue;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.DeviceFactory;
import com.github.pierrebeucher.cctalk4j.device.DeviceRequestException;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;
import com.github.pierrebeucher.cctalk4j.device.bill.event.Event;
import com.github.pierrebeucher.cctalk4j.device.bill.event.EventType;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

public class BillEventHandlerTest {

	private static final BillEventBuffer emptyBuffer = new BillEventBuffer(new BillEvent[]{}, (byte)0);
	
	//a few dummy events
	private BillEvent event00 = new BillEvent((byte)0, (byte)0, Event.MASTER_INHIBIT_ACTIVE, EventType.STATUS);
	private BillEvent event10 = new BillEvent((byte)1, (byte)0, Event.BILL_VALIDATED_CASHBOX, EventType.CREDIT);
	private BillEvent event21 = new BillEvent((byte)2, (byte)1, Event.BILL_VALIDATED_ESCROW, EventType.PENDING_CREDIT);
	private BillEvent event42 = new BillEvent((byte)4, (byte)2, Event.BARCODE_DETECTED, EventType.PENDING_CREDIT);
	
	/**
	 * A test with a real device. To be mode in IT when possible.
	 * @throws MessagePortException 
	 * @throws UnexpectedContentException 
	 * @throws MessageIOException 
	 */
	//@Test
	//desactivad for the moment, consuming too much time 
	public void billEventHandlerEventCount() throws DeviceRequestException, MessagePortException{
		BillEventHandler handler = new BillEventHandler(null);
		handler.initEventBufferQueue(emptyBuffer);
		BillValidator validator = DeviceFactory.billValidatorSerialCRC("COM6", (byte)40);
		try{
			validator.connect();
			handler.feed(validator.readBufferedNoteEvents());
			int counterBefore = handler.getCurrentEventCounter();
			
			//fill event buffer...
			int eventCountToAdd = 3;
			for(int i=0; i<=eventCountToAdd; i++){
				validator.modifyMasterInhibitStatus(false);
				validator.modifyMasterInhibitStatus(true);
			}
			
			handler.feed(validator.readBufferedNoteEvents());
			Assert.assertEquals(handler.getCurrentEventCounter(), counterBefore + eventCountToAdd);
		} finally {
			validator.disconnect();
		}
	}
	
	@Test
	public void calculateEventCounterDiff_nominal() {
		byte currentCounter = 40;
		byte newCounter = 42;
		byte expected = 2;
		Assert.assertEquals(BillEventHandler.calculateEventCounterDiff(currentCounter, newCounter), expected);
	}
	
	@Test
	public void calculateEventCounterDiff_java_negative() {
		byte expected = 4;
		byte currentCounter = 127;
		byte newCounter = -125;
		Assert.assertEquals(BillEventHandler.calculateEventCounterDiff(currentCounter, newCounter), expected);
	}
	
	@Test
	public void calculateEventCounterDiff_counter_reset_1() {
		//simulate a counter reset 255 > 1 (+1) 
		byte expected = 1;
		byte currentCounter = Utils.unsignedIntToByte(255);
		byte newCounter = Utils.unsignedIntToByte(1);
		Assert.assertEquals(BillEventHandler.calculateEventCounterDiff(currentCounter, newCounter), expected);
	}
	
	@Test
	public void calculateEventCounterDiff_counter_reset_3() {
		//simulate a counter reset 255 > 3 (+3)
		byte expected = 3;
		byte currentCounter = Utils.unsignedIntToByte(255);
		byte newCounter = Utils.unsignedIntToByte(3);
		Assert.assertEquals(BillEventHandler.calculateEventCounterDiff(currentCounter, newCounter), expected);
	}

	@Test
	public void feed_nominal() {
		BillEventHandler handler = new BillEventHandler(null);
		handler.initEventBufferQueue(emptyBuffer);
		
		BillEventBuffer buf = new BillEventBuffer(new BillEvent[]{event00, event10, event00, event00, event00}, (byte)2);
		handler.feed(buf);
		
		Assert.assertEquals(handler.getCurrentEventCounter(), 2);
	}
	
	@Test
	public void feed_deque_recycling_atomic() {
		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event10, event21, event42, event10, event00}, (byte)1);
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event42, event10, event21, event42, event10}, (byte)2);
		BillEventBuffer buf3 = new BillEventBuffer(new BillEvent[]{event00, event42, event10, event21, event42}, (byte)3);
		BillEventHandler handler = new BillEventHandler(null, 1); //dequeue with 1 capacity
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
		handler.feed(buf3);
		Assert.assertEquals(handler.getEventBufferDeque().peekFirst(), buf3);
		Assert.assertEquals(handler.getEventBufferDeque().peekLast(), buf3);
	}
	
	@Test
	public void feed_deque_recycling_2() {
		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event10, event21, event42, event10, event00}, (byte)1);
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event42, event10, event21, event42, event10}, (byte)2);
		BillEventBuffer buf3 = new BillEventBuffer(new BillEvent[]{event00, event42, event10, event21, event42}, (byte)3);
		BillEventHandler handler = new BillEventHandler(null, 2);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
		handler.feed(buf3);
		Assert.assertEquals(handler.getEventBufferDeque().peekFirst(), buf2);
		Assert.assertEquals(handler.getEventBufferDeque().peekLast(), buf3);
	}
	
	@Test
	public void feed_deque_recycling_3() {
		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event10, event21, event42, event10, event00}, (byte)1);
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event42, event10, event21, event42, event10}, (byte)2);
		BillEventBuffer buf3 = new BillEventBuffer(new BillEvent[]{event00, event42, event10, event21, event42}, (byte)3);
		BillEventHandler handler = new BillEventHandler(null, 3);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
		handler.feed(buf3);
		Assert.assertEquals(handler.getEventBufferDeque().peekFirst(), buf1);
		Assert.assertEquals(handler.getEventBufferDeque().peekLast(), buf3);
	}
	
	@Test
	public void getCurrentEventCounter_onInstanciation() {
		BillEventHandler handler = new BillEventHandler(null);
		Assert.assertEquals(handler.getCurrentEventCounter(), 0);
	}
	
	@Test
	public void getCurrentEventCounter_onFeed() {
		byte counter = 3;
		BillEventBuffer buf = new BillEventBuffer(new BillEvent[]{event00, event10}, counter);
		BillEventHandler handler = new BillEventHandler(null);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf);
		
		Assert.assertEquals(handler.getCurrentEventCounter(), counter);
	}

	@Test
	public void getEventBufferDeque() {
		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event00}, (byte)1);
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event21, event10, event00}, (byte)3);
		BillEventHandler handler = new BillEventHandler(null);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
		
		Assert.assertEquals(handler.getEventBufferDeque().poll(), emptyBuffer);
		Assert.assertEquals(handler.getEventBufferDeque().poll(), buf1);
		Assert.assertEquals(handler.getEventBufferDeque().poll(), buf2);
	}

	@Test
	public void handleEvent_nominal_simple() {
		//event are expected to be treated in this order
		Queue<BillEvent> expected = new LinkedList<BillEvent>();
		expected.add(event00);
		expected.add(event10);
		expected.add(event21);

		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event00}, (byte)1);
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event21, event10, event00}, (byte)3);
		BillEventHandler handler = new BillEventHandler(null);
		TestBillEventListener listener = new TestBillEventListener();
		handler.addListener(listener);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
		
		Assert.assertEquals(listener.getNotifiedEvents(), expected);
	}
	
	@Test
	public void handleEvent_nominal_complex() {
		//event are expected to be treated in this order
		Queue<BillEvent> expected = new LinkedList<BillEvent>();
		expected.add(event00);//1:1
		expected.add(event10);
		expected.add(event21);//2:3
		expected.add(event42);
		expected.add(event00);
		expected.add(event00);
		expected.add(event42);
		expected.add(event42);//3:8
		expected.add(event42);
		expected.add(event42);
		expected.add(event42);
		expected.add(event42);
		expected.add(event42);//4:13
		expected.add(event42);
		expected.add(event42);//5:15
		expected.add(event21);//6:16
		expected.add(event10);
		expected.add(event10);
		expected.add(event21);//7:19
		
		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event00, event00, event00, event00, event00}, (byte)1); //1
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event21, event10, event00, event00, event00}, (byte)3); //2
		BillEventBuffer buf3 = new BillEventBuffer(new BillEvent[]{event42, event42, event00, event00, event42}, (byte)8); //3
		BillEventBuffer buf4 = new BillEventBuffer(new BillEvent[]{event42, event42, event42, event42, event42}, (byte)13); //4
		BillEventBuffer buf5 = new BillEventBuffer(new BillEvent[]{event42, event42, event42, event42, event42}, (byte)15); //5
		BillEventBuffer buf6 = new BillEventBuffer(new BillEvent[]{event21, event42, event42, event42, event42}, (byte)16); //6
		BillEventBuffer buf7 = new BillEventBuffer(new BillEvent[]{event21, event10, event10, event21, event42}, (byte)19); //7
		BillEventHandler handler = new BillEventHandler(null);
		TestBillEventListener listener = new TestBillEventListener();
		handler.addListener(listener);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
		handler.feed(buf3);
		handler.feed(buf4);
		handler.feed(buf5);
		handler.feed(buf6);
		handler.feed(buf7);
		
		Assert.assertEquals(listener.getNotifiedEvents(), expected);
	}
	
	@Test
	public void handleEvent_afterCounterReset_simple() {
		//event are expected to be treated in this order
		Queue<BillEvent> expected = new LinkedList<BillEvent>();
		expected.add(event00);
		expected.add(event10);
		expected.add(event21);
		expected.add(event10);
		expected.add(event42);
		expected.add(event21);
		
		//simulate a counter reset 255 > 1
		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event42, event10, event21, event10, event00}, Utils.unsignedIntToByte(255));
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event21, event42, event10, event21, event10}, Utils.unsignedIntToByte(1));
		BillEventHandler handler = new BillEventHandler(null);
		TestBillEventListener listener = new TestBillEventListener();
		handler.addListener(listener);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
	
		Assert.assertEquals(listener.getNotifiedEvents(), expected);
	}
	
	@Test
	public void handleEvent_afterCounterReset_complex() {
		//event are expected to be treated in this order
		Queue<BillEvent> expected = new LinkedList<BillEvent>();
		expected.add(event21);
		expected.add(event10);
		expected.add(event00);
		expected.add(event10);
		expected.add(event42);//1:250
		expected.add(event10);
		expected.add(event21);
		expected.add(event10);//2:253
		expected.add(event42);
		expected.add(event21);
		expected.add(event42);
		expected.add(event42);
		expected.add(event00);//3:3
		expected.add(event00);
		expected.add(event21);//4:5
		
		//simulate a counter reset 255 > 1
		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event42, event10, event00, event10, event21}, Utils.unsignedIntToByte(250)); //1
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event10, event21, event10, event42, event00}, Utils.unsignedIntToByte(253)); //2
		BillEventBuffer buf3 = new BillEventBuffer(new BillEvent[]{event00, event42, event42, event21, event42}, Utils.unsignedIntToByte(3)); //3
		BillEventBuffer buf4 = new BillEventBuffer(new BillEvent[]{event21, event00, event00, event42, event42}, Utils.unsignedIntToByte(5)); //4
		BillEventHandler handler = new BillEventHandler(null);
		TestBillEventListener listener = new TestBillEventListener();
		handler.addListener(listener);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
		handler.feed(buf3);
		handler.feed(buf4);
	
		Assert.assertEquals(listener.getNotifiedEvents(), expected);
	}
	
	@Test
	public void notifyEvent(){
		
	}

	@Test
	public void notifyLostEvent() {
		BillEventBuffer buf1 = new BillEventBuffer(new BillEvent[]{event00, event00, event00, event00, event00}, (byte)1);
		BillEventBuffer buf2 = new BillEventBuffer(new BillEvent[]{event21, event10, event00, event21, event21}, (byte)7);
		
		//this should trigger a lost event callback
		BillEventHandler handler = new BillEventHandler(null);
		TestBillEventListener listener = new TestBillEventListener();
		handler.addListener(listener);
		handler.initEventBufferQueue(emptyBuffer);
		handler.feed(buf1);
		handler.feed(buf2);
		
		Assert.assertEquals(listener.getLostEventPreviousBuffer(), buf1);
		Assert.assertEquals(listener.getLostEventNewBuffer(), buf2);
		Assert.assertEquals(listener.getLostEventCount(), (Integer)1);
	}
	
	@Test
	public void getBufferDequeMaxSize_default(){
		BillEventHandler handler = new BillEventHandler(null);
		Assert.assertEquals(handler.getEventBufferQueueMaxSize(), BillEventHandler.DEFAULT_MAX_EVENT_BUFFER_DEQUE_SIZE);
	}
	
	@Test
	public void getBufferDequeMaxSize_constructorParam(){
		int size = 4242;
		BillEventHandler handler = new BillEventHandler(null, size);
		Assert.assertEquals(handler.getEventBufferQueueMaxSize(),size);
	}
	
	@Test
	public void initEventBufferQueue_init_true(){
		BillEventHandler handler = new BillEventHandler(null);
		handler.initEventBufferQueue(emptyBuffer);
		Assert.assertEquals(handler.isEventBufferQueueInitialised(), true);
	}
	
	@Test
	public void initEventBufferQueue_init_false(){
		BillEventHandler handler = new BillEventHandler(null);
		Assert.assertEquals(handler.isEventBufferQueueInitialised(), false);
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void initEventBufferQueue_feed_not_init(){
		BillEventHandler handler = new BillEventHandler(null);
		handler.feed(new BillEventBuffer(new BillEvent[]{event21, event10, event00, event21, event21}, (byte)7));
	}
	
	private class TestBillEventListener implements BillEventListener{
		
		//every new event is added here
		private Queue<BillEvent> notifiedEvents = new LinkedList<BillEvent>();
		
		//those are set once when lostEvent() is called
		private BillEventBuffer lostEventPreviousBuffer;
		private BillEventBuffer lostEventNewBuffer;
		private Integer lostEventCount;
		
		@Override
		public void newEvent(BillValidatorHandler handler, BillEvent event) {
			this.notifiedEvents.add(event);
		}
		
		@Override
		public void lostEvent(int lostEventCount, BillEventBuffer previousBuffer, BillEventBuffer newBuffer) {
			this.lostEventPreviousBuffer = previousBuffer;
			this.lostEventNewBuffer = newBuffer;
			this.lostEventCount = lostEventCount;
		}
		
		public Queue<BillEvent> getNotifiedEvents() {
			return this.notifiedEvents;
		}

		public BillEventBuffer getLostEventPreviousBuffer() {
			return lostEventPreviousBuffer;
		}

		public BillEventBuffer getLostEventNewBuffer() {
			return lostEventNewBuffer;
		}

		public Integer getLostEventCount() {
			return lostEventCount;
		}
	};
}
