package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

/**
 * 
 * @author 赵明
 */
public class BaseDisabledPrintUser extends ListItem<Long> {
	private static final long serialVersionUID = 1L;
	private String userno;

	@Override
	public String getText() {
		return userno;
	}

	@Override
	public void setText(String text) {
		userno = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + userno + ";");
	}

	public BaseDisabledPrintUser() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseDisabledPrintUser))
			return;
		BaseDisabledPrintUser d = (BaseDisabledPrintUser) src;
		userno = d.userno;
	}

	@Override
	public IdableObject<Long> createObject() {
		return new BaseDisabledPrintUser();
	}

	public BaseDisabledPrintUser(Long roleId) {
		super(roleId);
	}

	public BaseDisabledPrintUser(Long roleId, String userno) {
		super(roleId);
		this.userno = userno;
	}

	public Long getUserId() {
		return getId();
	}

	public void setUserId(Long roleId) {
		this.setId(roleId);
	}

	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	@Override
	protected String toDisplayLabel() {
		return userno;
	}

	@Override
	public String toString() {
		return userno;
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
}
