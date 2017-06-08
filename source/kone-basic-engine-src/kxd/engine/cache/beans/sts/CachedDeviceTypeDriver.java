package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedDeviceTypeDriver extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.devicetypedriver";
	private String deviceTypeDriverDesp;
	private String driverFile;

	public CachedDeviceTypeDriver() {
		super();
	}

	public CachedDeviceTypeDriver(int id) {
		super(id);
	}

	public CachedDeviceTypeDriver(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getDeviceTypeDriverDesp() {
		return deviceTypeDriverDesp;
	}

	public void setDeviceTypeDriverDesp(String deviceTypeDriverDesp) {
		this.deviceTypeDriverDesp = deviceTypeDriverDesp;
	}

	public String getDriverFile() {
		return driverFile;
	}

	public void setDriverFile(String driverFile) {
		this.driverFile = driverFile;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setDeviceTypeDriverDesp(stream.readPacketByteString(3000));
		setDriverFile(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getDeviceTypeDriverDesp(), 3000);
		stream.writePacketByteString(getDriverFile(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedDeviceTypeDriver) {
			CachedDeviceTypeDriver o = (CachedDeviceTypeDriver) src;
			setDeviceTypeDriverDesp(o.getDeviceTypeDriverDesp());
			setDriverFile(o.getDriverFile());
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedDeviceTypeDriver();
	}

	@Override
	public String getText() {
		return deviceTypeDriverDesp;
	}

	@Override
	public void setText(String text) {
		deviceTypeDriverDesp = text;
	}

}
