package com.github.pierrebeucher.cctalk4j.core;

import java.math.BigInteger;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Utils;

/**
 * CRC 16 CCIT tests data taken from the ccTalk official specifications examples.
 * See CranePi ccTalk specs part 3 v4.7 page 29 and 31.
 * @author Pierre Beucher
 *
 */
public class UtilsTest {
	
	//private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * TX crc = CRC( 40, 0, 1 ) = 3F46 hex
	 */
	@Test
	public void checksumCRC16CCIT_1() {
		checksumCRC16CCIT(new byte[]{40, 0, 1}, "3F46");
	}
	
	/**
	 * RX crc = CRC( 1, 0, 0 ) = 3730 hex
	 */
	@Test
	public void checksumCRC16CCIT_2() {
		checksumCRC16CCIT(new byte[]{1, 0, 0}, "3730");
	}
	
	/**
	 * Data = 49 D5 F2 / CRC-CCITT Checksum = A6B3
	 */
	@Test
	public void checksumCRC16CCIT_3() {
		checksumCRC16CCIT("49D5F2", "A6B3");
	}
	
	/**
	 * Data = 2F BD 9D / CRC-CCITT Checksum = 90B2
	 */
	@Test
	public void checksumCRC16CCIT_4() {
		checksumCRC16CCIT("2FBD9D", "90B2");
	}
	
	/**
	 * Data = D9 53 D1 / CRC-CCITT Checksum = 7BB5
	 */
	@Test
	public void checksumCRC16CCIT_5() {
		checksumCRC16CCIT("D953D1", "7BB5");
	}
	
	/**
	 * Data = 70 B8 D9 64 04 15 / CRC-CCITT Checksum = FB00
	 */
	@Test
	public void checksumCRC16CCIT_6() {
		checksumCRC16CCIT("70B8D9640415", "FB00");
	}
	
	/**
	 * Data = 72 61 B9 4E D0 78 / CRC-CCITT Checksum = 93E3
	 */
	@Test
	public void checksumCRC16CCIT_7() {
		checksumCRC16CCIT("7261B94ED078", "93E3");
	}
	
	/**
	 * Data = 63 FA D1 9F E6 19 / CRC-CCITT Checksum = 5BB3
	 */
	@Test
	public void checksumCRC16CCIT_8() {
		checksumCRC16CCIT("63FAD19FE619", "5bb3");
	}

	private void checksumCRC16CCIT(String hexData, String hexChecksumExpected){
		byte[] bytes = new BigInteger(hexData, 16).toByteArray();
		short result = Utils.checksumCRC16CCIT(bytes);
		Assert.assertEquals(Utils.shortToHex(result), hexChecksumExpected.toLowerCase());
	}
	
	private void checksumCRC16CCIT(byte[] bytes, String hexChecksumExpected){
		short result = Utils.checksumCRC16CCIT(bytes);
		Assert.assertEquals(Integer.toHexString(result), hexChecksumExpected.toLowerCase());
	}
	
	@Test
	public void byteToUnsignedInt_1(){
		byte b = 42;
		int result = Utils.byteToUnsignedInt(b);
		Assert.assertEquals(result, 42);
	}
	
	@Test
	public void byteToUnsignedInt_2(){
		byte b = -1;
		int result = Utils.byteToUnsignedInt(b);
		Assert.assertEquals(result, 255);
	}
	
	@Test
	public void unsignedIntToByte_1(){
		int i=42;
		byte expected = 42;
		Assert.assertEquals(Utils.unsignedIntToByte(i), expected);
	}
	
	@Test
	public void unsignedIntToByte_2(){
		int i=255;
		byte expected = -1;
		Assert.assertEquals(Utils.unsignedIntToByte(i), expected);
	}
	
	@Test
	public void unsignedIntsToBytes(){
		int[] intArray = new int[]{254, 41};
		byte[] expected = new byte[]{-2, 41};
		byte[] actual = Utils.unsignedIntsToBytes(intArray);
		
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void bytesToHex(){
		//3F 46 (63 70)
		String result = Utils.bytesToHex(new byte[]{63, 70});
		Assert.assertEquals(result, "3F46");
	}
	
	@Test
	public void shortToHex(){
		short n = 42; //hex=2a
		String result = Utils.shortToHex(n);
		Assert.assertEquals(result, "2a");
	}
	
	@Test
	public void concat_both_full(){
		byte[] a = new byte[]{1,2};
		byte[] b = new byte[]{3,4,5,42};
		byte[] actual = Utils.concat(a, b);
		byte[] expected = new byte[]{1,2,3,4,5,42};
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void concat_left_empty(){
		byte[] a = new byte[]{};
		byte[] b = new byte[]{4,5,42};
		byte[] actual = Utils.concat(a, b);
		byte[] expected = new byte[]{4,5,42};
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void concat_right_empty(){
		byte[] a = new byte[]{1,2,3};
		byte[] b = new byte[]{};
		byte[] actual = Utils.concat(a, b);
		byte[] expected = new byte[]{1,2,3};
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void boolToByte_true(){
		Assert.assertEquals(Utils.boolToByte(true), (byte)1);
	}
	
	@Test
	public void boolToByte_false(){
		Assert.assertEquals(Utils.boolToByte(false), (byte)0);
	}
	
	@Test
	public void byteToBool_true(){
		Assert.assertTrue(Utils.byteToBool((byte)1));
	}
	
	@Test
	public void byteToBool_false(){
		Assert.assertFalse(Utils.byteToBool((byte)0));
	}
	
	@Test
	public void byteToBool_err_minus_1(){
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				Utils.byteToBool((byte)-1);
			}
		};
		Assert.assertThrows(IllegalArgumentException.class, r);
	}
	
	@Test
	public void byteToBool_err_2(){
		ThrowingRunnable r = new ThrowingRunnable(){
			@Override
			public void run() throws Throwable {
				Utils.byteToBool((byte)2);
			}
		};
		Assert.assertThrows(IllegalArgumentException.class, r);
	}
}
