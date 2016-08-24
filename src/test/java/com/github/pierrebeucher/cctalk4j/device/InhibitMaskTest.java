package com.github.pierrebeucher.cctalk4j.device;

import java.util.BitSet;

import org.testng.Assert;
import org.testng.annotations.Test;

public class InhibitMaskTest {

	@Test
	public void isEnabled_nominal() {
		byte inhibit1 = Byte.parseByte("01011101", 2);
		byte inhibit2 = Byte.parseByte("01110001", 2);
		InhibitMask mask = new InhibitMask(BitSet.valueOf(new byte[]{inhibit1, inhibit2}));
		
		Assert.assertEquals(mask.isEnabled(0), true);
		Assert.assertEquals(mask.isEnabled(1), false);
		Assert.assertEquals(mask.isEnabled(2), true);
		Assert.assertEquals(mask.isEnabled(3), true);
		Assert.assertEquals(mask.isEnabled(4), true);
		Assert.assertEquals(mask.isEnabled(5), false);
		Assert.assertEquals(mask.isEnabled(6), true);
		Assert.assertEquals(mask.isEnabled(7), false);

		Assert.assertEquals(mask.isEnabled(8), true);
		Assert.assertEquals(mask.isEnabled(9), false);
		Assert.assertEquals(mask.isEnabled(10), false);
		Assert.assertEquals(mask.isEnabled(11), false);
		Assert.assertEquals(mask.isEnabled(12), true);
		Assert.assertEquals(mask.isEnabled(13), true);
		Assert.assertEquals(mask.isEnabled(14), true);
		Assert.assertEquals(mask.isEnabled(15), false);
	}
	
	@Test
	public void isEnabled_empty() {
		byte inhibit1 = 0;
		byte inhibit2 = 0;
		BitSet bSet = BitSet.valueOf(new byte[]{inhibit1, inhibit2});
		InhibitMask mask = new InhibitMask(bSet);
		
		for(int i=0; i<16; i++){
			Assert.assertEquals(mask.isEnabled(i), false);
		}
	}
	
	@Test
	public void bytes_nominal(){
		byte[] data = new byte[]{1,2};
		InhibitMask mask = new InhibitMask(BitSet.valueOf(data));
		Assert.assertEquals(mask.bytes(), data);
	}
	
	@Test
	public void bytes_empty(){
		byte[] data = new byte[]{};
		byte[] expected = new byte[]{0, 0};
		InhibitMask mask = new InhibitMask(BitSet.valueOf(data));
		Assert.assertEquals(mask.bytes(), expected);
	}
	
	@Test
	public void equals_true(){
		BitSet bset1 = BitSet.valueOf(new byte[]{1,42});
		BitSet bset2 = BitSet.valueOf(new byte[]{1,42});
		InhibitMask mask1 = new InhibitMask(bset1);
		InhibitMask mask2 = new InhibitMask(bset2);
		Assert.assertEquals(mask1, mask2);
	}
	
	@Test
	public void equals_false(){
		InhibitMask mask1 = new InhibitMask(BitSet.valueOf(new byte[]{2,42}));
		InhibitMask mask2 = new InhibitMask(BitSet.valueOf(new byte[]{2,43}));
		Assert.assertNotEquals(mask1, mask2);
	}
	
}
