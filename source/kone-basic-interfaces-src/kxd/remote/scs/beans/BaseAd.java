package kxd.remote.scs.beans;

import java.util.Date;

import kxd.remote.scs.util.emun.AdPriority;
import kxd.remote.scs.util.emun.AdStoreType;
import kxd.remote.scs.util.emun.AuditStatus;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseAd extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private BaseAdCategory adCategory;
	private String adDesp;
	private Date startDate;
	private Date endDate;
	private String fileName;
	private int playTimes;
	private int duration;
	private AuditStatus auditStatus;
	private String url;
	private AdStoreType storeType;
	private AdPriority priority;
	boolean uploadComplete;

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseAd();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseAd))
			return;
		BaseAd d = (BaseAd) src;
		adCategory = d.adCategory;
		adDesp = d.adDesp;
		startDate = d.startDate;
		endDate = d.endDate;
		fileName = d.fileName;
		playTimes = d.playTimes;
		duration = d.duration;
		auditStatus = d.auditStatus;
		url = d.url;
		uploadComplete = d.uploadComplete;
		storeType = d.storeType;
		priority = d.priority;
	}

	public BaseAd() {
		super();
	}

	public BaseAd(Integer id) {
		super(id);
	}

	public Integer getAdId() {
		return getId();
	}

	public void setAdId(Integer id) {
		setId(id);
	}

	public BaseAdCategory getAdCategory() {
		return adCategory;
	}

	public void setAdCategory(BaseAdCategory adCategory) {
		this.adCategory = adCategory;
	}

	public String getAdDesp() {
		return adDesp;
	}

	public void setAdDesp(String adDesp) {
		this.adDesp = adDesp;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(int playTimes) {
		this.playTimes = playTimes;
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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getUrl() {
		if (url == null) {
			url = "/fileService?filecategory=4&filename=" + fileName;
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOriginalUrl() {
		return url;
	}

	public boolean isUploadComplete() {
		return uploadComplete;
	}

	public void setUploadComplete(boolean uploadComplete) {
		this.uploadComplete = uploadComplete;
	}

	public AdStoreType getStoreType() {
		return storeType;
	}

	public void setStoreType(AdStoreType storeType) {
		this.storeType = storeType;
	}

	public AdPriority getPriority() {
		return priority;
	}

	public void setPriority(AdPriority priority) {
		this.priority = priority;
	}
}
