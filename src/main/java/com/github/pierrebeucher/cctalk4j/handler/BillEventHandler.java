package com.github.pierrebeucher.cctalk4j.handler;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer;

/**
 * <p><code>BillEventHandler</code> is used to handle bill event buffers.
 * This class uses callbacks functions {@link #notifyNewEvent(BillEvent)}
 * and {@link #handleLostEvent(BillEventBuffer, BillEventBuffer)}
 * to monitor incoming events, fed using {@link #feed(BillEventBuffer)}.</p>
 * 
 * <p>Any <code>BillEventBuffer</code> fed is saved into a Deque of a limited
 * size (minimum size: 1). This <code>Deque</code> has two roles: mainly,
 * keep the previous event counter to compare it with a buffer event count and
 * deduce new, old and lost events. It can also be used to keep a more complete
 * history of encountered events. This <code>Deque</code> is automatically recycled
 * once its maximal size is reached, with older <code>BillEventBuffer</code> being
 * removed.</p>
 * 
 * <p>Example considering the following <code>BillEventBuffer</code> instances:
 * <pre>{@code
 * //eventA is the oldest event, eventC the most recent
 * buf1 = {counter=3| eventC, eventB, eventA}
 * buf2 = {counter=3| eventC, eventB, eventA}
 * buf3 = {counter=5| eventE, eventD, eventC, eventB, eventA}
 * }</pre>
 * Then calling:
 * <pre>
 * {@code
 * 	BillEventHandler handler = new SomeBillEventHandler();
 * 	handler.feed(buf1);
 * 	handler.feed(buf2);
 * 	handler.feed(buf3);
 * }</pre>
 * 
 * <code>handler.feed(buf1)</code> will trigger calls to {@link #notifyNewEvent(BillEvent)}
 * for eventA, eventB and eventC (in this order), <code>handler.feed(buf2)</code>
 * will not trigger any callback as there is no new events, and finally
 * <code>handler.feed(buf3)</code> will trigger calls to {@link #notifyNewEvent(BillEvent)}
 * with eventD and eventE. <p>
 * <p>Similarly, with something like this, <here the event counter between buf1 and buf2
 * is too large for a single <code>BillEventBuffer</code>: 
 * <pre>{@code
 * buf1 = {<u>counter=1</u>| events...}
 * buf2 = {<u>counter=7</u>| other events...}
 * }</pre>
 * Calling:
 * <pre>
 * {@code
 * 	BillEventHandler handler = new SomeBillEventHandler();
 * 	handler.feed(buf1);
 * 	handler.feed(buf2);
 * }</pre> 
 * Will trigger a call to {@link #handleLostEvent(BillEventBuffer, BillEventBuffer)}, as too much events
 * has been added (6 new events), more that can be held in memory: some events has ben lost.
 * </p>
 * @author Pierre Beucher
 *
 */
class BillEventHandler {
	
	/**
	 * Empty bill event array returned by {@link #extractNewEvents(BillEventBuffer)} when
	 * no new events available 
	 */
	private static final BillEvent[] EMPTY_BILL_EVENT_ARRAY = new BillEvent[]{};
	
	/**
	 * Default maximum size of the event buffer deque.
	 */
	public static int DEFAULT_MAX_EVENT_BUFFER_DEQUE_SIZE = 50;
	
	public static int calculateEventCounterDiff(byte previousCounter, byte newCounter){
		int newCounterInt = Utils.byteToUnsignedInt(newCounter);
		int previousCounterInt = Utils.byteToUnsignedInt(previousCounter);
		if(newCounterInt < previousCounterInt){
			//counter reset
			return (newCounterInt + 255) - previousCounterInt;
		} else {
			//no counter reset
			return newCounterInt - previousCounterInt;
		}
	}
	
	/*
	 * Deque keeping fed BillEventBuffers until a certain limit
	 */
	private Deque<BillEventBuffer> eventBufferDeque;
	
	/*
	 * Maximum number of event buffers to keep in the deque
	 */
	private int eventBufferDequeMaxSize;
	
	/*
	 * Collection of listeners to notify
	 */
	private Collection<BillEventListener> eventListeners;
	
