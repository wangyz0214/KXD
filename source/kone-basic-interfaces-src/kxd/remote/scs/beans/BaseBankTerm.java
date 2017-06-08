package kxd.remote.scs.beans;

import kxd.remote.scs.beans.device.EditedBankTerm;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseBankTerm extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String bankTermCode;
	private String bankTermDesp;
	private String merchantAccount;

	public String getMerchantAccount() {
		return merchantAccount;
	}

	public void setMerchantAccount(String merchantAccount) {
		this.merchantAccount = merchantAccount;
	}

	@Override
	public String getText() {
		return bankTermDesp;
	}

	@Override
	public void setText(String text) {
		bankTermDesp = text;
	}

	public BaseBankTerm() {
		super();
	}

	public BaseBankTerm(Integer id) {
		super(id);
	}

	public BaseBankTerm(Integer id, String termCode, String termDesp,String merchantAccount) {
		super(id);
		this.bankTermCode = termCode;
		this.bankTermDesp = termDesp;
		this.merchantAccount=merchantAccount;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseBankTerm))
			return;
		BaseBankTerm d = (BaseBankTerm) src;
		bankTermCode = d.bankTermCode;
		bankTermDesp = d.bankTermDesp;
		merchantAccount=d.merchantAccount;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseBankTerm();
	}

	public Integer getBankTermId() {
		return getId();
	}

	public void setBankTermId(Integer id) {
		setId(id);
	}

	public String getBankTermCode() {
		return bankTermCode;
	}

	public void setBankTermCode(String termCode) {
		this.bankTermCode = termCode;
	}

	public String getBankTermDesp() {
		return bankTermDesp;
	}

	public void setBankTermDesp(String termDesp) {
		this.bankTermDesp = termDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return bankTermDesp;
	}

	@Override
	public String toString() {
		return bankTermDesp + "(" + getBankTermCode() + ")";
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
	public void assignToBankTerm(EditedBankTerm edit){
		setId(edit.getId());
		setBankTermCode(edit.getBankTermCode());
		setBankTermDesp(edit.getBankTermDesp());
		setMerchantAccount(edit.getMerchantAccount());
	}
	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "code: " + getBankTermCode() + ";");
		logger.debug(prefix + "desp: " + getBankTermDesp() + ";");
	}
}
