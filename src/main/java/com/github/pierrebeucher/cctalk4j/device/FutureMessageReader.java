package com.github.pierrebeucher.cctalk4j.device;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessagePort;
import com.github.pierrebeucher.cctalk4j.core.MessagePortTimeoutException;

/**
 * <p>The <code>FutureMessageReader</code> is used to read
 * messages from a <code>MessagePort</code> through a <code>Future</code>.
 * This allow the calling thread to contiue processing and retrieve the result
 * as wished.</p>
 * @author Pierre Beucher
 *
 */
public class FutureMessageReader {

	private MessagePort messagePort;
		
	private ExecutorService executorService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * @param messagePort message port to use
	 */
	public FutureMessageReader(MessagePort messagePort) {
		super();
		this.messagePort = messagePort;
		this.executorService = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * Submit a new task which will read a message with the given timeout.
	 * This function returns immediatly with a Future which can be used
	 * to retrieve the message to read. Note that the message may be null
	 * if no message was received.
	 */
	public Future<MessageReaderResponse> readMessage(final int timeout){
		return this.executorService.submit(new MessageReaderCallable(timeout, messagePort));
	}
	
	private class MessageReaderCallable implements Callable<MessageReaderResponse>{

		private int timeout;
		
		private MessagePort port;
		
		/**
		 * @param timeout
		 * @param port
		 */
		public MessageReaderCallable(int timeout, MessagePort port) {
			super();
			this.timeout = timeout;
			this.port = port;
		}
		
		@Override
		public MessageReaderResponse call() throws Exception {
			MessageReaderResponse readerResponse = new MessageReaderResponse();
			
			try {
				Message response = this.port.read(timeout);
				readerResponse.setMessage(response);
			} catch (Exception e) {
				logger.warn("Exception encountered when reading message. "
						+ "It will be reported in the message reader response. Caused by:", e);
				readerResponse.setHasTimedOut(e instanceof MessagePortTimeoutException);
				readerResponse.setException(e);
			}
			
			return readerResponse;
		}
	}
}
