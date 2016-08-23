package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.Utils;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;

public class AbstractMessageWrapperTest {

	@Test
	public void getWrappedMessage() {
		final String excMsg = "Wrapping test exception";
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				Message m = new CRCChecksumMessageBuilder()
					.destination(Utils.unsignedIntToByte(1))
					.header(Header.SIMPLE_POLL)
					.build();
				new MyAbstractMessageWrapper(m).throwWrappingException(excMsg);
			}
		};
		
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	private class MyAbstractMessageWrapper extends AbstractMessageWrapper{

		protected MyAbstractMessageWrapper(Message message) throws UnexpectedContentException {
			super(message);
		}

		@Override
		protected void wrapContent() throws UnexpectedContentException {
			
		}
		
	}
}
