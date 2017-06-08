package kxd.remote.scs.beans;

import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class BaseAlarmCategory extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String alarmCategoryDesp;
	private AlarmLevel alarmLevel;

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseAlarmCategory();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseAlarmCategory))
			return;
		BaseAlarmCategory d = (BaseAlarmCategory) src;
		alarmCategoryDesp = d.alarmCategoryDesp;
		alarmLevel = d.alarmLevel;
	}

	public BaseAlarmCategory() {
		super();
	}

	public BaseAlarmCategory(Integer id) {
		super(id);
	}

	public Integer getAlarmCategoryId() {
		return getId();
	}

	public void setAlarmCategoryId(Integer id) {
		setId(id);
	}

	public String getAlarmCategoryDesp() {
		return alarmCategoryDesp;
	}

	public void setAlarmCategoryDesp(String alarmCategoryDesp) {
		this.alarmCategoryDesp = alarmCategoryDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return alarmCategoryDesp;
	}

	@Override
	public String toString() {
		return alarmCategoryDesp + "(" + getId() + ")";
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
			setId(Integer.valueOf(id));
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + getAlarmCategoryDesp() + ";");
		logger.debug(prefix + "level: " + getAlarmLevel() + ";");
	}

	public AlarmLevel getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(AlarmLevel alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	@Override
	public String getText() {
		return alarmCategoryDesp;
	}

	@Override
	public void setText(String text) {
		alarmCategoryDesp = text;
	}
}
