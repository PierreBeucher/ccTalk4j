package com.github.pierrebeucher.cctalk4j.device;

import com.github.pierrebeucher.cctalk4j.core.CRCChecksumMessageParser;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.SerialMessagePort;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.CRCChecksumMessageBuilder;

/**
 * Provides static methods to instantiate <code>Device</code>.
 * All functions are named using the following convention:
 * <code>{DeviceName}{CommunicationType}{MessageType}</code>
 * such as {@link #billValidatorSerialCRC(MessagePort, byte)} for
 * a Bill Validator using a serial port with CRC Checksum messages.
 * @author Pierre Beucher
 *
 */
public class DeviceFactory {
	
	private DeviceFactory(){}
	
	/**
	 * Create a new <code>BillValidator</code> using a serial port
	 * with CRC Checksum messages.
	 * @param portName port to use
	 * @param address address of the device
	 * @return
	 */
	public static BillValidator billValidatorSerialCRC(String portName, byte address){
		MessagePort port = new SerialMessagePort(portName, new CRCChecksumMessageParser());
		return new BillValidator(port, CRCChecksumMessageBuilder.class, address);
	}
	

}
