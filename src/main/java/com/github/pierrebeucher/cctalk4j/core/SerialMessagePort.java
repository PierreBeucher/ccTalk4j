package com.github.pierrebeucher.cctalk4j.core;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class SerialMessagePort implements MessagePort{
	
	private SerialPort serialPort;
	
	private MessageParser parser;
	
	public SerialMessagePort(String serialPortName, int messageType){
		this.serialPort = new SerialPort(serialPortName);
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
	
	public SerialMessagePort(String serialPortName, MessageParser parser){
		this.serialPort = new SerialPort(serialPortName);
		setParser(parser);
	}
	
	protected void setParser(MessageParser parser){
		this.parser = parser;
	}
	
	public void open() throws MessagePortException{
		try{
			if(!serialPort.openPort()){
				throw new MessagePortException("Cannot open port, openPort() returned false.");
			}
		} catch(SerialPortException e){
			throw new MessagePortException(e);
		}
	}
	
	public boolean isOpen(){
		return serialPort.isOpened();
	}
	
	public boolean isClosed(){
		return !serialPort.isOpened();
	}
	
	public void close() throws MessagePortException{
		try{
			if(!serialPort.closePort()){
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
			byte[] remaining = serialPort.readBytes(dataLength + 3);
			
			parser.setMessageBytes(Utils.concat(destAndLength, remaining));
			return parser.parse();
		} catch (SerialPortException e) {
			throw new MessagePortException(e);
		} catch (SerialPortTimeoutException e) {
			throw new MessagePortException(e);
		}
	}
}
