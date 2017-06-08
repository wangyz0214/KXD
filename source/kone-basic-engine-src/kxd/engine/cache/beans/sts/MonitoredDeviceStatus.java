package kxd.engine.cache.beans.sts;

import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class MonitoredDeviceStatus extends ListItem<Integer> {
	private static final long serialVersionUID = -3279038990311209414L;
	private int status;
	private String message;
	private String deviceDesp;
	private String deviceTypeDesp;
	private int port;
	private String extConfig;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "status: " + status + ";");
		logger.debug(prefix + "message: " + message + ";");
		logger.debug(prefix + "deviceDesp: " + deviceDesp + ";");
		logger.debug(prefix + "deviceTypeDesp: " + deviceTypeDesp + ";");
		logger.debug(prefix + "port: " + port + ";");
		logger.debug(prefix + "extConfig: " + extConfig + ";");
	}

	public MonitoredDeviceStatus() {
		super();
	}

	public MonitoredDeviceStatus(Integer id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof MonitoredDeviceStatus))
			return;
		MonitoredDeviceStatus d = (MonitoredDeviceStatus) src;
		status = d.status;
		message = d.message;
		deviceDesp = d.deviceDesp;
		deviceTypeDesp = d.deviceTypeDesp;
		port = d.port;
		extConfig = d.extConfig;
	}

	@Override
	public ListItem<Integer> createObject() {
		return new MonitoredDeviceStatus();
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(Integer.valueOf(id));
	}

	@Override
	public String toDisplayLabel() {
		return getIdString();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getText() {
		return deviceDesp;
	}

	@Override
	public void setText(String text) {
		deviceDesp = text;
	}

	public String getDeviceDesp() {
		return deviceDesp;
	}

	public void setDeviceDesp(String deviceDesp) {
		this.deviceDesp = deviceDesp;
	}

	public String getDeviceTypeDesp() {
		return deviceTypeDesp;
	}

	public void setDeviceTypeDesp(String deviceTypeDesp) {
		this.deviceTypeDesp = deviceTypeDesp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getExtConfig() {
		return extConfig;
	}

	public void setExtConfig(String extConfig) {
		this.extConfig = extConfig;
	}

}
