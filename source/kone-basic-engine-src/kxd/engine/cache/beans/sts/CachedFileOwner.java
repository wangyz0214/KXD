package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedShortObject;
import kxd.remote.scs.util.emun.FileVisitRight;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存文件服务用户数据
 * 
 * @author zhaom
 * 
 */
public class CachedFileOwner extends CachedShortObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.fileowner";
	private String fileOwnerDesp;
	private FileVisitRight visitRight;

	public CachedFileOwner() {
		super();
	}

	public CachedFileOwner(short id) {
		super(id);
	}

	public CachedFileOwner(Short id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getFileOwnerDesp() {
		return fileOwnerDesp;
	}

	public void setFileOwnerDesp(String fileOwnerDesp) {
		this.fileOwnerDesp = fileOwnerDesp;
	}

	public FileVisitRight getVisitRight() {
		return visitRight;
	}

	public void setVisitRight(FileVisitRight visitRight) {
		this.visitRight = visitRight;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setFileOwnerDesp(stream.readPacketByteString(3000));
		setVisitRight(FileVisitRight.valueOf(stream.readByte(3000)));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getFileOwnerDesp(), 3000);
		stream.writeByte((byte) getVisitRight().getValue(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedFileOwner) {
			CachedFileOwner o = (CachedFileOwner) src;
			setFileOwnerDesp(o.getFileOwnerDesp());
			setVisitRight(o.getVisitRight());
		}
	}

	@Override
	public IdableObject<Short> createObject() {
		return new CachedFileOwner();
	}

	@Override
	public String getText() {
		return fileOwnerDesp;
	}

	@Override
	public void setText(String text) {
		fileOwnerDesp = text;
	}
}
