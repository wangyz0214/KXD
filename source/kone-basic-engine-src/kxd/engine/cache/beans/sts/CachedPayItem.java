package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedShortObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存交易代码数据
 * 
 * @author zhaom
 * 
 */
public class CachedPayItem extends CachedShortObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.payitem";
	private String payItemDesp;
	private boolean needTrade;
	private long price;
	private String memo;

	public CachedPayItem() {
		super();
	}

	public CachedPayItem(short id) {
		super(id);
	}

	public CachedPayItem(Short id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getPayItemDesp() {
		return payItemDesp;
	}

	public void setPayItemDesp(String payItemDesp) {
		this.payItemDesp = payItemDesp;
	}

	public boolean isNeedTrade() {
		return needTrade;
	}

	public void setNeedTrade(boolean needTrade) {
		this.needTrade = needTrade;
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

	@Override
	public void doReadData(Stream stream) throws IOException {
		setPayItemDesp(stream.readPacketByteString(3000));
		setNeedTrade(stream.readBoolean(3000));
		setPrice(stream.readLong(3000));
		setMemo(stream.readPacketShortString(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getPayItemDesp(), 3000);
		stream.writeBoolean(isNeedTrade(), 3000);
		stream.writeLong(price, 3000);
		stream.writePacketShortString(memo, false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedPayItem) {
			CachedPayItem o = (CachedPayItem) src;
			needTrade = o.needTrade;
			payItemDesp = o.payItemDesp;
			price = o.price;
			memo = o.memo;
		}
	}

	@Override
	public IdableObject<Short> createObject() {
		return new CachedPayItem();
	}

	@Override
	public String getText() {
		return payItemDesp;
	}

	@Override
	public void setText(String text) {
		payItemDesp = text;
	}

}
