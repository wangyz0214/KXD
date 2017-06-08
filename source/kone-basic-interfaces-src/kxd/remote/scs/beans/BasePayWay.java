/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.beans;

import kxd.remote.scs.util.emun.PayWayType;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

/**
 * 
 * @author 赵明
 */
public class BasePayWay extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String payWayDesp;
	private boolean needTrade;
	private PayWayType type;

	@Override
	public String getText() {
		return payWayDesp;
	}

	@Override
	public void setText(String text) {
		payWayDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + payWayDesp + ";");
	}

	public BasePayWay() {
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BasePayWay))
			return;
		BasePayWay d = (BasePayWay) src;
		payWayDesp = d.payWayDesp;
		needTrade = d.needTrade;
		type = d.type;
	}

	@Override
	public IdableObject<Short> createObject() {
		return new BasePayWay();
	}

	public BasePayWay(Short payWay) {
		super(payWay);
	}

	public BasePayWay(Short payWay, String payWayDesp) {
		super(payWay);
		this.payWayDesp = payWayDesp;
	}

	public Short getPayWayId() {
		return getId();
	}

	public void setPayWayId(Short payWayId) {
		this.setId(payWayId);
	}

	public String getPayWayDesp() {
		return payWayDesp;
	}

	public void setPayWayDesp(String payWayDesp) {
		this.payWayDesp = payWayDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return payWayDesp;
	}

	@Override
	public String toString() {
		return payWayDesp + "(" + getId() + ")";
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

	public PayWayType getType() {
		return type;
	}

	public void setType(PayWayType type) {
		this.type = type;
	}

}
