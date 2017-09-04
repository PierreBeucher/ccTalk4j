package com.github.pierrebeucher.cctalk4j.serial;

public class JsscSerialPort implements SerialPort {

	private jssc.SerialPort sp;
	
	public JsscSerialPort(String portName) {
		super();
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
			return sp.openPort();
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

	@Override
	public void setParameters(int baudRate, int dataBits, int stopBits, int parity, boolean rtsLineStatus, boolean dtrLineStatus) throws SerialPortException {
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
			//baud rate, data and stop bits are transparent with jssc
			sp.setParams(jssc.SerialPort.BAUDRATE_9600, jssc.SerialPort.DATABITS_8, jssc.SerialPort.STOPBITS_1, jsscParity, rtsLineStatus, dtrLineStatus);
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
	}

	@Override
	public void setFlowControl(int flowControlFlag) throws SerialPortException {
		int jsscFlowControl = -1;
		switch(flowControlFlag){
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
			throw new SerialPortException("No match in jssc serial implementation for flow control: " + flowControlFlag);
		}
		try {
			sp.setFlowControlMode(jsscFlowControl);
		} catch (jssc.SerialPortException e) {
			throw new SerialPortException(e);
		}
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

}
