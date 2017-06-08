package kxd.remote.scs.beans.device;

import kxd.remote.scs.beans.BaseTermTypeDevice;

import org.apache.log4j.Logger;

public class EditedTermTypeDevice extends BaseTermTypeDevice {
	private static final long serialVersionUID = 1L;
	private int port;
	private String extConfig;

	@Override
	protected void debugContent(Logger logger, String prefix) {
		super.debugContent(logger, prefix);
		logger.debug( prefix + "port: " + port + ";");
		logger.debug( prefix + "extConfig: " + extConfig + ";");
	}

	public EditedTermTypeDevice() {
		super();
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedTermTypeDevice))
			return;
		EditedTermTypeDevice d = (EditedTermTypeDevice) src;
		port = d.port;
		extConfig = d.extConfig;
	}

	@Override
	public BaseTermTypeDevice createObject() {
		return new EditedTermTypeDevice();
	}

	public String getExtConfig() {
		return extConfig;
	}

	public void setExtConfig(String extConfig) {
		this.extConfig = extConfig;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
