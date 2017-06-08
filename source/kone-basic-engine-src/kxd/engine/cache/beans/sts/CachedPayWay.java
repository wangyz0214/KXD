package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedShortObject;
import kxd.remote.scs.util.emun.PayWayType;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存交易代码数据
 * 
 * @author zhaom
 * 
 */
public class CachedPayWay extends CachedShortObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.payway";
	private String payWayDesp;
	private boolean needTrade;
	private PayWayType type;

	public CachedPayWay() {
		super();
	}

	public CachedPayWay(short id) {
		super(id);
	}

	public CachedPayWay(Short id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getPayWayDesp() {
		return payWayDesp;
	}

	public void setPayWayDesp(String payWayDesp) {
		this.payWayDesp = payWayDesp;
	}

	public boolean isNeedTrade() {
		return needTrade;
	}

	public void setNeedTrade(boolean needTrade) {
		this.needTrade = needTrade;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setPayWayDesp(stream.readPacketByteString(3000));
		setNeedTrade(stream.readBoolean(3000));
		setType(PayWayType.valueOf(stream.readByte(3000)));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getPayWayDesp(), 3000);
		stream.writeBoolean(isNeedTrade(), 3000);
		stream.writeByte((byte) type.getValue(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedPayWay) {
			CachedPayWay o = (CachedPayWay) src;
			needTrade = o.needTrade;
			payWayDesp = o.payWayDesp;
			type = o.type;
		}
	}

	@Override
	public IdableObject<Short> createObject() {
		return new CachedPayWay();
	}

	@Override
	public String getText() {
		return payWayDesp;
	}

	@Override
	public void setText(String text) {
		payWayDesp = text;
	}

	public PayWayType getType() {
		return type;
	}

	public void setType(PayWayType type) {
		this.type = type;
	}

}
