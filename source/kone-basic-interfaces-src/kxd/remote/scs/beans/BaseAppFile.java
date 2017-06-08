package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseAppFile extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String appFilename;
	private BaseApp app;

	@Override
	public String getText() {
		return appFilename;
	}

	@Override
	public void setText(String text) {
		appFilename = text;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseAppFile();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseAppFile))
			return;
		BaseAppFile d = (BaseAppFile) src;
		appFilename = d.appFilename;
		app = d.app;
	}

	public BaseAppFile() {
		super();
	}

	public BaseAppFile(Integer id) {
		super(id);
	}

	public Integer getAppFileId() {
		return getId();
	}

	public void setAppFileId(Integer id) {
		setId(id);
	}

	@Override
	protected String toDisplayLabel() {
		return appFilename;
	}

	@Override
	public String toString() {
		return appFilename + "(" + getId() + ")";
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
		logger.debug(prefix + "filename: " + getAppFilename() + ";");
		logger.debug(prefix + "app: {");
		app.debug(logger, prefix + "  ");
		logger.debug(prefix + "}");
	}

	public String getAppFilename() {
		return appFilename;
	}

	public void setAppFilename(String appFilename) {
		this.appFilename = appFilename;
	}

	public BaseApp getApp() {
		return app;
	}

	public void setApp(BaseApp app) {
		this.app = app;
	}
}
