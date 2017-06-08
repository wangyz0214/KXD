package kxd.remote.scs.beans.appdeploy;

import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.BasePageElement;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class EditedPageElement extends BasePageElement {
	private static final long serialVersionUID = 1L;
	private BaseBusiness business;
	private String pageCode;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "pageCode: " + pageCode + ";");
		logger.debug(prefix + "app: ");
		business.debug(logger, prefix + "  ");
	}

	public EditedPageElement() {
		super();
	}

	public EditedPageElement(Integer id, String pageDesp) {
		super(id, pageDesp);
	}

	public EditedPageElement(Integer id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedPageElement))
			return;
		EditedPageElement d = (EditedPageElement) src;
		business = d.business;
		pageCode = d.pageCode;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedPageElement();
	}

	public BaseBusiness getBusiness() {
		return business;
	}

	public void setBusiness(BaseBusiness business) {
		this.business = business;
	}

	public String getPageCode() {
		return pageCode;
	}

	public void setPageCode(String pageCode) {
		this.pageCode = pageCode;
	}
}
