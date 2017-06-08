package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseDeviceDriverFile extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private int deviceDriverId;
	private String fileName;

	@Override
	public String getText() {
		return fileName;
	}

	@Override
	public void setText(String text) {
		fileName = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "file: " + fileName + ";");
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseDeviceDriverFile();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseDeviceDriverFile))
			return;
		BaseDeviceDriverFile d = (BaseDeviceDriverFile) src;
		deviceDriverId = d.deviceDriverId;
		fileName = d.fileName;
	}

	public BaseDeviceDriverFile() {
		super();
	}

	public BaseDeviceDriverFile(Integer id) {
		super(id);
	}

	public BaseDeviceDriverFile(Integer id, String fileName) {
		super(id);
		this.fileName = fileName;
	}

	public BaseDeviceDriverFile(Integer id, int deviceDriverId, String fileName) {
		super(id);
		this.deviceDriverId = deviceDriverId;
		this.fileName = fileName;
	}

	@Override
	protected String toDisplayLabel() {
		return fileName;
	}

	@Override
	public String toString() {
		return fileName + "(" + getId() + ")";
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

	public int getDeviceDriverId() {
		return deviceDriverId;
	}

	public void setDeviceDriverId(int deviceDriverId) {
		this.deviceDriverId = deviceDriverId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
