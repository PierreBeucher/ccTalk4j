package com.github.pierrebeucher.cctalk4j.utils.message.builder;

import com.github.pierrebeucher.cctalk4j.core.CRCChecksumMessage;
import com.github.pierrebeucher.cctalk4j.core.Message;

/**
 * A <code>MessageBuilder</code> for <code>CRCChecksumMessage</code>.
 * Note that it is superficial to cal {@link #source(byte)} as
 * this kind of message does not contain a source byte. 
 * @author Pierre Beucher
 *
 */
public class CRCChecksumMessageBuilder extends AbstractMessageBuilder
		implements MessageBuilder {
	
	/**
	 * 
	 */
	public CRCChecksumMessageBuilder() {
		super();
		this.source = 1; // default source to 1 as it is unused
	}
	
	public Message doBuild() {		
		return new CRCChecksumMessage(destination, header, data);
	}

	/**
	 * {@inheritDoc}
	 * <p>Note: any source defined via {@link #source(byte)}
	 * is ignored, as a <code>CRCChecksumMessage</code> does not
	 * contain a source byte.</p>
	 */
	@Override
	public Message build() throws MessageBuildException {
		return super.build();
	}

}
