package kxd.remote.scs.beans.invoice;

import kxd.remote.scs.beans.BaseOrg;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

/**
 * 
 * @author jurstone
 */
public class EditedInvoiceConfig extends BaseInvoiceConfig {
	private static final long serialVersionUID = 1L;
	private String configCode;
	private BaseOrg org;
	private int invoiceType;
	private BaseInvoiceTemplate invoiceTemplate;
	private int taxFlag;
	private int awayFlag;
	private int alertCount;
	private boolean logged;// 是否记录发票明细
	private String extdata0;
	private String extdata1;

	public EditedInvoiceConfig() {
		super();
	}

	public EditedInvoiceConfig(Integer configId, String configCode) {
		super(configId);
		this.configCode = configCode;
	}

	public EditedInvoiceConfig(Integer configId) {
		super(configId);
	}

	public String getConfigCode() {
		return configCode;
	}

	public void setConfigCode(String configCode) {
		this.configCode = configCode;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedInvoiceConfig))
			return;
		EditedInvoiceConfig d = (EditedInvoiceConfig) src;
		configCode = d.configCode;
		org = d.org;
		invoiceType = d.invoiceType;
		invoiceTemplate = d.invoiceTemplate;
		taxFlag = d.taxFlag;
		awayFlag = d.awayFlag;
		alertCount = d.alertCount;
		logged = d.logged;
		extdata0 = d.extdata0;
		extdata1 = d.extdata1;
	}

	public int getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}

	public int getTaxFlag() {
		return taxFlag;
	}

	public void setTaxFlag(int taxFlag) {
		this.taxFlag = taxFlag;
	}

	public int getAwayFlag() {
		return awayFlag;
	}

	public void setAwayFlag(int awayFlag) {
		this.awayFlag = awayFlag;
	}

	public int getAlertCount() {
		return alertCount;
	}

	public void setAlertCount(int alertCount) {
		this.alertCount = alertCount;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public String getExtdata0() {
		return extdata0;
	}

	public void setExtdata0(String extdata0) {
		this.extdata0 = extdata0;
	}

	public String getExtdata1() {
		return extdata1;
	}

	public void setExtdata1(String extdata1) {
		this.extdata1 = extdata1;
	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

	public BaseInvoiceTemplate getInvoiceTemplate() {
		return invoiceTemplate;
	}

	public void setInvoiceTemplate(BaseInvoiceTemplate invoiceTemplate) {
		this.invoiceTemplate = invoiceTemplate;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "configCode: " + configCode + ";");
		logger.debug(prefix + "invoiceTemplate: ");
		invoiceTemplate.debug(logger, prefix + "  ");
		logger.debug(prefix + "org: ");
		org.debug(logger, prefix + "");
		logger.debug(prefix + "invoiceType: " + invoiceType + ";");
		logger.debug(prefix + "taxFlag: " + taxFlag + ";");
		logger.debug(prefix + "awayFlag: " + awayFlag + ";");
		logger.debug(prefix + "alertCount: " + alertCount + ";");
		logger.debug(prefix + "logged: " + logged + ";");
		logger.debug(prefix + "extdata0: " + extdata0 + ";");
		logger.debug(prefix + "extdata1: " + extdata1 + ";");

	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedInvoiceConfig();
	}

	@Override
	public String toString() {
		return this.getConfigDesp() + "(" + this.getConfigCode() + ")";
	}

}
