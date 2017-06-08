package kxd.remote.scs.beans;

import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class BaseDeviceStatus extends IdableObject<Integer> {
	private static final long serialVersionUID = -3279038990311209414L;
	private int status;
	private String message;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "status: " + status + ";");
		logger.debug(prefix + "message: " + message + ";");
	}

	public BaseDeviceStatus() {
		super();
	}

	public BaseDeviceStatus(Integer id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseDeviceStatus))
			return;
		BaseDeviceStatus d = (BaseDeviceStatus) src;
		status = d.status;
		message = d.message;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseDeviceStatus();
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
	protected String toDisplayLabel() {
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
}
