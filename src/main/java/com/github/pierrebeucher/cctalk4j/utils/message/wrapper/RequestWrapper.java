package com.github.pierrebeucher.cctalk4j.utils.message.wrapper;

import com.github.pierrebeucher.cctalk4j.core.Message;

public abstract class RequestWrapper extends AbstractMessageWrapper{

	public RequestWrapper(Message message) throws UnexpectedContentException {
		super(message);
	}

}
