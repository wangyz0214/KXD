package kxd.remote.scs.beans.appdeploy;

import kxd.remote.scs.beans.BasePayItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class EditedPayItem extends BasePayItem {
	private static final long serialVersionUID = 1L;
	private long price;
	private String memo;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug( prefix + "price: " + price + ";");
		logger.debug( prefix + "memo: [" + memo + "];");
	}

	public EditedPayItem() {
		super();
	}

	public EditedPayItem(Short payItemId, String payItemDesp) {
		super(payItemId, payItemDesp);
	}

	public EditedPayItem(Short payItemId) {
		super(payItemId);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof EditedPayItem))
			return;
		EditedPayItem d = (EditedPayItem) src;
		price = d.price;
		memo = d.memo;
	}

	@Override
	public IdableObject<Short> createObject() {
		return new EditedPayItem();
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
