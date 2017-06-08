package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.util.emun.CashFlag;
import kxd.remote.scs.util.emun.FixType;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端型号数据
 * 
 * @author zhaom
 * 
 */
public class CachedTermType extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.termtype";
	String typeCode;
	/**
	 * 型号描述
	 */
	String typeDesp;
	/**
	 * 安装类型
	 */
	FixType fixType;
	/**
	 * 现金标志
	 */
	CashFlag cashFlag;
	int appId;
	int manufId;
	String ip;//add by jurstone 20120611
	/**
	 * 型号所属应用
	 */
	CachedApp app;
	/**
	 * 型号所属厂商
	 */
	CachedManuf manuf;
	Hashtable<Integer, CachedTermTypeDevice> deviceMap = new Hashtable<Integer, CachedTermTypeDevice>();

	public CachedTermType() {
		super();
	}

	public CachedTermType(int id) {
		super(id);
	}

	public CachedTermType(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeDesp() {
		return typeDesp;
	}

	public void setTypeDesp(String typeDesp) {
		this.typeDesp = typeDesp;
	}

	public FixType getFixType() {
		return fixType;
	}

	public void setFixType(FixType fixType) {
		this.fixType = fixType;
	}

	public CashFlag getCashFlag() {
		return cashFlag;
	}

	public void setCashFlag(CashFlag cashFlag) {
		this.cashFlag = cashFlag;
	}

	private long lastAppUpdateTime = 0;
	public CachedApp getApp() {
		if (app == null || isRelatedObjectNeedUpdate(lastAppUpdateTime)) {
			lastAppUpdateTime = System.currentTimeMillis();
			app = CacheHelper.appMap.get(appId);
		}
		return app;
	}

	private long lastManufUpdateTime = 0;

	public CachedManuf getManuf() {
		if (manuf == null || isRelatedObjectNeedUpdate(lastManufUpdateTime)) {
			lastManufUpdateTime = System.currentTimeMillis();
			manuf = CacheHelper.manufMap.get(manufId);
		}
		return manuf;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getManufId() {
		return manufId;
	}

	public void setManufId(int manufId) {
		this.manufId = manufId;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setTypeCode(stream.readPacketByteString(3000));
		setTypeDesp(stream.readPacketByteString(3000));
		setFixType(FixType.valueOf(stream.readByte(3000)));
		setCashFlag(CashFlag.valueOf(stream.readByte(3000)));
		setAppId(stream.readInt(false, 3000));
		setManufId(stream.readInt(false, 3000));
		deviceMap.clear();
		int c = stream.readShort(false, 3000);
		for (int i = 0; i < c; i++) {
			CachedTermTypeDevice code = new CachedTermTypeDevice();
			code.readData(stream);
			deviceMap.put(code.getId(), code);
		}
		setIp(stream.readPacketByteString(3000));//add by jurstone 20120611
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getTypeCode(), 3000);
		stream.writePacketByteString(getTypeDesp(), 3000);
		stream.writeByte((byte) getFixType().getValue(), 3000);
		stream.writeByte((byte) getCashFlag().getValue(), 3000);
		stream.writeInt(getAppId(), false, 3000);
		stream.writeInt(getManufId(), false, 3000);
		stream.writeShort((short) deviceMap.size(), false, 3000);
		Enumeration<CachedTermTypeDevice> en = deviceMap.elements();
		while (en.hasMoreElements()) {
			en.nextElement().writeData(stream);
		}
		stream.writePacketByteString(getIp(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedTermType) {
			CachedTermType o = (CachedTermType) src;
			setTypeCode(o.getTypeCode());
			setTypeDesp(o.getTypeDesp());
			setFixType(o.getFixType());
			setCashFlag(o.getCashFlag());
			setAppId(o.getAppId());
			setManufId(o.getManufId());
			setDeviceMap(o.deviceMap);
			setIp(o.getIp());//add by jurstone 20120611
		}
	}

	public Hashtable<Integer, CachedTermTypeDevice> getDeviceMap() {
		return deviceMap;
	}

	public void setDeviceMap(Hashtable<Integer, CachedTermTypeDevice> deviceMap) {
		this.deviceMap = deviceMap;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedTermType();
	}

	@Override
	public String getText() {
		return typeDesp;
	}

	@Override
	public void setText(String text) {
		typeDesp = text;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
