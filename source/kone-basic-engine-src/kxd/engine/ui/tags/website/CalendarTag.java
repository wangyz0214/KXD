package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import kxd.util.DateTime;

public class CalendarTag extends DropdownTag {
	private static final long serialVersionUID = 1L;
	private String startDate, endDate;
	private String startDateId, endDateId;
	private boolean timeSelectEnabled;

	@Override
	public void release() {
		startDate = null;
		endDate = null;
		startDateId = null;
		endDateId = null;
		super.release();
	}

	@Override
	protected void writeButton(JspWriter writer) throws JspTagException,
			IOException {
		super.writeButton(writer);
		if (getStartDate() != null) {
			writeText(writer, ",'startdate':'" + getStartDate() + "'");
		}
		if (getEndDate() != null) {
			writeText(writer, ",'enddate':'" + getEndDate() + "'");
		}
		if (getStartDateId() != null) {
			writeText(writer, ",'startcalendar':" + getStartDateId());
		}
		writeText(writer, ",'timeselectenabled':" + isTimeSelectEnabled());
	}

	@Override
	protected void writePopupWindow(JspWriter writer) throws JspTagException,
			IOException {
		super.writePopupWindow(writer);
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStartDateId() {
		return startDateId;
	}

	public void setStartDateId(String startDateId) {
		this.startDateId = startDateId;
	}

	public String getEndDateId() {
		return endDateId;
	}

	@Override
	protected String getDropdownClass() {
		return "Calendar";
	}

	public void setEndDateId(String endDateId) {
		this.endDateId = endDateId;
	}

	@Override
	public Object getValue() {
		Object v = super.getValue();
		if (v == null)
			try {
				setValue(new DateTime().format("yyyy-MM-dd"));
			} catch (JspException e) {
			}
		return super.getValue();
	}

	public boolean isTimeSelectEnabled() {
		return timeSelectEnabled;
	}

	public void setTimeSelectEnabled(boolean timeSelectEnabled) {
		this.timeSelectEnabled = timeSelectEnabled;
	}
}
