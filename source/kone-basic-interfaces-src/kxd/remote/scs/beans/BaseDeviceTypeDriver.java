package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseDeviceTypeDriver extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String deviceTypeDriverDesp;
	private String driverFile;

	@Override
	public String getText() {
		return deviceTypeDriverDesp;
	}

	@Override
	public void setText(String text) {
		deviceTypeDriverDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + deviceTypeDriverDesp + ";");
		logger.debug(prefix + "file: " + driverFile + ";");
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseDeviceTypeDriver();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseDeviceTypeDriver))
			return;
		BaseDeviceTypeDriver d = (BaseDeviceTypeDriver) src;
		deviceTypeDriverDesp = d.deviceTypeDriverDesp;
		driverFile = d.driverFile;

	}

	public BaseDeviceTypeDriver() {
		super();
	}

	public BaseDeviceTypeDriver(Integer id) {
		super(id);
	}

	public BaseDeviceTypeDriver(Integer id, String desp) {
		super(id);
		this.deviceTypeDriverDesp = desp;
	}

	@Override
	protected String toDisplayLabel() {
		return deviceTypeDriverDesp;
	}

	@Override
	public String toString() {
		return deviceTypeDriverDesp + "(" + getId() + ")";
	}

	public String getDeviceTypeDriverDesp() {
		return deviceTypeDriverDesp;
	}

	public void setDeviceTypeDriverDesp(String deviceTypeDriverDesp) {
		this.deviceTypeDriverDesp = deviceTypeDriverDesp;
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
			return "resource/devicetype/" + getDriverFile();
	}

	public String getDriverFileDesp() {
		if (driverFile == null)
			return "未指定驱动程序";
		else
			return getDriverFile();
	}

	public Integer getDeviceTypeDriverId() {
		return getId();
	}

	public void setDeviceTypeDriverId(Integer id) {
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
