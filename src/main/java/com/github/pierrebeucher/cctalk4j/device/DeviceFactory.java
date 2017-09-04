package com.github.pierrebeucher.cctalk4j.device;

import com.github.pierrebeucher.cctalk4j.core.CRCChecksumMessageParser;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.SerialMessagePort;
import com.github.pierrebeucher.cctalk4j.device.bill.validator.BillValidator;
import com.github.pierrebeucher.cctalk4j.serial.SerialPort;
import com.github.pierrebeucher.cctalk4j.serial.SerialPortException;
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
	 * with CRC Checksum messages. The device uses a configurator to setup the serial port 
	 * with a 9600 baud rate, 8 data bits, 1 stop bits no parity and no flow control. 
	 * @param portName port to use
	 * @param address address of the device
	 * @return
	 * @throws SerialPortException 
	 */
	public static BillValidator billValidatorSerialCRC(String portName, byte address) {
			SerialMessagePort port = new SerialMessagePort(portName, new CRCChecksumMessageParser());
			BillValidator dev = new BillValidator(port, CRCChecksumMessageBuilder.class, address); 
			dev.addConfigurator(new DeviceConfigurator(){
				@Override
				public void configureDevice(Device dev) throws DeviceConfigurationException {
					try {
						SerialPort sp = ((SerialMessagePort)((BillValidator) dev).getMessagePort()).getSerialPort();
						sp.setParameters(9600, 8, 1, SerialPort.PARIY_NONE, false, false);
						sp.setFlowControl(SerialPort.FLOWCONTROL_RTSCTS);
					} catch (Exception e) {
						throw new DeviceConfigurationException(e);
					}
				}
			});
			
			return dev; 
	}
	
	

}
