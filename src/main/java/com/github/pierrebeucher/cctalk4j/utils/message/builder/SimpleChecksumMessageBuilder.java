package com.github.pierrebeucher.cctalk4j.utils.message.builder;

import com.github.pierrebeucher.cctalk4j.core.Message;
import com.github.pierrebeucher.cctalk4j.core.SimpleChecksumMessage;

public class SimpleChecksumMessageBuilder extends AbstractMessageBuilder{

	public Message doBuild() {
		return new SimpleChecksumMessage(destination, source, header, data);
	}

}
