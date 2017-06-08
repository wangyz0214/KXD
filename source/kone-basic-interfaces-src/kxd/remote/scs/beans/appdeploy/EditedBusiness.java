package kxd.remote.scs.beans.appdeploy;

import kxd.remote.scs.beans.BaseBusiness;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class EditedBusiness extends BaseBusiness {
	private static final long serialVersionUID = 1L;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
	}

	public EditedBusiness() {
		super();
	}

	public EditedBusiness(Integer id) {
		super(id);
	}

	public EditedBusiness(Integer id, String desp) {
		super(id, desp);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedBusiness))
			return;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedBusiness();
	}

}
