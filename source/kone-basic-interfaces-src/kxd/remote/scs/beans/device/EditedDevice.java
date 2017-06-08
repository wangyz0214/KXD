package kxd.remote.scs.beans.device;

import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedDevice extends QueryedDevice {
	private static final long serialVersionUID = 1L;
	private String extConfig;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "extConfig: " + extConfig + ";");
	}

	public EditedDevice() {
		super();
	}

	public EditedDevice(Integer id, String deviceName) {
		super(id, deviceName);
	}

	public EditedDevice(Integer id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedDevice))
			return;
		EditedDevice d = (EditedDevice) src;
		extConfig = d.extConfig;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedDevice();
	}

	public String getExtConfig() {
		return extConfig;
	}

	public void setExtConfig(String extConfig) {
		this.extConfig = extConfig;
	}
}
