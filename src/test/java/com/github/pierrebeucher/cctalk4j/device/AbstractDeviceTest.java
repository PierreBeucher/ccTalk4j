package com.github.pierrebeucher.cctalk4j.device;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.SerialMessagePort;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;

public class AbstractDeviceTest {
	
	private String portName = "DUMMY";
	private byte address = 40;
	
	private Device buildTestDevice(){
		MessagePort port = new SerialMessagePort(portName, MessagePort.MESSAGE_TYPE_CRC16_CHECKSUM);
		return new AbstractDevice(port,
				CRCChecksumMessageBuilder.class,
				address){
			
		};
	}

	@Test
	public void getReadTimeout() {
		Device device = buildTestDevice();
		device.setReadTimeout(42);
		Assert.assertEquals(device.getReadTimeout(), 42);
	}

	@Test
	public void getWriteTimeout() {
		Device device = buildTestDevice();
		device.setWriteTimeout(42);
		Assert.assertEquals(device.getWriteTimeout(), 42);
	}
}
