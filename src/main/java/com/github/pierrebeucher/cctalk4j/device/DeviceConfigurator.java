package com.github.pierrebeucher.cctalk4j.device;

/**
 * The <code>DeviceConfigurator</code> is used to configure a device when it is initialized
 * This is handy to define some configurations to be applied by default for the device,
 * such as serial port parameters or additional configuration which may be specific for each devices.
 * @author Pierre Beucher
 *
 */
public interface DeviceConfigurator {

	public void configureDevice(Device dev) throws DeviceConfigurationException;
}
