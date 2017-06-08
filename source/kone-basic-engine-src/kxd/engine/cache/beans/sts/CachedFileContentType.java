package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedStringObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存文件服务用户数据
 * 
 * @author zhaom
 * 
 */
public class CachedFileContentType extends CachedStringObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.filecontenttype";
	String contentType;

	public CachedFileContentType() {
		super();
	}

	public CachedFileContentType(String id) {
		super(id);
	}

	public CachedFileContentType(String id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setContentType(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getContentType(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedFileContentType) {
			CachedFileContentType o = (CachedFileContentType) src;
			setContentType(o.contentType);
		}
	}

	@Override
	public IdableObject<String> createObject() {
		return new CachedFileContentType();
	}

	@Override
	public String getText() {
		return contentType;
	}

	@Override
	public void setText(String text) {
		contentType = text;
	}

}
