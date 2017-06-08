package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseDeviceType extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String deviceTypeCode;

	@Override
	public String getText() {
		return deviceTypeCode;
	}

	@Override
	public void setText(String text) {
		deviceTypeCode = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "typecode: " + deviceTypeCode + ";");
	}

	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}

	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}

	private String deviceTypeDesp;
	private BaseDeviceTypeDriver driver;

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseDeviceType();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseDeviceType))
			return;
		BaseDeviceType d = (BaseDeviceType) src;
		deviceTypeCode = d.deviceTypeCode;
		deviceTypeDesp = d.deviceTypeDesp;
		driver = d.driver;
	}

	public BaseDeviceType() {
		super();
	}

	public BaseDeviceType(Integer id) {
		super(id);
	}

	public BaseDeviceType(Integer id, String deviceTypeDesp) {
		super(id);
		this.deviceTypeDesp = deviceTypeDesp;
	}

	public Integer getDeviceTypeId() {
		return getId();
	}

	public void setDeviceTypeId(Integer type) {
		setId(type);
	}

	public String getDeviceTypeDesp() {
		return deviceTypeDesp;
	}

	public void setDeviceTypeDesp(String deviceTypeDesp) {
		this.deviceTypeDesp = deviceTypeDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return deviceTypeDesp;
	}

	@Override
	public String toString() {
		return deviceTypeDesp + "(" + getId() + ")";
	}

	public BaseDeviceTypeDriver getDriver() {
		return driver;
	}

	public void setDriver(BaseDeviceTypeDriver driver) {
		this.driver = driver;
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
