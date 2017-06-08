package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.engine.helper.CacheHelper;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedDevice extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.device";
	/**
	 * 设备名称
	 */
	String deviceName;
	/**
	 * 扩展配置
	 */
	String extConfig;
	/**
	 * 厂商
	 */
	String manufDesp;
	private int deviceTypeId;
	private int deviceDriverId;
	CachedDeviceType deviceType;
	CachedDeviceDriver deviceDriver;

	public CachedDevice() {
		super();
	}

	public CachedDevice(int id) {
		super(id);
	}

	public CachedDevice(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getExtConfig() {
		return extConfig;
	}

	public void setExtConfig(String extConfig) {
		this.extConfig = extConfig;
	}

	public String getManufDesp() {
		return manufDesp;
	}

	public void setManufDesp(String manufDesp) {
		this.manufDesp = manufDesp;
	}

	public int getDeviceTypeId() {
		return deviceTypeId;
	}

	public void setDeviceTypeId(int deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

	public int getDeviceDriverId() {
		return deviceDriverId;
	}

	public void setDeviceDriverId(int deviceDriverId) {
		this.deviceDriverId = deviceDriverId;
	}

	private long lastDeviceTypeUpdateTime = 0;

	public CachedDeviceType getDeviceType() {
		if (deviceType == null
				|| isRelatedObjectNeedUpdate(lastDeviceTypeUpdateTime)) {
			lastDeviceTypeUpdateTime = System.currentTimeMillis();
			deviceType = CacheHelper.deviceTypeMap.get(deviceTypeId);
		}
		return deviceType;
	}

	private long lastDeviceDriverUpdateTime = 0;

	public CachedDeviceDriver getDeviceDriver() {
		if (deviceDriver == null
				|| isRelatedObjectNeedUpdate(lastDeviceDriverUpdateTime)) {
			lastDeviceDriverUpdateTime = System.currentTimeMillis();
			deviceDriver = CacheHelper.deviceDriverMap.get(deviceDriverId);
		}
		return deviceDriver;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setDeviceName(stream.readPacketByteString(3000));
		setExtConfig(stream.readPacketByteString(3000));
		setManufDesp(stream.readPacketByteString(3000));
		setDeviceDriverId(stream.readInt(false, 3000));
		setDeviceTypeId(stream.readInt(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getDeviceName(), 3000);
		stream.writePacketByteString(getExtConfig(), 3000);
		stream.writePacketByteString(getManufDesp(), 3000);
		stream.writeInt(getDeviceDriverId(), false, 3000);
		stream.writeInt(getDeviceTypeId(), false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedDevice) {
			CachedDevice o = (CachedDevice) src;
			setDeviceName(o.getDeviceName());
			setExtConfig(o.getExtConfig());
			setManufDesp(o.getManufDesp());
			setDeviceDriverId(o.getDeviceDriverId());
			setDeviceTypeId(o.getDeviceTypeId());
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedApp();
	}

	@Override
	public String getText() {
		return deviceName;
	}

	@Override
	public void setText(String text) {
		deviceName = text;
	}

}
