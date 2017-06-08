package kxd.remote.scs.beans.appdeploy;

import kxd.remote.scs.beans.BasePayItem;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.beans.BaseTradeCode;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class EditedTradeCode extends BaseTradeCode {
	private static final long serialVersionUID = 1L;
	private BasePayWay payWay;
	private BasePayItem payItem;
	private boolean stated;
	private boolean logged;
	private BaseTradeCode strikeTadeCode;
	private int refundMode;
	private boolean redoEnabled;
	private int cancelRefundMode;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "stated: " + stated + ";");
		logger.debug(prefix + "payWay: ");
		payWay.debug(logger, prefix + "  ");
		logger.debug(prefix + "payItem: ");
		payItem.debug(logger, prefix + "  ");
		logger.debug(prefix + "strikeTadeCode: ");
		if (strikeTadeCode == null)
			strikeTadeCode.debug(logger, prefix + "  ");
		else
			logger.debug(prefix + "  {null}");
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedTradeCode))
			return;
		EditedTradeCode d = (EditedTradeCode) src;
		payWay = d.payWay;
		payItem = d.payItem;
		stated = d.stated;
		logged = d.logged;
		strikeTadeCode = d.strikeTadeCode;
		redoEnabled = d.redoEnabled;
		refundMode = d.refundMode;
		cancelRefundMode = d.cancelRefundMode;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new EditedTradeCode();
	}

	public BasePayWay getPayWay() {
		return payWay;
	}

	public void setPayWay(BasePayWay payWay) {
		this.payWay = payWay;
	}

	public BasePayItem getPayItem() {
		return payItem;
	}

	public void setPayItem(BasePayItem payItem) {
		this.payItem = payItem;
	}

	public boolean isStated() {
		return stated;
	}

	public void setStated(boolean stated) {
		this.stated = stated;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public BaseTradeCode getStrikeTadeCode() {
		return strikeTadeCode;
	}

	public void setStrikeTadeCode(BaseTradeCode strikeTadeCode) {
		this.strikeTadeCode = strikeTadeCode;
	}

	@Override
	protected String toDisplayLabel() {
		return super.toDisplayLabel();
	}

	public boolean isRedoEnabled() {
		return redoEnabled;
	}

	public void setRedoEnabled(boolean redoEnabled) {
		this.redoEnabled = redoEnabled;
	}

	public int getRefundMode() {
		return refundMode;
	}

	public void setRefundMode(int refundMode) {
		this.refundMode = refundMode;
	}

	public int getCancelRefundMode() {
		return cancelRefundMode;
	}

	public void setCancelRefundMode(int cancelRefundMode) {
		this.cancelRefundMode = cancelRefundMode;
	}

}
