package com.github.pierrebeucher.cctalk4j.serial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsscSerialPort extends AbstractSerialPort implements SerialPort {

	private jssc.SerialPort sp;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public JsscSerialPort(String portName) {
		super();
		this.sp = new jssc.SerialPort(portName);
	}

	public JsscSerialPort(String portName, int baudRate, int dataBits, int stopBits, int flowControl, int parity,
			boolean rtsLineStatus, boolean dtrLineStatus) {
		super(portName, baudRate, dataBits, stopBits, flowControl, parity, rtsLineStatus, dtrLineStatus);
		this.sp = new jssc.SerialPort(portName);
	}

	/**
	 * Return the underlying <code>jssc.SerialPort</code> used.
	 */
	@Override
	public jssc.SerialPort getConcretSerialPort() {
		return this.sp;
	}

	@Override
	public boolean writeBytes(byte[] b) throws SerialPortException {
		try {
			return sp.writeBytes(b);
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public byte[] readBytes(int count, int timeout) throws SerialPortException {
		try {
			return sp.readBytes(count, timeout);
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		} catch (jssc.SerialPortTimeoutException e){
			throw new SerialPortTimeoutException(e);
		}
	}

	@Override
	public boolean open() throws SerialPortException {
		try {
			boolean result = sp.openPort();
			updateFlowControl();
			updateParameters();
			return result;
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public boolean close() throws SerialPortException {
		try {
			return sp.closePort();
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);		
		}
	}

	@Override
	public boolean isOpen() {
		return sp.isOpened();
	}

	@Override
	public boolean isClosed() {
		return !sp.isOpened();
	}
	
	protected void setParametersImplWrapper(int baudRate, int dataBits, int stopBits, int parity, boolean rtsLineStatus, boolean dtrLineStatus) throws SerialPortException{
		int jsscParity = -1;
		switch(parity){
		case SerialPort.PARIY_NONE:
			jsscParity=jssc.SerialPort.PARITY_NONE;
			break;
		case SerialPort.PARIY_EVEN:
			jsscParity=jssc.SerialPort.PARITY_EVEN;
			break;
		case SerialPort.PARIY_MARK:
			jsscParity=jssc.SerialPort.PARITY_MARK;
			break;
		case SerialPort.PARIY_ODD:
			jsscParity=jssc.SerialPort.PARITY_ODD;
			break;
		case SerialPort.PARIY_SPACE:
			jsscParity=jssc.SerialPort.PARITY_SPACE;
			break;
		default:
			throw new SerialPortException("No match in jssc serial implementation for parity: " + parity);
		}
		
		try {
			logger.debug("Setting jssc port params: baudRate={}, dataBits={}, stopBits={}, rts={}, dtr={}", baudRate, dataBits, stopBits, rtsLineStatus, dtrLineStatus);
			//baud rate, data and stop bits are transparent with jssc
			sp.setParams(baudRate, dataBits, stopBits, jsscParity, rtsLineStatus, dtrLineStatus);
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}
	
	protected void setFlowControlImplWrapper(int newFlowControl) throws SerialPortException{
		int jsscFlowControl = -1;
		switch(newFlowControl){
		case SerialPort.FLOWCONTROL_NONE:
			jsscFlowControl=jssc.SerialPort.FLOWCONTROL_NONE;
			break;
		case SerialPort.FLOWCONTROL_RTSCTS:
			jsscFlowControl=jssc.SerialPort.FLOWCONTROL_RTSCTS_IN;
			break;
		case SerialPort.FLOWCONTROL_XONXOFF:
			jsscFlowControl=jssc.SerialPort.FLOWCONTROL_XONXOFF_IN;
			break;
		default:
			throw new SerialPortException("No match in jssc serial implementation for flow control: " + newFlowControl);
		}
		try {
			logger.debug("Setting jssc flow control: {}", jsscFlowControl);
			sp.setFlowControlMode(jsscFlowControl);
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}
	
	/**
	 * Update the port parameters using currently set attributes. Only apply 
	 * if port is open.
	 * @throws SerialPortException 
	 */
	protected void updateParameters() throws SerialPortException{
		if(isOpen()){
			setParametersImplWrapper(baudRate, dataBits, stopBits, parity, rtsLineStatus, dtrLineStatus);
		}
	}
	
	/**
	 * Update the flow control using currently set attributes. Only apply 
	 * if port is open.
	 * @throws SerialPortException 
	 */
	protected void updateFlowControl() throws SerialPortException{
		if(isOpen()){
			setFlowControlImplWrapper(flowControl);
		}
	}

	@Override
	public void setParameters(int baudRate, int dataBits, int stopBits, int parity, boolean rtsLineStatus, boolean dtrLineStatus) throws SerialPortException {
		this.baudRate = baudRate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.rtsLineStatus = rtsLineStatus;
		this.dtrLineStatus = dtrLineStatus;
		this.updateParameters();
	}

	@Override
	public void setFlowControl(int flowControlFlag) throws SerialPortException {
		this.flowControl = flowControlFlag;
		this.updateFlowControl();
	}

	@Override
	public void setDTR(boolean enabled) throws SerialPortException {
		try {
			sp.setDTR(enabled);
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public void setRTS(boolean enabled) throws SerialPortException {
		try {
			sp.setRTS(enabled);
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public boolean isCTS() throws SerialPortException {
		try {
			return sp.isCTS();
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public boolean isDRS()  throws SerialPortException {
		try {
			return sp.isDSR();
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public boolean isRING() throws SerialPortException {
		try {
			return sp.isRING();
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public boolean isRLSD()  throws SerialPortException {
		try {
			return sp.isRLSD();
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public void resetInputBuffer() throws SerialPortException {
		purgeBuffer(jssc.SerialPort.PURGE_RXCLEAR);
	}

	@Override
	public void resetOutputBuffer() throws SerialPortException {
		purgeBuffer(jssc.SerialPort.PURGE_TXCLEAR);
	}
	
	private void purgeBuffer(int flag) throws SerialPortException{
		try {
			if(!sp.purgePort(flag)){
				throw new SerialPortException("Cannot purge buffer: purgePort() returned false");
			}
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

}