	/*
	 * Validator handler for which this event handler works 
	 */
	private BillValidatorHandler handler;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Creates a new <code>BillEventHandler</code> working for the given <code>DeviceHandler</code>,
	 * and with the given <code>Deque</code> size is
	 * @param handler handler this event handler is working for
	 * @param bufferDequeMaxSize maximum size of the internal <code>Deque</code>
	 */
	public BillEventHandler(BillValidatorHandler handler, int bufferDequeMaxSize) {
		super();
		this.handler = handler;
		this.eventBufferDequeMaxSize = bufferDequeMaxSize;
		this.eventBufferDeque = new LinkedBlockingDeque<BillEventBuffer>(bufferDequeMaxSize);
		this.eventListeners = new ArrayList<BillEventListener>();
	}
	
	/**
	 * Creates a new <code>BillEventHandler</code> working for the given <code>DeviceHandler</code>,
	 * for which the internal <code>Deque</code> size is {@link #DEFAULT_MAX_EVENT_BUFFER_DEQUE_SIZE}
	 * @param handler handler this event handler is working for
	 */
	public BillEventHandler(BillValidatorHandler handler) {
		this(handler, DEFAULT_MAX_EVENT_BUFFER_DEQUE_SIZE);
	}
	
	/**
	 * <p>Feed a new <code>BillEventBuffer</code> to this <code>BillEventHandler</code>.
	 * By feeding an event buffer, the handler will check for any new event by comparing
	 * the currentEventCounter with the eventCounter held in the given buffer. If any
	 * new event has been added, {@link #notifyNewEvent(BillEvent)} will be called for each new events,
	 * in the order in which they appeared. Once any new events has been handled and any callback
	 * triggered, the new eventBuffer is stacked in an internal <code>Stack</code>.</p>
	 * 
	 * <p>Note: if the difference between the currentEventCounter and the counter in the given
	 * eventBuffer is greater than <code>BillEventBuffer.EVENT_BUFFER_SIZE</code>, it is deduced
	 * than too much events has been generated since the last time this function was called,
	 * meaning some events has been lost, resulting in {@link #handleLostEvent()} being called.
	 * (a <code>BillEventBuffer</code> cannot hold more than {@link com.github.pierrebeucher.cctalk4j.device.bill.event.BillEventBuffer#EVENT_BUFFER_SIZE},
	 * thus if too much events are added without calling this function, the older events are lost). </p>
	 * 
	 * @param eventBuffer new event buffer to feed
	 */
	public void feed(BillEventBuffer eventBuffer){
		logger.debug("New BillEventBuffer fed: {} - previous buffer: {}", eventBuffer, peekPreviousEventBuffer());
		BillEvent[] events = extractNewEvents(eventBuffer);
		
		if(events.length == 0){
			logger.debug("No new events to add for {}", eventBuffer);
			return;
		}
		logger.debug("Adding {} new events: {}", events.length, (Object[])events);
	
		//older events are to be processed first
		//older events are at the end of the array
		for(int i=events.length-1; i>=0; i--){
			notifyNewEvent(events[i]);
		}
		pushEventBuffer(eventBuffer);
	}
	
	/**
	 * Add a <code>BillEventListener</code> to this <code>BillEventHandler</code>.
	 * The added listener will be notified of any new or lost event. 
	 * @param listener listener to add
	 */
	public void addListener(BillEventListener listener){
		eventListeners.add(listener);
	}
	
	/**
	 * 
	 * @return the previous event buffer, or null if there are none
	 */
	protected BillEventBuffer peekPreviousEventBuffer(){
		return this.eventBufferDeque.peekLast();
	}
	
	/**
	 * 
	 * @return the most recent buffer event counter, or 0 if no event buffer fed yet
	 */
	protected byte peekPreviousEventCounter(){
		return this.eventBufferDeque.size() == 0 ?  0 : this.eventBufferDeque.peekLast().getEventCounter();
	}
	
