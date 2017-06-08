package kxd.remote.scs.beans;

import kxd.remote.scs.util.emun.AuditStatus;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BasePrintAd extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private BaseOrg org;
	private BasePrintAdCategory adCategory;
	private AuditStatus auditStatus;

	@Override
	public IdableObject<Integer> createObject() {
		return new BasePrintAd();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BasePrintAd))
			return;
		BasePrintAd d = (BasePrintAd) src;
		adCategory = d.adCategory;
		org = d.org;
		auditStatus = d.auditStatus;
	}

	public BasePrintAd() {
		super();
	}

	public BasePrintAd(Integer id) {
		super(id);
	}

	public Integer getAdId() {
		return getId();
	}

	public void setAdId(Integer id) {
		setId(id);
	}

	public BasePrintAdCategory getAdCategory() {
		return adCategory;
	}

	public void setAdCategory(BasePrintAdCategory adCategory) {
		this.adCategory = adCategory;
	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

	@Override
	protected String toDisplayLabel() {
		return adCategory.getDisplayLabel();
	}

	@Override
	public String toString() {
		return adCategory.toString();
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
		logger.debug(prefix + "category: " + adCategory.toString() + ";");
	}

	@Override
	public String getText() {
		return adCategory.toString();
	}

	@Override
	public void setText(String text) {

	}

	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}
}
