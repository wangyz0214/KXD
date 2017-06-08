package kxd.engine.fileservice;

import java.io.Serializable;

public class FileProperty implements Serializable {
	private static final long serialVersionUID = 3864661716422756421L;
	private String fileName;
	private int fileCategory;
	private String lastModified;
	private long size;
	private String md5;

	public FileProperty(int fileCategory, String fileName, String lastModified,
			long size, String md5) {
		super();
		this.fileName = fileName;
		this.fileCategory = fileCategory;
		this.lastModified = lastModified;
		this.size = size;
		this.md5 = md5;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileCategory() {
		return fileCategory;
	}

	public void setFileCategory(int fileCategory) {
		this.fileCategory = fileCategory;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

}
