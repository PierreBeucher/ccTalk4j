package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import java.util.BitSet;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;

public class BitSetResponseWrapperTest {

	@Test
	public void wrap_nominal() throws MessageBuildException, UnexpectedContentException {
		byte[] data = new byte[]{1,0,-4,42,-50};
		BitSet expected = BitSet.valueOf(data);
		Message m = new CRCChecksumMessageBuilder()
			.header(Header.NONE)
			.destination((byte)40)
			.data(data)
			.build();
		Assert.assertEquals(BitSetResponseWrapper.wrap(m).bitSet(), expected);
	}

	@Test
	public void wrap_empty() throws MessageBuildException, UnexpectedContentException {
		byte[] data = new byte[]{};
		BitSet expected = BitSet.valueOf(data);
		Message m = new CRCChecksumMessageBuilder()
			.header(Header.NONE)
			.destination((byte)40)
			.data(data)
			.build();
		Assert.assertEquals(BitSetResponseWrapper.wrap(m).bitSet(), expected);
	}
}
