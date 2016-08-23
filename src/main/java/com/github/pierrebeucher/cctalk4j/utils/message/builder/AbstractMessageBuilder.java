package com.github.pierrebeucher.cctalk4j.utils.message.builder;

import java.util.Arrays;

import com.github.pierrebeucher.cctalk4j.core.Header;
import com.github.pierrebeucher.cctalk4j.core.Message;

public abstract class AbstractMessageBuilder implements MessageBuilder{

	protected Byte destination;
	protected Byte source;
	protected Byte header;
	protected byte[] data = EMPTY_DATA; //by default, no data
	
	public MessageBuilder destination(byte dest) {
		this.destination = dest;
		return this;
	}

	public MessageBuilder source(byte source) {
		this.source = source;
		return this;
	}

	public MessageBuilder header(byte header) {
		this.header = header;
		return this;
	}

	public MessageBuilder header(Header h) {
		this.header = h.getValue();
		return this;
	}

	public MessageBuilder data(byte[] data) {
		this.data = data;
		return this;
	}
	
	/**
	 * Ensure the message content consistency, throwing
	 * a <code>MessageBuildException</code> if message data are invalid.
	 * @throws MessageBuildException
	 */
	protected void ensureMessageConsistency() throws MessageBuildException{
		if(source == null || source == 0){
			throw new MessageBuildException("Source cannot be null or 0.");
		}
		
		if(destination == null){
			throw new MessageBuildException("Destination cannot be null.");
		}
		
		if(header == null){
			throw new MessageBuildException("Header cannot be null.");
		}
	}
	
	/**
	 * Buid the effective message. Call directly after {@link #ensureMessageConsistency()}
	 * @return
	 */
	protected abstract Message doBuild();

	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation calls {@link #ensureMessageConsistency()} and {@link #doBuild()}.</p>
	 */
	@Override
	public Message build() throws MessageBuildException {
		ensureMessageConsistency();
		return doBuild();
	}

	@Override
	public String toString() {
		return "AbstractMessageBuilder [destination=" + destination + ", source=" + source + ", header=" + header
				+ ", data=" + Arrays.toString(data) + "]";
	}

}
