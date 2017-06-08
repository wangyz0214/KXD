package kxd.remote.scs.beans.device;

import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class QueryedDevice extends BaseDevice {
	private static final long serialVersionUID = 1L;
	private String manufDesp;
	private BaseDeviceType deviceType;
	private BaseDeviceDriver driver;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug( prefix + "manufDesp: " + manufDesp + ";");
		logger.debug( prefix + "deviceType: ");
		deviceType.debug(logger, prefix + "  ");
		logger.debug( prefix + "driver: ");
		driver.debug(logger, prefix + "  ");
	}

	public QueryedDevice() {
		super();
	}

	public QueryedDevice(Integer id, String deviceName) {
		super(id, deviceName);
	}

	public QueryedDevice(Integer id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof QueryedDevice))
			return;
		QueryedDevice d = (QueryedDevice) src;
		deviceType = d.deviceType;
		manufDesp = d.manufDesp;
		driver = d.driver;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new QueryedDevice();
	}

	public BaseDeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(BaseDeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getManufDesp() {
		return manufDesp;
	}

	public void setManufDesp(String manufDesp) {
		this.manufDesp = manufDesp;
	}

	@Override
	protected String toDisplayLabel() {
		if (deviceType != null)
			return deviceType.getDeviceTypeDesp() + "-"
					+ super.toDisplayLabel();
		else
			return super.toDisplayLabel();
	}

	public String getFileName() {
		if (driver == null)
			return null;
		else
			return driver.getDriverFile();
	}

	public BaseDeviceDriver getDriver() {
		return driver;
	}

	public void setDriver(BaseDeviceDriver driver) {
		this.driver = driver;
	}

}
