package com.github.pierrebeucher.cctalk4j.serial;

/**
 * Simple serial port interface for the concrete Serial Port used by ccTalk4j. This interface
 * is supposed to be a simple abtstraction level for a concrete Serial Port implementation which
 * can be obtained with {@link #getConcretSerialPort()}.
 * Current interface is pretty basic to support operations we need for CCTalk.
 * @author Pierre Beucher
 *
 */
public interface SerialPort {
	
	public static final int PARIY_NONE = 0,
			PARIY_EVEN = 1,
		    PARIY_MARK = 2,
		    PARIY_ODD = 3,
		    PARIY_SPACE = 4;
	
	public static final int FLOWCONTROL_NONE = 0, 
		FLOWCONTROL_RTSCTS = 1,
		FLOWCONTROL_XONXOFF = 2;
	
	/**
	 * 
	 * @return the concrete serial port instance used by this implementation
	 */
	public Object getConcretSerialPort();
	
	boolean writeBytes(byte[] b) throws SerialPortException;
	
	byte[] readBytes(int count, int timeout) throws SerialPortException;

	boolean open() throws SerialPortException;
	
	boolean close() throws SerialPortException;
	
	boolean isOpen();
	
	boolean isClosed();
	
	/**
	 * <p>Set the port parameters. All parameters are direct except parity 
	 * which is defined using flag, i.e.:
	 * <code>setPortParameters(9600, 8, 1, SerialPort.PARITY_NONE)</code>.</p>
	 * <p>If called before the port is open, will set proper parameters as soon
	 * when port is opened with {@link #open()} .</p>
	 * @param baudRate
	 * @param dataBits 
	 * @param stopBits 
	 * @param parity
	 * @param rtsLineStatus
	 * @param dtrLineStatus
	 */
	void setParameters(int baudRate, int dataBits, int stopBits, int parity, boolean rtsLineStatus, boolean dtrLineStatus) throws SerialPortException;
	
	/**
	 * Set the flow control, such as {@value #FLOWCONTROL_NONE}.
	 * If called before the port is open, will set proper parameters as soon
	 * when port is opened with {@link #open()}
	 * @param flowControlFlag
	 * @throws SerialPortException
	 */
	void setFlowControl(int flowControlFlag) throws SerialPortException;
	
	void setDTR(boolean enabled) throws SerialPortException;
	
	void setRTS(boolean enabled) throws SerialPortException;
	
	boolean isCTS() throws SerialPortException;
	
	boolean isDRS() throws SerialPortException;
	
	boolean isRING() throws SerialPortException;
	
	boolean isRLSD() throws SerialPortException;
	
	void resetInputBuffer() throws SerialPortException;
	
	void resetOutputBuffer() throws SerialPortException;
}
