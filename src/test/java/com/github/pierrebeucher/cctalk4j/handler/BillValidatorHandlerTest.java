package com.github.pierrebeucher.cctalk4j.handler;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test bill value codes, scaling factors and decimal places. Based on example
 * provided with ccTalk specification part 3 v4.7 Appendix 15. 
 * @author Pierre Beucher
 *
 */
public class BillValidatorHandlerTest {

	@Test
	public void calculateBillCurrencyValue_1_100_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("1", 100, 2),
				BigDecimal.valueOf(1).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_10_100_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("10", 100, 2),
				BigDecimal.valueOf(10).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_1_10_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("1", 10, 2),
				BigDecimal.valueOf(0.1).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_25_10_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("25", 10, 2),
				BigDecimal.valueOf(2.5).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_100_10_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("100", 10, 2),
				BigDecimal.valueOf(10).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_2500_10_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("2500", 10, 2),
				BigDecimal.valueOf(250).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_1_1000_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("1", 1000, 2),
				BigDecimal.valueOf(10).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_250_1000_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("250", 1000, 2),
				BigDecimal.valueOf(2500).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_1_10000_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("1", 10000, 2),
				BigDecimal.valueOf(100).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_250_10000_2(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("250", 10000, 2),
				BigDecimal.valueOf(25000).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_smallest(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("1", 1, 2),
				BigDecimal.valueOf(0.01).setScale(2));
	}
	
	@Test
	public void calculateBillCurrencyValue_largest(){
		testCalculateBillCurrencyValue(
				BillValidatorHandler.calculateBillCurrencyValue("9999", 65535, 0),
				BigDecimal.valueOf(655284465));
	}
	
	private void testCalculateBillCurrencyValue(BigDecimal actual, BigDecimal expected){
		Assert.assertEquals(actual, expected);
	}
}
