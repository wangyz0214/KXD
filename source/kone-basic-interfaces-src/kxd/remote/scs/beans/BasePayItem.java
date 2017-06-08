/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.beans;

import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

/**
 * 
 * @author 赵明
 */
public class BasePayItem extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String payItemDesp;
	private boolean needTrade;

	@Override
	public String getText() {
		return payItemDesp;
	}

	@Override
	public void setText(String text) {
		payItemDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + payItemDesp + ";");
	}

	public BasePayItem() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BasePayItem))
			return;
		BasePayItem d = (BasePayItem) src;
		payItemDesp = d.payItemDesp;
		needTrade = d.needTrade;
	}

	@Override
	public IdableObject<Short> createObject() {
		return new BasePayItem();
	}

	public BasePayItem(Short payItemId) {
		super(payItemId);
	}

	public BasePayItem(Short payItemId, String payItemDesp) {
		super(payItemId);
		this.payItemDesp = payItemDesp;
	}

	public Short getPayItemId() {
		return getId();
	}

	public void setPayItemId(Short payItemId) {
		this.setId(payItemId);
	}

	public String getPayItemDesp() {
		return payItemDesp;
	}

	public void setPayItemDesp(String payItemDesp) {
		this.payItemDesp = payItemDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return payItemDesp;
	}

	@Override
	public String toString() {
		return payItemDesp + "(" + getId() + ")";
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
			setId(Short.parseShort(id));
	}

	public boolean isNeedTrade() {
		return needTrade;
	}

	public void setNeedTrade(boolean needTrade) {
		this.needTrade = needTrade;
	}
}
