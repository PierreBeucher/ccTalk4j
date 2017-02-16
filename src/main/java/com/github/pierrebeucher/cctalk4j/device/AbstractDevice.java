package com.github.pierrebeucher.cctalk4j.device;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuildException;
import com.github.pierrebeucher.cctalk4j.utils.message.builder.MessageBuilder;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.AckWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.AsciiDataResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.SelfCheckAckResponseWrapper;
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
	
	/*
	 * Message port used by this Device.
	 * Should not be used by external components, otherwise
	 * there is a synchronization risk by not using
	 * synchronized functions
	 */
	private MessagePort port;
	
	private byte deviceAddress;
	
	private Class<? extends MessageBuilder> messageBuilderClass;
	
	private int readTimeout;
	
	private int writeTimeout;
	
	private FutureMessageReader messageReader;
	
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
		this.messageReader = new FutureMessageReader(port);
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
	
//	/**
//	 * Read a single message using this device read timeout
//	 * @return read message
//	 * @throws MessagePortException
//	 * @throws MessageParsingException
//	 */
//	private Message readMessage() throws MessagePortException, MessageParsingException{
//		Message m = port.read(readTimeout);
//		logger.debug("{} read {}", this, m);
//		return m;
//	}
//	
//	private void writeMessage(Message m) throws MessagePortException{
//		logger.debug("{} writes {}", this, m);
//		port.write(m);
//	}

	@Override
	public void simplePoll() throws DeviceRequestException {
		Message ackResponse = requestResponse(Header.SIMPLE_POLL);
		if(!AckWrapper.isAck(ackResponse)){
			throw new DeviceRequestException("Obtained response is not ack: " + ackResponse);
		}
	}
	
	private Message requestResponse(Message m) throws DeviceRequestException{
		//start reading before sending request
		//this ensure that the response will not be lost
		//if we first write the message but the response arrive
		//before we being to read the port
		Future<MessageReaderResponse> futureResponse = messageReader.readMessage(readTimeout);
		
		//normally we should not have to define a timeout as port read already does
		//just in case...
		try {
			port.write(m);
			MessageReaderResponse readerResponse = futureResponse.get(readTimeout, TimeUnit.MILLISECONDS);
			Message responseMessage = readerResponse.getMessage();
			if(responseMessage == null){
				if(readerResponse.getException() != null){
					throw new DeviceRequestException("Unable to get response for request:" + readerResponse.getException(), readerResponse.getException());
				}
				throw new DeviceRequestException("Unable to read message response, but no exception reported. There is probably a bug in the MessageReaderResponse, please report.");
			}
			return responseMessage;
		} catch (MessagePortException | InterruptedException | ExecutionException | TimeoutException e) {
			throw new DeviceRequestException("Error during device request/response:" + e, e);
		}
	}
	
	/**
	 * Send a request with no data expecting a single response.
	 * @param requestHeader header to use 
	 * @return response obtained
	 * @throws DeviceRequestException 
	 */
	protected synchronized Message requestResponse(Header requestHeader) throws DeviceRequestException {
		return requestResponse(buildMessage(requestHeader));
	}
	
	/**
	 * Send a request with data payload expecting a single response.
	 * @param requestHeader header to use
	 * @param data data payload
	 * @return response obtained
	 * @throws DeviceRequestException 
	 */
	protected synchronized Message requestResponse(Header requestHeader, byte[] data) throws DeviceRequestException{
		return requestResponse(buildMessage(requestHeader, data));
	}
	
	/**
	 * Send a request with a single byte data payload expecting a single response.
	 * @param requestHeader header to use
	 * @param data single byte data payload
	 * @return response obtained
	 * @throws DeviceRequestException 
	 */
	protected synchronized Message requestResponse(Header requestHeader, byte data) throws DeviceRequestException{
		return requestResponse(buildMessage(requestHeader, new byte[]{data}));
	}
	
	/**
	 * Send a request expecting a single ASCII response, calling {@link #requestResponse(Header)}
	 * and wrapping the message with a <code>AsciiDataResponseWrapper</code>.
	 * @param request request to send
	 * @return response obtained as String 
	 * @throws DeviceRequestException 
	 */
	protected synchronized String requestAsciiResponse(Header requestHeader) throws DeviceRequestException{
		Message m = requestResponse(requestHeader);
		try {
			return AsciiDataResponseWrapper.wrap(m).getAsciiData();
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
	}

	@Override
	public String requestManufacturerId() throws DeviceRequestException {
		return requestAsciiResponse(Header.REQUEST_MANUFACTURER_ID);
	}

	@Override
	public String requestEquipmentCategoryId() throws DeviceRequestException {
		return requestAsciiResponse(Header.REQUEST_EQUIPMENT_CATEGORY_ID);
	}

	@Override
	public String requestProductCode() throws DeviceRequestException {
		return requestAsciiResponse(Header.REQUEST_PRODUCT_CODE);
	}

	@Override
	public String requestBuildCode() throws DeviceRequestException {
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
	public SelfCheckAckResponseWrapper performSelfCheck() throws DeviceRequestException {
		Message response = requestResponse(Header.PERFORM_SELF_CHECK);
		try {
			return SelfCheckAckResponseWrapper.wrap(response);
		} catch (UnexpectedContentException e) {
			throw new DeviceRequestException(e);
		}
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
