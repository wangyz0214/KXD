package kxd.remote.scs.beans;

import java.util.Date;

import kxd.remote.scs.util.emun.BusinessOpenCloseMode;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseOrgBusinessOpenClose extends ListItem<Long> {
	private static final long serialVersionUID = 1L;
	private int orgId;
	private Date startDate, endDate;
	private String openTimes;
	private BusinessOpenCloseMode openMode;
	private String businessCategoryIds, businessIds, payWays, payItems;
	private String reason;

	@Override
	public IdableObject<Long> createObject() {
		return new BaseOrgBusinessOpenClose();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseOrgBusinessOpenClose))
			return;
		BaseOrgBusinessOpenClose d = (BaseOrgBusinessOpenClose) src;
		orgId = d.orgId;
		startDate = d.startDate;
		endDate = d.endDate;
		openTimes = d.openTimes;
		openMode = d.openMode;
		businessCategoryIds = d.businessCategoryIds;
		businessIds = d.businessIds;
		reason = d.reason;
		payItems = d.payItems;
		payWays = d.payWays;

	}

	public BaseOrgBusinessOpenClose() {
		super();
	}

	public BaseOrgBusinessOpenClose(Long id) {
		super(id);
	}

	@Override
	protected String toDisplayLabel() {
		return reason;
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
			setId(Long.valueOf(id));
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
	}

	@Override
	public String getText() {
		return reason;
	}

	@Override
	public void setText(String text) {
		this.reason = text;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getOpenTimes() {
		return openTimes;
	}

	public void setOpenTimes(String openTimes) {
		this.openTimes = openTimes;
	}

	public BusinessOpenCloseMode getOpenMode() {
		return openMode;
	}

	public void setOpenMode(BusinessOpenCloseMode openMode) {
		this.openMode = openMode;
	}

	public String getBusinessCategoryIds() {
		return businessCategoryIds;
	}

	public void setBusinessCategoryIds(String businessCategoryIds) {
		this.businessCategoryIds = businessCategoryIds;
	}

	public String getBusinessIds() {
		return businessIds;
	}

	public void setBusinessIds(String businessIds) {
		this.businessIds = businessIds;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPayWays() {
		return payWays;
	}

	public void setPayWays(String payWays) {
		this.payWays = payWays;
	}

	public String getPayItems() {
		return payItems;
	}

	public void setPayItems(String payItems) {
		this.payItems = payItems;
	}
}
