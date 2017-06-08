package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

public class BaseInfoFile extends ListItem<Long> {

	private static final long serialVersionUID = 1L;
	private Long infoId;
	private String fileName;
	private int fileType;
	private String url;

	public BaseInfoFile() {
	}

	public BaseInfoFile(Long id) {
		super(id);
	}

	public Long getInfoFileId() {
		return getId();
	}

	public void setInfoFileId(Long infoFileId) {
		setId(infoFileId);
	}

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (src instanceof BaseInfoFile) {
			BaseInfoFile d = (BaseInfoFile) src;
			fileName = d.fileName;
			infoId = d.infoId;
			fileType = d.fileType;
			url = d.url;
		}
	}

	@Override
	public IdableObject<Long> createObject() {
		return new BaseInfoFile();
	}

	@Override
	public String getIdString() {
		return Long.toString(getId());
	}

	@Override
	public void setIdString(String id) {
		setId(Long.valueOf(id));
	}

	@Override
	protected String toDisplayLabel() {
		return fileName;
	}

	@Override
	public String getText() {
		return fileName;
	}

	@Override
	public void setText(String text) {
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
