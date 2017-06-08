package kxd.remote.scs.beans.device;

import java.util.Date;

import kxd.remote.scs.beans.BaseBankTerm;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class EditedBankTerm extends BaseBankTerm {
	private static final long serialVersionUID = 1L;
	private String workKey;
	private String mackKey;
	private String batch;
	private String extField;
	private Date signinTime;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "workKey: " + workKey + ";");
		logger.debug(prefix + "mackKey: " + mackKey + ";");
		logger.debug(prefix + "batch: " + batch + ";");
		logger.debug(prefix + "extField: " + extField + ";");
		logger.debug(prefix + "signinTime: " + signinTime + ";");
	}

	public EditedBankTerm() {
		super();
	}

	public EditedBankTerm(Integer id) {
		super(id);
	}

	public EditedBankTerm(Integer id, String termCode, String termDesp,
			String merchantAccount) {
		super(id, termCode, termDesp, merchantAccount);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedBankTerm))
			return;
		EditedBankTerm d = (EditedBankTerm) src;
		workKey = d.workKey;
		mackKey = d.mackKey;
		batch = d.batch;
		signinTime = d.signinTime;
		extField = d.extField;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedBankTerm();
	}

	public String getWorkKey() {
		return workKey;
	}

	public void setWorkKey(String workKey) {
		this.workKey = workKey;
	}

	public String getMacKey() {
		return mackKey;
	}

	public void setMacKey(String mackKey) {
		this.mackKey = mackKey;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Date getSigninTime() {
		return signinTime;
	}

	public void setSigninTime(Date signinTime) {
		this.signinTime = signinTime;
	}

	public String getExtField() {
		return extField;
	}

	public void setExtField(String extField) {
		this.extField = extField;
	}

}
