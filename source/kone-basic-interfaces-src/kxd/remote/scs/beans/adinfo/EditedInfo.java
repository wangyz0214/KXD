package kxd.remote.scs.beans.adinfo;

import java.util.Date;

import kxd.remote.scs.beans.BaseInfo;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class EditedInfo extends BaseInfo {
	private static final long serialVersionUID = 1L;
	private String summary;
	private Date startDate;
	private Date endDate;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
	}

	public EditedInfo() {
		super();
	}

	public EditedInfo(Long id) {
		super(id);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedInfo))
			return;
		EditedInfo d = (EditedInfo) src;
		summary = d.summary;
	}

	@Override
	public IdableObject<Long> createObject() {
		return new EditedInfo();
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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
}