	/**
	 * Push a new event buffer into the Deque, and
	 * remove older buffers if necessary (Deque full)
	 * @param eventBuffer buffer to push
	 */
	private void pushEventBuffer(BillEventBuffer eventBuffer){
		//pop if queue full
		if(this.eventBufferDeque.size() == this.eventBufferDequeMaxSize) {
			this.eventBufferDeque.pop();
		}
		this.eventBufferDeque.add(eventBuffer);
	}
	
	/**
	 * Extract new events from the given <code>BillEventBuffer</code>.
	 * Log an error and call {@link #handleLostEvent()} if events has been lost.
	 * @param eventBuffer new event buffer
	 * @return {@link #EMPTY_BILL_EVENT_ARRAY} if the currentEventCounter is the same as the given eventBuffer value, or any new events found.
	 */
	private BillEvent[] extractNewEvents(BillEventBuffer newBuffer){
		int eventCounterDiff = calculateEventCounterDiff(peekPreviousEventCounter(), newBuffer.getEventCounter());
		
		//if buffer counter same as current counter, no new events.
		if(eventCounterDiff == 0){
			return EMPTY_BILL_EVENT_ARRAY;
		}
			
		//check no events has been lost 
		//events lost if the current eventCounter is not 0 (meaning no event received yet)
		//and the eventCounterDiff is too large 
		if(this.peekPreviousEventCounter() != 0 &&
				eventCounterDiff > BillEventBuffer.EVENT_BUFFER_SIZE){
			BillEventBuffer previousBuffer = this.peekPreviousEventBuffer();
			int lostEventCount = eventCounterDiff - BillEventBuffer.EVENT_BUFFER_SIZE;
			notifyLostEvent(lostEventCount, previousBuffer, newBuffer);
		}
		
		//extract most recent events
		//do not extract more than the entire event array
		return Arrays.copyOfRange(newBuffer.getBillEvents(), 0, Math.min(eventCounterDiff, newBuffer.getBillEvents().length));
	}
	
	/**
	 * <p>This function is called when a fed <code>BillEventBuffer</code> contains new events,
	 * and the new event counter indicates that more events has been added since last call to {@link #feed(BillEventBuffer)}
	 * that can been held into a single <code>BillEventBuffer</code>. This means some events were never
	 * fed, thus being lost. </p>
	 * <p>e.g <code>calculateEventCounterDiff(this.getEventCounter(), newEventBuffer.getEventCounter()) > BillEventBuffer.EVENT_BUFFER_SIZE</code>
	 * returns <code>true</code>
	 * @param previousBuffer the previous buffer, or null if there are no previous buffer.
	 * @param newBuffer the new buffer
	 * @param count number of event lost
	 */
	private void notifyLostEvent(int count, BillEventBuffer previousBuffer, BillEventBuffer newBuffer){
		for(BillEventListener listener : eventListeners){
			listener.lostEvent(count, previousBuffer, newBuffer);
		}
	}
	
	/**
	 * Called whenever a new event is fed to {@link #feed(BillEventBuffer)}.
	 * Only new events will result in a a call to {@link #notifyNewEvent(BillEvent)},
	 * i.e. when the eventCounter is found to be different between the previous
	 * and newly fed BillEventBuffer. Oldest events are processed first.
	 * @param e the new event
	 * @throws DeviceHandlingException 
	 */
	private void notifyNewEvent(BillEvent e) {
		for(BillEventListener listener : eventListeners){
			listener.newEvent(handler, e);
		}
	}

	/**
	 * 
	 * @return <code>Deque</code> of <code>BillEventBuffer</code> fed to 
	 * this <code>BillEventHandler</code>.
	 */
	public Deque<BillEventBuffer> getEventBufferDeque() {
		return this.eventBufferDeque;
	}

	/**
	 * 
	 * @return current event counter byte.
	 */
	public byte getCurrentEventCounter() {
		return this.peekPreviousEventCounter();
	}

	/**
	 * 
	 * @return maximum size of the event buffer queue
	 */
	public int getEventBufferQueueMaxSize() {
		return eventBufferDequeMaxSize;
	}

	/**
	 * 
	 * @return the list of added event listeners
	 */
	public Collection<BillEventListener> getEventListeners() {
		return eventListeners;
	}
	
}
