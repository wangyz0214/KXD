package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseDevice extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String deviceName;

	@Override
	public String getText() {
		return deviceName;
	}

	@Override
	public void setText(String text) {
		deviceName = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "name: " + deviceName + ";");
	}

	public BaseDevice() {
		super();
	}

	public BaseDevice(Integer id) {
		super(id);
	}

	public BaseDevice(Integer id, String deviceName) {
		super(id);
		this.deviceName = deviceName;
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseDevice))
			return;
		BaseDevice d = (BaseDevice) src;
		deviceName = d.deviceName;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseDevice();
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Integer getDeviceId() {
		return getId();
	}

	public void setDeviceId(Integer id) {
		setId(id);
	}

	@Override
	protected String toDisplayLabel() {
		return deviceName;
	}

	@Override
	public String toString() {
		return deviceName + "(" + getId() + ")";
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
