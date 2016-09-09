package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import java.math.BigDecimal;
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

	private Bill bill = new Bill((byte) 1, "XO", "0010", "D", BigDecimal.valueOf(1000));
	private Bill john = new Bill((byte) 1, "XO", "0020", "A", BigDecimal.valueOf(2000));
	
	private Message messageOk;
	private Message messageAlsoOk;
	private Message messageTooShort;
	private Message messageUnprogrammed;
	
	@BeforeClass
	public void beforeClass() throws MessageBuildException{
		this.messageOk = new CRCChecksumMessageBuilder()
			.header(Header.NONE)
			.destination((byte)1)
			.data(bill.rawIdentification().getBytes(StandardCharsets.US_ASCII))
			.build();
		
		this.messageAlsoOk = new CRCChecksumMessageBuilder()
				.header(Header.NONE)
				.destination((byte)1)
				.data(john.rawIdentification().getBytes(StandardCharsets.US_ASCII))
				.build();
		
		this.messageTooShort = new CRCChecksumMessageBuilder()
				.header(Header.NONE)
				.destination((byte)1)
				.data(new byte[]{1,2,3,4,5,6})
				.build();
		
		this.messageUnprogrammed = new CRCChecksumMessageBuilder()
				.header(Header.NONE)
				.destination((byte)1)
				.data(".......".getBytes())
				.build();
	}
	
	@Test
	public void isProgrammed_false() throws UnexpectedContentException{
		BillIdResponseWrapper wp = BillIdResponseWrapper.wrap(messageUnprogrammed);
		Assert.assertFalse(BillIdResponseWrapper.isProgrammed(wp));
	}
	
	@Test
	public void isProgrammed_true() throws UnexpectedContentException{
		BillIdResponseWrapper wp = BillIdResponseWrapper.wrap(messageOk);
		Assert.assertTrue(BillIdResponseWrapper.isProgrammed(wp));
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
	
	@Test
	public void equals_true() throws UnexpectedContentException{
		BillIdResponseWrapper wp1 = BillIdResponseWrapper.wrap(messageOk);
		BillIdResponseWrapper wp2 = BillIdResponseWrapper.wrap(messageOk);
		Assert.assertEquals(wp1 , wp2);
	}
	
	@Test
	public void equals_false() throws UnexpectedContentException{
		BillIdResponseWrapper wp1 = BillIdResponseWrapper.wrap(messageAlsoOk);
		BillIdResponseWrapper wp2 = BillIdResponseWrapper.wrap(messageOk);
		Assert.assertNotEquals(wp1 , wp2);
	}

	
}
