package kxd.remote.scs.beans.appdeploy;

import kxd.remote.scs.beans.BaseAppCategory;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedAppCategory extends BaseAppCategory {
	private static final long serialVersionUID = 1L;
	private String appCategoryCode;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "code: " + appCategoryCode);
	}

	public EditedAppCategory() {
		super();
	}

	public EditedAppCategory(Integer id, String appCategoryCode) {
		super(id);
		this.appCategoryCode = appCategoryCode;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedAppCategory))
			return;
		EditedAppCategory d = (EditedAppCategory) src;
		appCategoryCode = d.appCategoryCode;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedAppCategory();
	}

	public String getAppCategoryCode() {
		return appCategoryCode;
	}

	public void setAppCategoryCode(String appCategoryCode) {
		this.appCategoryCode = appCategoryCode;
	}
}
