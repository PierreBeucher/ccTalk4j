package com.github.pierrebeucher.cctalk4j.serial;

public abstract class AbstractSerialPort implements SerialPort {

	protected String portName;
	
	protected int baudRate;
	
	protected int dataBits;
	
	protected int stopBits;
	
	protected int flowControl;
	
	protected int parity;
	
	protected boolean rtsLineStatus;
	
	protected boolean dtrLineStatus;
	
	public AbstractSerialPort(String portName, int baudRate, int dataBits, int stopBits, int flowControl, int parity,
			boolean rtsLineStatus, boolean dtrLineStatus) {
		super();
		this.portName = portName;
		this.baudRate = baudRate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.flowControl = flowControl;
		this.parity = parity;
		this.rtsLineStatus = rtsLineStatus;
		this.dtrLineStatus = dtrLineStatus;
	}
	
	public AbstractSerialPort(){
		super();
	}

	/**
	 * Use the concrete Serial port implementation to set parameters
	 * @throws SerialPortException
	 */
	protected abstract void setParametersImplWrapper(int baudRate, int dataBits, int stopBits, int parity, boolean rtsLineStatus, boolean dtrLineStatus) throws SerialPortException;
	
	/**
	 * Use the concrete serial port implementation to set flow control
	 * @param newFlowControl
	 * @throws SerialPortException
	 */
	protected abstract void setFlowControlImplWrapper(int newFlowControl) throws SerialPortException;
	
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
			setFlowControlImplWrapper(this.flowControl);
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

}
