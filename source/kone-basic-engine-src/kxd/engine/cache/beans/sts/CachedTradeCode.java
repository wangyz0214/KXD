package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.engine.helper.CacheHelper;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存交易代码数据
 * 
 * @author zhaom
 * 
 */
public class CachedTradeCode extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.tradecode";
	private String service, tradeCode;
	private boolean stated, logged;
	private int refundMode;
	private boolean redoEnabled;
	private Integer strikeTradeCodeId;
	private CachedTradeCode strikeTradeCode;
	private String tradeDesp;
	private Short payWayId, payItemId;
	private CachedPayWay payWay;
	private CachedPayItem payItem;
	private int cancelRefundMode;
	private int businessId;
	private CachedBusiness business;

	public CachedTradeCode() {
		super();
	}

	public CachedTradeCode(int id) {
		super(id);
	}

	public CachedTradeCode(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
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

	private long lastPayWayUpdateTime = 0;

	public CachedPayWay getPayWay() {
		if (payWayId != null) {
			if (payWay == null
					|| isRelatedObjectNeedUpdate(lastPayWayUpdateTime)) {
				lastPayWayUpdateTime = System.currentTimeMillis();
				payWay = CacheHelper.payWayMap.get(payWayId);
			}
			return payWay;
		} else
			return null;
	}

	private long lastPayItemUpdateTime = 0;

	public CachedPayItem getPayItem() {
		if (payItemId != null) {
			if (payItem == null
					|| isRelatedObjectNeedUpdate(lastPayItemUpdateTime)) {
				lastPayItemUpdateTime = System.currentTimeMillis();
				payItem = CacheHelper.payItemMap.get(payItemId);
			}
			return payItem;
		} else
			return null;
	}

	public CachedTradeCode getStrikeTradeCode() {
		if (strikeTradeCodeId != null) {
			if (strikeTradeCode == null) {
				strikeTradeCode = CacheHelper.tradeCodeMap
						.get(strikeTradeCodeId);
			}
			return strikeTradeCode;
		} else
			return null;
	}

	public String getTradeDesp() {
		return tradeDesp;
	}

	public void setTradeDesp(String tradeDesp) {
		this.tradeDesp = tradeDesp;
	}

	public Integer getStrikeTradeCodeId() {
		return strikeTradeCodeId;
	}

	public void setStrikeTradeCodeId(Integer strikeTradeCodeId) {
		this.strikeTradeCodeId = strikeTradeCodeId;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setService(stream.readPacketByteString(3000));
		setTradeCode(stream.readPacketByteString(3000));
		setTradeDesp(stream.readPacketByteString(3000));
		setLogged(stream.readBoolean(3000));
		setStated(stream.readBoolean(3000));
		setStrikeTradeCodeId(stream.readInt(false, 3000));
		setPayWayId(stream.readShort(false, 3000));
		setPayItemId(stream.readShort(false, 3000));
		if (strikeTradeCodeId < 0)
			strikeTradeCodeId = null;
		if (payWayId < 0)
			payWayId = null;
		if (payItemId < 0)
			payItemId = null;
		refundMode = stream.readByte(3000);
		redoEnabled = stream.readBoolean(3000);
		cancelRefundMode = stream.readByte(3000);
		businessId = stream.readInt(false, 3000);
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getService(), 3000);
		stream.writePacketByteString(getTradeCode(), 3000);
		stream.writePacketByteString(getTradeDesp(), 3000);
		stream.writeBoolean(isLogged(), 3000);
		stream.writeBoolean(isStated(), 3000);
		if (getStrikeTradeCodeId() == null)
			stream.writeInt(-1, false, 3000);
		else
			stream.writeInt(getStrikeTradeCodeId(), false, 3000);
		if (payWayId == null)
			stream.writeShort((short) -1, false, 3000);
		else
			stream.writeShort(payWayId, false, 3000);
		if (payItemId == null)
			stream.writeShort((short) -1, false, 3000);
		else
			stream.writeShort(payItemId, false, 3000);
		stream.writeByte((byte) refundMode, 3000);
		stream.writeBoolean(redoEnabled, 3000);
		stream.writeByte((byte) cancelRefundMode, 3000);
		stream.writeInt(businessId, false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedTradeCode) {
			CachedTradeCode o = (CachedTradeCode) src;
			setService(o.getService());
			setTradeCode(o.getTradeCode());
			setTradeDesp(o.getTradeDesp());
			setLogged(o.logged);
			setStated(o.stated);
			setStrikeTradeCodeId(o.strikeTradeCodeId);
			payItemId = o.payItemId;
			payWayId = o.payWayId;
			redoEnabled = o.redoEnabled;
			refundMode = o.refundMode;
			cancelRefundMode = o.cancelRefundMode;
			business = o.business;
			businessId = o.businessId;
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedTradeCode();
	}

	@Override
	public String getText() {
		return tradeCode;
	}

	@Override
	public void setText(String text) {
		tradeCode = text;
	}

	public Short getPayWayId() {
		return payWayId;
	}

	public void setPayWayId(Short payWayId) {
		this.payWayId = payWayId;
	}

	public Short getPayItemId() {
		return payItemId;
	}

	public void setPayItemId(Short payItemId) {
		this.payItemId = payItemId;
	}


	public boolean isRedoEnabled() {
		return redoEnabled;
	}

	public void setRedoEnabled(boolean redoEnabled) {
		this.redoEnabled = redoEnabled;
	}

	public int getBusinessId() {
		return businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}

	private long lastBusinessUpdateTime = 0;

	public CachedBusiness getBusiness() {
		if (business == null
				|| isRelatedObjectNeedUpdate(lastBusinessUpdateTime)) {
			lastBusinessUpdateTime = System.currentTimeMillis();
			business = CacheHelper.businessMap.get(businessId);
		}
		return business;
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
