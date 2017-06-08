package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseFileItem extends ListItem<String> {
	private static final long serialVersionUID = 1L;
	private short fileCategoryId, fileHostId, fileOwnerId;
	private String savePath, originalFileName;
	private String md5;

	@Override
	public IdableObject<String> createObject() {
		return new BaseFileItem();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseFileItem))
			return;
		BaseFileItem d = (BaseFileItem) src;
		fileCategoryId = d.fileCategoryId;
		fileHostId = d.fileHostId;
		fileOwnerId = d.fileOwnerId;
		originalFileName = d.originalFileName;
		savePath = d.savePath;
		md5 = d.md5;
	}

	public BaseFileItem() {
		super();
	}

	public BaseFileItem(String id) {
		super(id);
	}

	public String getFileItemId() {
		return getId();
	}

	public void setFileItemId(String id) {
		setId(id);
	}

	@Override
	protected String toDisplayLabel() {
		return getId();
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(id);
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
	}

	@Override
	public String getText() {
		return getId();
	}

	@Override
	public void setText(String text) {
		setId(text);
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
			return getFileItemId();
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
		if (!savePath.startsWith("/"))
			savePath = "/" + savePath;
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

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

}
