package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedAppCategory extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.appcategory";
	private String appCategoryCode;
	private String appCategoryDesp;

	public CachedAppCategory() {
		super();
	}

	public CachedAppCategory(int id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedAppCategory(int id) {
		super(id);
	}

	public String getAppCategoryDesp() {
		return appCategoryDesp;
	}

	public void setAppCategoryDesp(String appCategoryDesp) {
		this.appCategoryDesp = appCategoryDesp;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setAppCategoryCode(stream.readPacketByteString(3000));
		setAppCategoryDesp(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(appCategoryCode, 3000);
		stream.writePacketByteString(getAppCategoryDesp(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedAppCategory) {
			CachedAppCategory o = (CachedAppCategory) src;
			setAppCategoryDesp(o.getAppCategoryDesp());
			appCategoryCode = o.appCategoryCode;
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedAppCategory();
	}

	@Override
	public String getText() {
		return appCategoryDesp;
	}

	@Override
	public void setText(String text) {
		appCategoryDesp = text;
	}

	public String getAppCategoryCode() {
		return appCategoryCode;
	}

	public void setAppCategoryCode(String appCategoryCode) {
		this.appCategoryCode = appCategoryCode;
	}

}
