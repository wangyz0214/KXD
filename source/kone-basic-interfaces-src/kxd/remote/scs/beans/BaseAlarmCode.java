package kxd.remote.scs.beans;

import kxd.util.DebugableObject;
import org.apache.log4j.Logger;

public class BaseAlarmCode extends DebugableObject {
	private static final long serialVersionUID = 1L;
	private Integer deviceType;
	private int alarmCode;
	private BaseAlarmCategory alarmCategory;
	private String alarmDesp;

	@Override
	public DebugableObject createObject() {
		return new BaseAlarmCode();
	}

	public void copyData(Object src) {
		if (!(src instanceof BaseAlarmCode))
			return;
		BaseAlarmCode d = (BaseAlarmCode) src;
		deviceType = d.deviceType;
		alarmCode = d.alarmCode;
		alarmCategory = d.alarmCategory;
		alarmDesp = d.alarmDesp;
	}

	public BaseAlarmCode() {
		super();
	}

	public BaseAlarmCode(Integer deviceType, int alarmCode) {
		this.deviceType = deviceType;
		this.alarmCode = alarmCode;
	}

	public BaseAlarmCode(Integer deviceType, int alarmCode, String desp) {
		this.deviceType = deviceType;
		this.alarmCode = alarmCode;
		this.alarmDesp = desp;
	}

	public String getAlarmDesp() {
		return alarmDesp;
	}

	public void setAlarmDesp(String alarmDesp) {
		this.alarmDesp = alarmDesp;
	}

	@Override
	public String toString() {
		return alarmDesp;
	}

	@Override
	protected void debugContent(Logger logger, String prefix) {
		logger.debug( prefix + "code: " + getAlarmCode() + ";");
		logger.debug( prefix + "desp: " + getAlarmDesp() + ";");
		logger.debug( prefix + "deviceType: " + getDeviceType() + ";");
		logger.debug( prefix + "alarmCategory:{ ");
		alarmCategory.debug(logger, prefix + "  ");
		logger.debug( prefix + "} ");
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public int getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(int alarmCode) {
		this.alarmCode = alarmCode;
	}

	public BaseAlarmCategory getAlarmCategory() {
		return alarmCategory;
	}

	public void setAlarmCategory(BaseAlarmCategory alarmCategory) {
		this.alarmCategory = alarmCategory;
	}
}
