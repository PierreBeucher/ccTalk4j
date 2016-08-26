package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;

public class CountryScalingFactorResponseWrapperTest {

	// 1011 1000 = Java byte -71
	// 1000 1000 = Java byte -119
	// ‭1000 1000 1011 1000‬ = 35000 (base 10) 
	private byte scalingFactorLsb = -72;
	private byte scalingFactorMsb = -120;
	private int scalingFactor = 35000;
	private byte decimalPlace = 3;
	
	private Message messageOk;
	private Message messageTooLong;
	private Message messageTooShort;
	
	@BeforeClass
	public void beforeClass() throws MessageBuildException{
		this.messageOk = new CRCChecksumMessageBuilder()
			.destination((byte)40)
			.header(Header.NONE)
			.data(new byte[]{scalingFactorLsb, scalingFactorMsb, decimalPlace}) 
			.build();
		
		this.messageTooShort = new CRCChecksumMessageBuilder()
				.destination((byte)40)
				.header(Header.NONE)
				.data(new byte[]{scalingFactorLsb, scalingFactorMsb}) 
				.build();
		
		this.messageTooLong = new CRCChecksumMessageBuilder()
				.destination((byte)40)
				.header(Header.NONE)
				.data(new byte[]{scalingFactorLsb, scalingFactorMsb, decimalPlace, 42}) 
				.build();
	}
	
	@Test
	public void wrap_nominal() throws UnexpectedContentException{
		Assert.assertNotNull(CountryScalingFactorWrapper.wrap(messageOk));
	}
	
	@Test
	public void wrap_err_too_short(){
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				CountryScalingFactorWrapper.wrap(messageTooShort);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void wrap_err_too_long(){
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				CountryScalingFactorWrapper.wrap(messageTooLong);
			}
		};
		Assert.assertThrows(UnexpectedContentException.class, r);
	}
	
	@Test
	public void getDecimalPlace() throws UnexpectedContentException {
		CountryScalingFactorWrapper mw = CountryScalingFactorWrapper.wrap(messageOk);
		Assert.assertEquals(mw.getDecimalPlace(), 3);
	}

	@Test
	public void getScalingFactor() throws UnexpectedContentException {
		CountryScalingFactorWrapper mw = CountryScalingFactorWrapper.wrap(messageOk);
		Assert.assertEquals(mw.getScalingFactor(), this.scalingFactor);
	}

	@Test
	public void produceScalingFactorInt() {
		Assert.assertEquals(
				CountryScalingFactorWrapper.produceScalingFactorInt(scalingFactorMsb, scalingFactorLsb),
				scalingFactor);
	}
}
