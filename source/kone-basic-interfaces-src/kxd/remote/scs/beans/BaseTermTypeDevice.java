package kxd.remote.scs.beans;

import kxd.util.DebugableObject;
import org.apache.log4j.Logger;

public class BaseTermTypeDevice extends DebugableObject {
	private static final long serialVersionUID = 1L;
	BaseTermType termType;
	BaseDevice device;

	@Override
	protected void debugContent(Logger logger, String prefix) {
		logger.debug( prefix + "termtype: ");
		termType.debug(logger, prefix + "  ");
		logger.debug( prefix + "device: ");
		device.debug(logger, prefix + "  ");
	}

	public BaseTermTypeDevice(BaseTermType termType, BaseDevice device) {
		super();
		this.termType = termType;
		this.device = device;
	}

	public void copyData(Object src) {
		if (!(src instanceof BaseTermTypeDevice))
			return;
		BaseTermTypeDevice d = (BaseTermTypeDevice) src;
		device = d.device;
		termType = d.termType;
	}

	public BaseTermTypeDevice createObject() {
		return new BaseTermTypeDevice();
	}

	public BaseTermTypeDevice() {
		super();
	}

	public BaseTermType getTermType() {
		return termType;
	}

	public void setTermType(BaseTermType termType) {
		this.termType = termType;
	}

	public BaseDevice getDevice() {
		return device;
	}

	public void setDevice(BaseDevice device) {
		this.device = device;
	}

}
