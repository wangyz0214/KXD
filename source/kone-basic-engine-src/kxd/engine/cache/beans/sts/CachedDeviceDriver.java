package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.remote.scs.beans.BaseDeviceDriverFile;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedDeviceDriver extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.devicedriver";
	private String deviceDriverDesp;
	private String driverFile;
	List<BaseDeviceDriverFile> files = new ArrayList<BaseDeviceDriverFile>();

	public CachedDeviceDriver() {
		super();
	}

	public CachedDeviceDriver(int id) {
		super(id);
	}

	public CachedDeviceDriver(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getDeviceDriverDesp() {
		return deviceDriverDesp;
	}

	public void setDeviceDriverDesp(String deviceDriverDesp) {
		this.deviceDriverDesp = deviceDriverDesp;
	}

	public String getDriverFile() {
		return driverFile;
	}

	public void setDriverFile(String driverFile) {
		this.driverFile = driverFile;
	}

	public List<BaseDeviceDriverFile> getFiles() {
		return files;
	}

	public void setFiles(List<BaseDeviceDriverFile> files) {
		this.files = files;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setDeviceDriverDesp(stream.readPacketByteString(3000));
		setDriverFile(stream.readPacketByteString(3000));
		int c = stream.readInt(false, 3000);
		files.clear();
		for (int i = 0; i < c; i++) {
			BaseDeviceDriverFile file = new BaseDeviceDriverFile(
					stream.readInt(false, 3000));
			file.setFileName(stream.readPacketByteString(3000));
			files.add(file);
		}
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getDeviceDriverDesp(), 3000);
		stream.writePacketByteString(getDriverFile(), 3000);
		stream.writeInt(files.size(), false, 3000);
		for (int i = 0; i < files.size(); i++) {
			BaseDeviceDriverFile f = files.get(i);
			stream.writeInt(f.getId(), false, 3000);
			stream.writePacketByteString(f.getFileName(), 3000);
		}
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedDeviceDriver) {
			CachedDeviceDriver o = (CachedDeviceDriver) src;
			setDeviceDriverDesp(o.getDeviceDriverDesp());
			setDriverFile(o.getDriverFile());
			setFiles(o.files);
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedDeviceDriver();
	}

	@Override
	public String getText() {
		return deviceDriverDesp;
	}

	@Override
	public void setText(String text) {
		deviceDriverDesp = text;
	}
}
