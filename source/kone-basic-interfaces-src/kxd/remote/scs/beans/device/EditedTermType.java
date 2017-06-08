package kxd.remote.scs.beans.device;

import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.util.emun.CashFlag;
import kxd.remote.scs.util.emun.FixType;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedTermType extends BaseTermType {
	private static final long serialVersionUID = 1L;
	private String typeCode;
	private FixType fixType;
	private CashFlag cashFlag;
	private BaseApp app;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug( prefix + "typeCode: " + typeCode + ";");
		logger.debug( prefix + "fixType: " + fixType + ";");
		logger.debug( prefix + "cashFlag: " + cashFlag + ";");
		logger.debug( prefix + "app: ");
		app.debug(logger, prefix + "  ");
	}

	public EditedTermType() {
		super();
	}

	public EditedTermType(Integer id, String typeDesp) {
		super(id, typeDesp);
	}

	public EditedTermType(Integer id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedTermType))
			return;
		EditedTermType d = (EditedTermType) src;
		typeCode = d.typeCode;
		fixType = d.fixType;
		cashFlag = d.cashFlag;
		app = d.app;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedTermType();
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public FixType getFixType() {
		return fixType;
	}

	public void setFixType(FixType fixType) {
		this.fixType = fixType;
	}

	public CashFlag getCashFlag() {
		return cashFlag;
	}

	public void setCashFlag(CashFlag cashFlag) {
		this.cashFlag = cashFlag;
	}

	public BaseApp getApp() {
		return app;
	}

	public void setApp(BaseApp app) {
		this.app = app;
	}
}
