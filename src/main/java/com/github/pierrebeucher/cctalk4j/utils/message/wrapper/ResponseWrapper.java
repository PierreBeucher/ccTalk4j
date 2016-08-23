package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

public abstract class ResponseWrapper extends AbstractMessageWrapper {

	public ResponseWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

}
