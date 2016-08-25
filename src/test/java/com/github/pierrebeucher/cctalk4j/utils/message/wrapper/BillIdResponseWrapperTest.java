package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import java.nio.charset.StandardCharsets;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.device.bill.Bill;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;

public class BillIdResponseWrapperTest {

	private Bill bill = new Bill((byte) 1, "XO", "0010", "D");
	private Message messageOk;
	private Message messageTooShort;
	
	@BeforeClass
	public void beforeClass() throws MessageBuildException{
		this.messageOk = new CRCChecksumMessageBuilder()
			.header(Header.NONE)
			.destination((byte)1)
			.data(bill.rawIdentification().getBytes( StandardCharsets.US_ASCII))
			.build();
		
		this.messageTooShort = new CRCChecksumMessageBuilder()
				.header(Header.NONE)
				.destination((byte)1)
				.data(new byte[]{1,2,3,4,5,6})
				.build();
	}
	
	@Test
	public void wrap_nominal() throws UnexpectedContentException {
		Assert.assertNotNull(BillIdResponseWrapper.wrap(messageOk));
	}
	
	@Test
	public void wrap_err_lengh_too_short() {
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				BillIdResponseWrapper.wrap(messageTooShort);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void getCountryCode() throws UnexpectedContentException {
		Assert.assertEquals(BillIdResponseWrapper.wrap(messageOk).getCountryCode(), bill.countryCode());
	}

	@Test
	public void getIssueCode() throws UnexpectedContentException {
		Assert.assertEquals(BillIdResponseWrapper.wrap(messageOk).getIssueCode(), bill.issueCode());
	}

	@Test
	public void getValueCode() throws UnexpectedContentException {
		Assert.assertEquals(BillIdResponseWrapper.wrap(messageOk).getValueCode(), bill.valueCode());
	}

	
}
