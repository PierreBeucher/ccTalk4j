package com.github.pierrebeucher.cctalk4j.device;

import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.MessagePortException;
import com.github.pierrebeucher.cctalk4j.utils.message.wrapper.SelfCheckResponseWrapper;

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
	public void simplePoll() {
		return;
	}

	@Override
	public String requestManufacturerId() {
		return null;
	}

	@Override
	public String requestEquipmentCategoryId() {
		return null;
	}

	@Override
	public String requestProductCode() {
		return null;
	}

	@Override
	public String requestBuildCode() {
		return null;
	}

	@Override
	public Object requestEncryptionSupport() {
		return null;
	}

	@Override
	public SelfCheckResponseWrapper performSelfCheck() {
		return null;
	}

	@Override
	public void request(Message m) throws DeviceRequestException {

	}

	@Override
	public Message requestResponse(Message m) throws DeviceRequestException {
		return null;
	}

}
