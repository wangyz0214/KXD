package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedShortObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存文件服务用户数据
 * 
 * @author zhaom
 * 
 */
public class CachedFileHost extends CachedShortObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.filehost";
	private String hostDesp;
	private String fileRootDir;
	private String httpUrlPrefix;
	private String ftpHost;
	private String ftpUser;
	private String ftpPasswd;
	private String realHttpUrlRoot;

	public CachedFileHost() {
		super();
	}

	public CachedFileHost(short id) {
		super(id);
	}

	public CachedFileHost(Short id, boolean isNullValue) {
		super(id, isNullValue);
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
	public void doReadData(Stream stream) throws IOException {
		setHostDesp(stream.readPacketByteString(3000));
		setFileRootDir(stream.readPacketByteString(3000));
		setHttpUrlPrefix(stream.readPacketByteString(3000));
		setRealHttpUrlRoot(stream.readPacketByteString(3000));
		setFtpHost(stream.readPacketByteString(3000));
		setFtpUser(stream.readPacketByteString(3000));
		setFtpPasswd(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getHostDesp(), 3000);
		stream.writePacketByteString(getFileRootDir(), 3000);
		stream.writePacketByteString(getHttpUrlPrefix(), 3000);
		stream.writePacketByteString(getRealHttpUrlRoot(), 3000);
		stream.writePacketByteString(getFtpHost(), 3000);
		stream.writePacketByteString(getFtpUser(), 3000);
		stream.writePacketByteString(getFtpPasswd(), 3000);

	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedFileHost) {
			CachedFileHost o = (CachedFileHost) src;
			setFileRootDir(o.fileRootDir);
			setHostDesp(o.hostDesp);
			httpUrlPrefix = o.httpUrlPrefix;
			ftpHost = o.ftpHost;
			ftpUser = o.ftpUser;
			ftpPasswd = o.ftpPasswd;
			realHttpUrlRoot = o.realHttpUrlRoot;
		}
	}

	@Override
	public IdableObject<Short> createObject() {
		return new CachedFileHost();
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
		if (httpUrlPrefix != null) {
			httpUrlPrefix = httpUrlPrefix.trim();
			if (httpUrlPrefix.isEmpty())
				httpUrlPrefix = null;
		}
		this.httpUrlPrefix = httpUrlPrefix;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		if (ftpHost != null) {
			ftpHost = ftpHost.trim();
			if (ftpHost.isEmpty())
				ftpHost = null;
		}
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

	public boolean isLocalStored() {
		return httpUrlPrefix == null || ftpHost == null;
	}

	public String getRealHttpUrlRoot() {
		return realHttpUrlRoot;
	}

	public void setRealHttpUrlRoot(String realHttpUrlRoot) {
		this.realHttpUrlRoot = realHttpUrlRoot;
	}
}
