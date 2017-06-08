package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedShortObject;
import kxd.remote.scs.util.emun.FileCachedType;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存文件服务用户数据
 * 
 * @author zhaom
 * 
 */
public class CachedFileCategory extends CachedShortObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.filecategory";
	private String fileCategoryDesp;
	private FileCachedType cachedType;
	private short fileHost;

	public CachedFileCategory() {
		super();
	}

	public CachedFileCategory(short id) {
		super(id);
	}

	public CachedFileCategory(Short id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getFileCategoryDesp() {
		return fileCategoryDesp;
	}

	public void setFileCategoryDesp(String fileCategoryDesp) {
		this.fileCategoryDesp = fileCategoryDesp;
	}

	public FileCachedType getCachedType() {
		return cachedType;
	}

	public void setCachedType(FileCachedType cachedType) {
		this.cachedType = cachedType;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setFileCategoryDesp(stream.readPacketByteString(3000));
		setCachedType(FileCachedType.valueOf(stream.readByte(3000)));
		setFileHost(stream.readShort(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getFileCategoryDesp(), 3000);
		stream.writeByte((byte) getCachedType().getValue(), 3000);
		stream.writeShort(fileHost, false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedFileCategory) {
			CachedFileCategory o = (CachedFileCategory) src;
			setFileCategoryDesp(o.getFileCategoryDesp());
			setCachedType(o.cachedType);
			setFileHost(o.fileHost);
		}
	}

	@Override
	public IdableObject<Short> createObject() {
		return new CachedFileCategory();
	}

	@Override
	public String getText() {
		return fileCategoryDesp;
	}

	@Override
	public void setText(String text) {
		fileCategoryDesp = text;
	}

	public short getFileHost() {
		return fileHost;
	}

	public void setFileHost(short fileHost) {
		this.fileHost = fileHost;
	}

}
