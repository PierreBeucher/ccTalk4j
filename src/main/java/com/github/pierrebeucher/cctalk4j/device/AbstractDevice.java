package com.github.pierrebeucher.cctalk4j.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessageParsingException;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.core.MessagePortTimeoutException;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.AckWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.AsciiDataResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.SelfCheckResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

/**
 * Abstract implementation for a <code>Device</code>,
 * providing an interface to perform core ccTalk requests.
 * @author Pierre Beucher
 *
 */
public abstract class AbstractDevice implements Device {

	/**
	 * Default timeout when reading a message.
	 */
	public static final int DEFAULT_READ_TIMEOUT = 1000;
	
	/**
	 * Default timeout when writing a message.
	 */
	public static final int DEFAULT_WRITE_TIMEOUT = 1000;
	
	/**
	 * The master address, always equal to 1.
	 */
	public static final byte MASTER_ADDRESS = 1;
	
	/*
	 * Empty byte array used to build message with no data.
	 */
	private static final byte[] EMPTY_DATA_BYTE_ARRAY = new byte[]{};
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private MessagePort port;
	
	private byte deviceAddress;
	
	private Class<? extends MessageBuilder> messageBuilderClass;
	
	private int readTimeout;
	
	private int writeTimeout;
	
	/**
	 * Create a new <code>BillValidator</code> using the given port.
	 * @param port message port to use
	 * @param messageBuilderClass builder class used to create messages
	 * @param deviceAddress the device address
	 * @return 
	 */
	protected AbstractDevice(MessagePort port, Class<? extends MessageBuilder> messageBuilderClass,
			byte deviceAddress) {
		super();
		this.port = port;
		this.messageBuilderClass = messageBuilderClass;
		this.deviceAddress = deviceAddress;
		this.readTimeout = DEFAULT_READ_TIMEOUT;
		this.writeTimeout = DEFAULT_WRITE_TIMEOUT;
	}
	
	@Override
	public void connect() throws MessagePortException {
		port.open();
	}

	@Override
	public void disconnect() throws MessagePortException {
		port.close();
	}

	@Override
	public boolean isConnected() {
		return port.isOpen();
	}

	private MessageBuilder createBuilder(){
		try {
			return messageBuilderClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Cannot instanciate message builder.", e);
		}
	}
	
	private Message buildMessage(Header header, byte[] data){
		try {
			return createBuilder()
				.destination(this.deviceAddress)
				.source(MASTER_ADDRESS)
				.header(header)
				.data(data)
				.build();
		} catch (MessageBuildException e) {
			throw new RuntimeException("Unexpected Exception building a Message.", e);
		}
	}
	
	private Message buildMessage(Header header){
		return buildMessage(header, EMPTY_DATA_BYTE_ARRAY);
	}
	
	/**
	 * Read a single message using this device read timeout
	 * @return read message
	 * @throws MessagePortException
	 * @throws MessageParsingException
	 */
	private Message readMessage() throws MessagePortException, MessageParsingException{
		Message m = port.read(readTimeout);
		logger.debug("{} read {}", this, m);
		return m;
	}
	
	private void writeMessage(Message m) throws MessagePortException{
		logger.debug("{} writes {}", this, m);
		port.write(m);
	}

	@Override
	public boolean simplePoll() throws MessagePortException, MessageParsingException, UnexpectedContentException {
		writeMessage(buildMessage(Header.SIMPLE_POLL));
		try {
			Message response = readMessage();
			ensureAckMessage(response);
			return true;			
		} catch (MessagePortTimeoutException e) {
			//read timeout expected
			return false;
		}
	}
	/**
	 * Ensure the given message is an ACK message.
	 * Throw an <code>UnexpectedContentException</code> if it is not.
	 * @param m
	 * @throws UnexpectedContentException
	 */
	private void ensureAckMessage(Message m) throws UnexpectedContentException{
		AckWrapper.wrap(m);
	}
	
	/**
	 * Send a request with no data expecting a single response.
	 * @param requestHeader header to use 
	 * @return response obtained
	 * @throws MessageParsingException 
	 * @throws MessagePortException 
	 */
	protected Message requestResponse(Header requestHeader) throws MessagePortException, MessageParsingException{
		writeMessage(buildMessage(requestHeader));
		return readMessage();
	}
	
	/**
	 * Send a request with data payload expecting a single response.
	 * @param requestHeader header to use
	 * @param data data payload
	 * @return response obtained
	 * @throws MessagePortException
	 * @throws MessageParsingException
	 */
	protected Message requestResponse(Header requestHeader, byte[] data) throws MessagePortException, MessageParsingException{
		writeMessage(buildMessage(requestHeader, data));
		return readMessage();
	}
	
	/**
	 * Send a request with a single byte data payload expecting a single response.
	 * @param requestHeader header to use
	 * @param data single byte data payload
	 * @return response obtained
	 * @throws MessagePortException
	 * @throws MessageParsingException
	 */
	protected Message requestResponse(Header requestHeader, byte data) throws MessagePortException, MessageParsingException{
		writeMessage(buildMessage(requestHeader, new byte[]{data}));
		return readMessage();
	}
	
	/**
	 * Send a request expecting a single ASCII response, calling {@link #requestResponse(Header)}
	 * and wrapping the message with a <code>AsciiDataResponseWrapper</code>.
	 * @param request request to send
	 * @return response obtained as String
	 * @throws MessageParsingException 
	 * @throws MessagePortException 
	 * @throws UnexpectedContentException 
	 */
	protected String requestAsciiResponse(Header requestHeader) throws MessagePortException, MessageParsingException, UnexpectedContentException{
		Message m = requestResponse(requestHeader);
		return AsciiDataResponseWrapper.wrap(m).getAsciiData();
	}

	@Override
	public String requestManufacturerId() throws MessageIOException, UnexpectedContentException {
		return requestAsciiResponse(Header.REQUEST_MANUFACTURER_ID);
	}

	@Override
	public String requestEquipmentCategoryId() throws UnexpectedContentException, MessageIOException {
		return requestAsciiResponse(Header.REQUEST_EQUIPMENT_CATEGORY_ID);
	}

	@Override
	public String requestProductCode() throws UnexpectedContentException, MessageIOException {
		return requestAsciiResponse(Header.REQUEST_PRODUCT_CODE);
	}

	@Override
	public String requestBuildCode() throws UnexpectedContentException, MessageIOException {
		return requestAsciiResponse(Header.REQUEST_BUILD_CODE);
	}

	/**
	 * <p><b>Warning: not implemented yet, will throw a RuntimeException.</b></p>
	 * {@inheritDoc}
	 * 
	 */
	public Object requestEncryptionSupport() {
		throw new RuntimeException("Not implemented yet.");
	}

	@Override
	public SelfCheckResponseWrapper performSelfCheck() throws MessageIOException, UnexpectedContentException {
		Message response = requestResponse(Header.PERFORM_SELF_CHECK);
		return SelfCheckResponseWrapper.wrap(response);
	}

	@Override
	public int getReadTimeout() {
		return readTimeout;
	}

	@Override
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	@Override
	public int getWriteTimeout() {
		return writeTimeout;
	}

	@Override
	public void setWriteTimeout(int writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	@Override
	public String toString() {
		return "Device [addr=" + this.deviceAddress + "]";
	}

}
