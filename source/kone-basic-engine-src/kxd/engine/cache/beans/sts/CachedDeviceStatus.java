package kxd.engine.cache.beans.sts;

import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.util.IdableObject;
import kxd.util.ListItem;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedDeviceStatus extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private int status;
	private String message;
	private Long faultWorkFormId;// 故障工单ID
	private boolean notUploadStatus = false;
	private AlarmLevel level;

	public CachedDeviceStatus() {
		super();
		level = AlarmLevel.NORMAL;
	}

	public CachedDeviceStatus(int id) {
		super(id);
		level = AlarmLevel.NORMAL;
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
		if (message != null && message.length() > 125)
			message = message.substring(0, 125);
		this.message = message;
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedDeviceStatus) {
			CachedDeviceStatus o = (CachedDeviceStatus) src;
			setMessage(o.message);
			setStatus(o.status);
			faultWorkFormId = o.faultWorkFormId;
			notUploadStatus = o.notUploadStatus;
			level = o.level;
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedDeviceStatus();
	}

	@Override
	public String getText() {
		return message;
	}

	@Override
	public void setText(String text) {
		message = text;
	}

	public Long getFaultWorkFormId() {
		return faultWorkFormId;
	}

	public void setFaultWorkFormId(Long faultWorkFormId) {
		this.faultWorkFormId = faultWorkFormId;
	}

	@Override
	protected String toDisplayLabel() {
		return null;
	}

	@Override
	public String getIdString() {
		return Integer.toString(getId());
	}

	@Override
	public void setIdString(String id) {
		setId(Integer.valueOf(id));
	}

	public boolean isNotUploadStatus() {
		return notUploadStatus;
	}

	public void setNotUploadStatus(boolean notUploadStatus) {
		this.notUploadStatus = notUploadStatus;
	}

	public AlarmLevel getLevel() {
		return level;
	}

	public void setLevel(AlarmLevel level) {
		this.level = level;
	}
}
