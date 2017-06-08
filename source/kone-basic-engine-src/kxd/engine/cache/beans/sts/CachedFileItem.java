package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.util.Date;

import kxd.engine.cache.beans.CachedStringObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedFileItem extends CachedStringObject {
	private static final long serialVersionUID = 1L;
	private short fileCategoryId, fileHostId, fileOwnerId;
	private String savePath, originalFileName, md5;
	// 下面成员不保存缓存中
	private long lastUpdateTime;
	private long size;
	private Date lastModified;

	public CachedFileItem(String id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedFileItem() {
		super();
	}

	public CachedFileItem(String id) {
		super(id);
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		fileCategoryId = stream.readShort(false, 3000);
		fileHostId = stream.readShort(false, 3000);
		fileOwnerId = stream.readShort(false, 3000);
		savePath = stream.readPacketByteString(3000);
		originalFileName = stream.readPacketByteString(3000);
		md5 = stream.readPacketByteString(3000);
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writeShort(fileCategoryId, false, 3000);
		stream.writeShort(fileHostId, false, 3000);
		stream.writeShort(fileOwnerId, false, 3000);
		stream.writePacketByteString(savePath, 3000);
		stream.writePacketByteString(originalFileName, 3000);
		stream.writePacketByteString(md5, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedFileItem) {
			CachedFileItem o = (CachedFileItem) src;
			fileCategoryId = o.fileCategoryId;
			fileHostId = o.fileHostId;
			fileOwnerId = o.fileOwnerId;
			savePath = o.savePath;
			originalFileName = o.originalFileName;
			md5 = o.md5;
		}
	}

	@Override
	public IdableObject<String> createObject() {
		return new CachedFileItem();
	}

	@Override
	public String getText() {
		return "";
	}

	@Override
	public void setText(String text) {
	}

	public short getFileCategoryId() {
		return fileCategoryId;
	}

	public void setFileCategoryId(short fileCategoryId) {
		this.fileCategoryId = fileCategoryId;
	}

	public short getFileHostId() {
		return fileHostId;
	}

	public void setFileHostId(short fileHostId) {
		this.fileHostId = fileHostId;
	}

	public short getFileOwnerId() {
		return fileOwnerId;
	}

	public void setFileOwnerId(short fileOwnerId) {
		this.fileOwnerId = fileOwnerId;
	}

	public String getSavePath() {
		return savePath;
	}

	public String getRealFileName() {
		if (originalFileName == null || originalFileName.isEmpty())
			return getId();
		else {
			if (savePath == null || savePath.isEmpty())
				return originalFileName;
			else
				return savePath + originalFileName;
		}
	}

	public void setSavePath(String savePath) {
		if (savePath == null)
			savePath = "";
		savePath = savePath.trim();
		if (savePath.startsWith("/"))
			savePath = savePath.substring(1);
		if (!savePath.endsWith("/"))
			savePath += "/";
		this.savePath = savePath;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		if (originalFileName != null) {
			originalFileName = originalFileName.trim();
		}
		if (originalFileName != null && !originalFileName.isEmpty()) {
			if (originalFileName.startsWith("/"))
				originalFileName = originalFileName.substring(1);
			if (originalFileName.endsWith("/"))
				originalFileName = originalFileName.substring(0,
						originalFileName.length() - 1);
		}
		this.originalFileName = originalFileName;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

}
