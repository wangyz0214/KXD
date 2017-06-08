package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseApp extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String appDesp;

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseApp();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseApp))
			return;
		BaseApp d = (BaseApp) src;
		appDesp = d.appDesp;

	}

	public BaseApp() {
		super();
	}

	public BaseApp(Integer id) {
		super(id);
	}

	public BaseApp(Integer id, String desp) {
		super(id);
		this.appDesp = desp;
	}

	public Integer getAppId() {
		return getId();
	}

	public void setAppId(Integer id) {
		setId(id);
	}

	public String getAppDesp() {
		return appDesp;
	}

	public void setAppDesp(String appDesp) {
		this.appDesp = appDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return appDesp;
	}

	@Override
	public String toString() {
		return appDesp + "(" + getId() + ")";
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

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + getAppDesp() + ";");
	}

	@Override
	public String getText() {
		return appDesp;
	}

	@Override
	public void setText(String text) {
		this.appDesp = text;
	}
}
