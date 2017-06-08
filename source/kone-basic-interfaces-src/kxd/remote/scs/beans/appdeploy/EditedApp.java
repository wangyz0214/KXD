package kxd.remote.scs.beans.appdeploy;

import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseAppCategory;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedApp extends BaseApp {
	private static final long serialVersionUID = 1L;
	private String appCode;
	private BaseAppCategory appCategory;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "code: " + appCode);
		logger.debug(prefix + "appCategory: ");
		appCategory.debug(logger, prefix + "  ");
	}

	public EditedApp() {
		super();
	}

	public EditedApp(Integer id, String appCode) {
		super(id);
		this.appCode = appCode;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedApp))
			return;
		EditedApp d = (EditedApp) src;
		appCode = d.appCode;
		appCategory = d.appCategory;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedApp();
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public BaseAppCategory getAppCategory() {
		return appCategory;
	}

	public void setAppCategory(BaseAppCategory appCategory) {
		this.appCategory = appCategory;
	}
}
