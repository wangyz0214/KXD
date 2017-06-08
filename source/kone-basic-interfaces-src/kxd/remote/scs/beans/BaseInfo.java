package kxd.remote.scs.beans;

import java.util.Date;

import kxd.remote.scs.util.emun.AuditStatus;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseInfo extends ListItem<Long> {
	private static final long serialVersionUID = 1L;
	private BaseOrg org;
	private BaseInfoCategory infoCategory;
	private Date publishTime;
	private String title;
	private AuditStatus auditStatus;
	private String url;
	private String fileName;

	@Override
	public IdableObject<Long> createObject() {
		return new BaseInfo();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseInfo))
			return;
		BaseInfo d = (BaseInfo) src;
		org = d.org;
		infoCategory = d.infoCategory;
		publishTime = d.publishTime;
		title = d.title;
		auditStatus = d.auditStatus;
		url = d.url;
		fileName = d.fileName;
	}

	public BaseInfo() {
		super();
	}

	public BaseInfo(Long id) {
		super(id);
	}

	public Long getInfoId() {
		return getId();
	}

	public void setInfoId(Long id) {
		setId(id);
	}

	@Override
	protected String toDisplayLabel() {
		return infoCategory.getDisplayLabel();
	}

	@Override
	public String toString() {
		return infoCategory.toString();
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
		logger.debug(prefix + "category: " + infoCategory.toString() + ";");
	}

	@Override
	public String getText() {
		return infoCategory.toString();
	}

	@Override
	public void setText(String text) {

	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

	public BaseInfoCategory getInfoCategory() {
		return infoCategory;
	}

	public void setInfoCategory(BaseInfoCategory infoCategory) {
		this.infoCategory = infoCategory;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getUrl() {
		if (url == null) {
			url = "/fileService?filecategory=6&filename=" + fileName;
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
