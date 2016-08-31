package com.github.pierrebeucher.cctalk4j.device;

import com.github.pierrebeucher.cctalk4j.core.MessageIOException;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.SelfCheckResponseWrapper;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.UnexpectedContentException;

/**
 * Dummy device used for testing purpose only.
 * Most methods does nothing, unless specified.
 * @author Pierre Beucher
 *
 */
public class DummyDevice implements Device {

	@Override
	public void connect() throws MessagePortException {
	
	}

	@Override
	public void disconnect() throws MessagePortException {
		
	}


	/**
	 * Always return true.
	 */
	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public int getReadTimeout() {
		return 0;
	}

	@Override
	public void setReadTimeout(int readTimeout) {
		
	}

	@Override
	public int getWriteTimeout() {
		return 0;
	}

	@Override
	public void setWriteTimeout(int writeTimeout) {
		
	}

	/**
	 * Always returns true.
	 */
	@Override
	public boolean simplePoll() throws MessageIOException, UnexpectedContentException {
		return true;
	}

	@Override
	public String requestManufacturerId() throws MessageIOException, UnexpectedContentException {
		return null;
	}

	@Override
	public String requestEquipmentCategoryId() throws MessageIOException, UnexpectedContentException {
		return null;
	}

	@Override
	public String requestProductCode() throws MessageIOException, UnexpectedContentException {
		return null;
	}

	@Override
	public String requestBuildCode() throws MessageIOException, UnexpectedContentException {
		return null;
	}

	@Override
	public Object requestEncryptionSupport() throws MessageIOException, UnexpectedContentException {
		return null;
	}

	@Override
	public SelfCheckResponseWrapper performSelfCheck() throws MessageIOException, UnexpectedContentException {
		return null;
	}

}
