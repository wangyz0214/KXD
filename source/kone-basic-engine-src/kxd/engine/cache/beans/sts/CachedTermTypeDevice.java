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
public class CachedTermTypeDevice extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	private CachedDevice device;
	private int port;
	private String extConfig;

	public CachedTermTypeDevice() {
		super();
	}

	public CachedTermTypeDevice(int id) {
		super(id);
	}

	public CachedTermTypeDevice(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	private long lastDeviceUpdateTime = 0;

	public CachedDevice getDevice() {
		if (device == null || isRelatedObjectNeedUpdate(lastDeviceUpdateTime)) {
			lastDeviceUpdateTime = System.currentTimeMillis();
			device = CacheHelper.deviceMap.get(getId());
		}
		return device;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getExtConfig() {
		return extConfig;
	}

	public void setExtConfig(String extConfig) {
		this.extConfig = extConfig;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setId(stream.readInt(false, 3000));
		setPort(stream.readInt(false, 3000));
		setExtConfig(stream.readPacketShortString(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writeInt(getId(), false, 3000);
		stream.writeInt(port, false, 3000);
		stream.writePacketShortString(getExtConfig(), false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedTermTypeDevice) {
			CachedTermTypeDevice o = (CachedTermTypeDevice) src;
			setExtConfig(o.extConfig);
			setPort(o.port);
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedTermTypeDevice();
	}

	@Override
	public String getText() {
		return Integer.toString(port);

	}

	@Override
	public void setText(String text) {
		port = Integer.valueOf(text);
	}
}
