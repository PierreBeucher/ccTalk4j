package com.github.pierrebeucher.cctalk4j.handler;

import com.github.pierrebeucher.cctalk4j.device.Device;

/**
 * <code>DeviceHandler</code> provides an interface to handle devices easily,
 * hidding the complexity behing easy to use functions and methods. 
 * @author Pierre Beucher
 *
 */
public interface DeviceHandler {

	/**
	 * 
	 * @return the <code>Device</code> handled by this <code>DeviceHandler</code>
	 */
	public Device getDevice();
}
