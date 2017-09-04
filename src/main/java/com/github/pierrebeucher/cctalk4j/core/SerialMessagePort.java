package com.github.pierrebeucher.cctalk4j.core;

import com.github.pierrebeucher.cctalk4j.serial.JsscSerialPort;
import com.github.pierrebeucher.cctalk4j.serial.SerialPort;
import com.github.pierrebeucher.cctalk4j.serial.SerialPortException;

/**
 * A <code>MessagePort</code> using a serial port for communication. 
 * The serial port is configured to respect ccTalk specifications default
 * parameters: 9600 baud rate, 8 data bits, 1 stop bit, no parity.
 * @author Pierre Beucher
 *
 */
public class SerialMessagePort implements MessagePort{
	
	public static final int DEFAULT_BAUD_RATE = 9600;
	public static final int DEFAULT_DATA_BITS = 8;
	public static final int DEFAULT_STOP_BITS = 1;
	public static final int DEFAULT_PARITY = SerialPort.PARIY_NONE;
	public static final int DEFAULT_FLOW_CONTROL = SerialPort.FLOWCONTROL_NONE;
	
	private SerialPort serialPort;
	
	private MessageParser parser;
	
	public SerialMessagePort(String serialPortName, int messageType){
		createSerialPort(serialPortName);
		switch(messageType){
			case MESSAGE_TYPE_CRC16_CHECKSUM:
				setParser(new CRCChecksumMessageParser());
				break;
			case MESSAGE_TYPE_STANDARD_CHECKSUM:
				throw new RuntimeException("Not available yet");
				//setParser(new StandardChecksumMessageParser());
				//break;
			default:
				throw new IllegalArgumentException("Unknown message type: " + messageType);
		}
	}
	
	public SerialMessagePort(String serialPortName, MessageParser parser) {
		createSerialPort(serialPortName);
		setParser(parser);
	}
	
	private void createSerialPort(String portName){
		this.serialPort = new JsscSerialPort(portName, 
				DEFAULT_BAUD_RATE, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS, DEFAULT_FLOW_CONTROL, DEFAULT_PARITY,
				false, false);
	}
	
	protected void setParser(MessageParser parser){
		this.parser = parser;
	}
	
	public void open() throws MessagePortException{
		try{
			if(!serialPort.open()){
				throw new MessagePortException("Cannot open port, openPort() returned false.");
			}
		} catch(SerialPortException e){
			throw new MessagePortException(e);
		}
	}
	
	public boolean isOpen(){
		return serialPort.isOpen();
	}
	
	public boolean isClosed(){
		return !serialPort.isOpen();
	}
	
	public void close() throws MessagePortException{
		try{
			if(!serialPort.close()){
				throw new MessagePortException("Cannot open port, closePort() returned false.");
			}
		} catch(SerialPortException e){
			throw new MessagePortException(e);
		}
	}
	
	public void write(Message m) throws MessagePortException{
		try {
			if(!serialPort.writeBytes(m.bytes())){
				throw new MessagePortException("Error during write, writeBytes() returned false.");
			}
		} catch (SerialPortException e) {
			throw new MessagePortException(e);
		}
	}

	public Message read(int timeout) throws MessagePortException, MessageParsingException {
		try {
			//try to read 2 bytes (dest and data length)
			//then read more bytes according to data length
			byte[] destAndLength = serialPort.readBytes(2, timeout);
			byte dataLength = destAndLength[1];
			
			//read the remaining message: data, header and checksum/source bytes
			//no timeout as the remaining bytes should be available
			byte[] remaining = serialPort.readBytes(dataLength + 3, timeout);
			
			parser.setMessageBytes(Utils.concat(destAndLength, remaining));
			return parser.parse();
		} catch (SerialPortException e) {
			throw new MessagePortException(e);
		} 
	}
	

	@Override
	public void resetInputBuffer() throws SerialPortException {
		this.serialPort.resetInputBuffer();
	}

	@Override
	public void resetOutputBuffer() throws SerialPortException {
		this.serialPort.resetOutputBuffer();
	}

	/**
	 * 
	 * @return the underlying <code>jssc.SerialPort</code> used 
	 */
	public SerialPort getSerialPort() {
		return serialPort;
	}

	public MessageParser getParser() {
		return parser;
	}
}
