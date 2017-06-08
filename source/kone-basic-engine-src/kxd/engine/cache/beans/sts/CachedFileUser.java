package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedStringObject;
import kxd.engine.helper.CacheHelper;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存文件服务用户数据
 * 
 * @author zhaom
 * 
 */
public class CachedFileUser extends CachedStringObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.fileuser";
	String fileUserPwd;
	short fileOwnerId;
	CachedFileOwner fileOwner;

	public CachedFileUser() {
		super();
	}

	public CachedFileUser(String id) {
		super(id);
	}

	public CachedFileUser(String id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getFileUserPwd() {
		return fileUserPwd;
	}

	public void setFileUserPwd(String fileUserPwd) {
		this.fileUserPwd = fileUserPwd;
	}

	public CachedFileOwner getFileOwner() {
		if (fileOwner == null) {
			fileOwner = CacheHelper.fileOwnerMap.get(fileOwnerId);
		}
		return fileOwner;
	}

	public short getFileOwnerId() {
		return fileOwnerId;
	}

	public void setFileOwnerId(short fileOwnerId) {
		this.fileOwnerId = fileOwnerId;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setFileUserPwd(stream.readPacketByteString(3000));
		setFileOwnerId(stream.readShort(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getFileUserPwd(), 3000);
		if (getFileOwner() == null)
			stream.writeShort((short) -1, false, 3000);
		else
			stream.writeShort(getFileOwnerId(), false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedFileUser) {
			CachedFileUser o = (CachedFileUser) src;
			setFileUserPwd(o.getFileUserPwd());
			setFileOwnerId(o.getFileOwnerId());
		}
	}

	@Override
	public IdableObject<String> createObject() {
		return new CachedFileUser();
	}

	@Override
	public String getText() {
		return getId();
	}

	@Override
	public void setText(String text) {
		setId(text);
	}

}
