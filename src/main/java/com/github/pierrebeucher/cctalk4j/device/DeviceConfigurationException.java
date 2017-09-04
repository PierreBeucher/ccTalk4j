package com.github.pierrebeucher.cctalk4j.device;

public class DeviceConfigurationException extends DeviceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3746751209803091533L;

	public DeviceConfigurationException() {
		super();
	}

	protected DeviceConfigurationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DeviceConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeviceConfigurationException(String message) {
		super(message);
	}

	public DeviceConfigurationException(Throwable cause) {
		super(cause);
	}

}
