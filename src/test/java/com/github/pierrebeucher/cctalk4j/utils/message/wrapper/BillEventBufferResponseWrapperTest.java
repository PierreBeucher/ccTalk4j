package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.device.bill.event.BillEvent;
import com.github.pierrebeucher.cctalk4j.device.bill.event.UnrecognizedEventException;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;

public class BillEventBufferResponseWrapperTest {

	@Test
	public void wrap_nominal() throws UnexpectedContentException, MessageBuildException, IllegalArgumentException, UnrecognizedEventException {
		BillEvent e1 = BillEvent.event((byte)2, (byte)0);
		BillEvent e2 = BillEvent.event((byte)2, (byte)1);
		BillEvent e3 = BillEvent.event((byte)-45, (byte)0);
		BillEvent e4 = BillEvent.event((byte)0, (byte)0);
		BillEvent e5 = BillEvent.event((byte)0, (byte)5);
		
		Message m = new CRCChecksumMessageBuilder()
				.destination(Utils.unsignedIntToByte(40))
				.header(Header.NONE)
				.data(new byte[]{42,
						e1.getResultA(), e1.getResultB(), 
						e2.getResultA(), e2.getResultB(), 
						e3.getResultA(), e3.getResultB(), 
						e4.getResultA(), e4.getResultB(),
						e5.getResultA(), e5.getResultB()})
				.build();
		
		BillEventBufferResponseWrapper wrapper = BillEventBufferResponseWrapper.wrap(m); 
		
		//ensure each events is wrapper properly and event counter set
		Assert.assertEquals(wrapper.getEventCounter(), (byte)42);
		BillEvent[] events = wrapper.getBillEvents();
		Assert.assertEquals(events[0], e1);
		Assert.assertEquals(events[1], e2);
		Assert.assertEquals(events[2], e3);
		Assert.assertEquals(events[3], e4);
		Assert.assertEquals(events[4], e5);
	}
	
	@Test
	public void wrap_err_unrecognized_event() throws UnexpectedContentException, MessageBuildException, IllegalArgumentException, UnrecognizedEventException {
		BillEvent e1 = BillEvent.event((byte)2, (byte)0);
		BillEvent e2 = BillEvent.event((byte)2, (byte)1);
		BillEvent e3 = BillEvent.event((byte)-45, (byte)0);
		BillEvent e4 = BillEvent.event((byte)0, (byte)0);
		BillEvent e5 = BillEvent.event((byte)0, (byte)5);
		
		final Message m = new CRCChecksumMessageBuilder()
				.destination(Utils.unsignedIntToByte(40))
				.header(Header.NONE)
				.data(new byte[]{42,
						e1.getResultA(), e1.getResultB(), 
						e2.getResultA(), e2.getResultB(), 
						e3.getResultA(), e3.getResultB(), 
						e4.getResultA(), 42, //error
						e5.getResultA(), e5.getResultB()})
				.build();
		
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				BillEventBufferResponseWrapper.wrap(m);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void wrap_err_data_length() throws UnexpectedContentException, MessageBuildException, IllegalArgumentException, UnrecognizedEventException {
		BillEvent e1 = BillEvent.event((byte)2, (byte)0);
		BillEvent e2 = BillEvent.event((byte)2, (byte)1);
		BillEvent e3 = BillEvent.event((byte)-45, (byte)0);
		
		final Message m = new CRCChecksumMessageBuilder()
				.destination(Utils.unsignedIntToByte(40))
				.header(Header.NONE)
				.data(new byte[]{42,
						e1.getResultA(), e1.getResultB(), 
						e2.getResultA(), e2.getResultB(), 
						e3.getResultA(), e3.getResultB()
						//error: missing elements
						})
				.build();
		
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				BillEventBufferResponseWrapper.wrap(m);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	
}
