package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseFileHost extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String hostDesp;
	private String fileRootDir;
	private String httpUrlPrefix;
	private String ftpHost;
	private String ftpUser;
	private String ftpPasswd;
	private String realHttpUrlRoot;

	@Override
	public IdableObject<Short> createObject() {
		return new BaseFileHost();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseFileHost))
			return;
		BaseFileHost d = (BaseFileHost) src;
		hostDesp = d.hostDesp;
		fileRootDir = d.fileRootDir;
		httpUrlPrefix = d.httpUrlPrefix;
		ftpHost = d.ftpHost;
		ftpUser = d.ftpUser;
		ftpPasswd = d.ftpPasswd;
		realHttpUrlRoot = d.realHttpUrlRoot;
	}

	public BaseFileHost() {
		super();
	}

	public BaseFileHost(Short id) {
		super(id);
	}

	public Short getHostId() {
		return getId();
	}

	public void setHostId(Short id) {
		setId(id);
	}

	@Override
	protected String toDisplayLabel() {
		return hostDesp;
	}

	@Override
	public String toString() {
		return hostDesp + "(" + getId() + ")";
	}

	public String getHostDesp() {
		return hostDesp;
	}

	public void setHostDesp(String hostDesp) {
		this.hostDesp = hostDesp;
	}

	public String getFileRootDir() {
		return fileRootDir;
	}

	public void setFileRootDir(String fileRootDir) {
		this.fileRootDir = fileRootDir;
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
		if (id == null || id.trim().isEmpty())
			setId(null);
		else
			setId(Short.parseShort(id));
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + hostDesp + ";");
	}

	@Override
	public String getText() {
		return hostDesp;
	}

	@Override
	public void setText(String text) {
		hostDesp = text;
	}

	public String getHttpUrlPrefix() {
		return httpUrlPrefix;
	}

	public void setHttpUrlPrefix(String httpUrlPrefix) {
		this.httpUrlPrefix = httpUrlPrefix;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPasswd() {
		return ftpPasswd;
	}

	public void setFtpPasswd(String ftpPasswd) {
		this.ftpPasswd = ftpPasswd;
	}

	public String getRealHttpUrlRoot() {
		return realHttpUrlRoot;
	}

	public void setRealHttpUrlRoot(String realHttpUrlRoot) {
		this.realHttpUrlRoot = realHttpUrlRoot;
	}
}
