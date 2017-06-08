package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseDeviceDriver extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String deviceDriverDesp;
	private String driverFile;

	@Override
	public String getText() {
		return deviceDriverDesp;
	}

	@Override
	public void setText(String text) {
		deviceDriverDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + deviceDriverDesp + ";");
		logger.debug(prefix + "file: " + driverFile + ";");
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseDeviceDriver();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseDeviceDriver))
			return;
		BaseDeviceDriver d = (BaseDeviceDriver) src;
		deviceDriverDesp = d.deviceDriverDesp;
		driverFile = d.driverFile;
	}

	public BaseDeviceDriver() {
		super();
	}

	public BaseDeviceDriver(Integer id) {
		super(id);
	}

	public BaseDeviceDriver(Integer id, String desp) {
		super(id);
		this.deviceDriverDesp = desp;
	}

	@Override
	protected String toDisplayLabel() {
		return deviceDriverDesp;
	}

	@Override
	public String toString() {
		return deviceDriverDesp + "(" + getId() + ")";
	}

	public String getDeviceDriverDesp() {
		return deviceDriverDesp;
	}

	public void setDeviceDriverDesp(String deviceDriverDesp) {
		this.deviceDriverDesp = deviceDriverDesp;
	}

	public String getDriverFile() {
		return driverFile;
	}

	public void setDriverFile(String driverFile) {
		this.driverFile = driverFile;
	}

	public String getFileName() {
		if (driverFile == null)
			return null;
		else
			return "resource/device/" + getDriverFile();
	}

	public String getDriverFileDesp() {
		if (driverFile == null)
			return "未指定驱动程序";
		else
			return getDriverFile();
	}

	public Integer getDeviceDriverId() {
		return getId();
	}

	public void setDeviceDriverId(Integer id) {
		setId(id);
	}

	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		if (id == null || id.trim().isEmpty())
			setId(null);
		else
			setId(Integer.parseInt(id));
	}
}
