package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.engine.helper.CacheHelper;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedApp extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.app";
	private String appCode;
	private String appDesp;
	private int appCategoryId;
	private CachedAppCategory appCategory;

	public CachedApp(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedApp() {
		super();
	}

	public CachedApp(int id) {
		super(id);
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppDesp() {
		return appDesp;
	}

	public void setAppDesp(String appDesp) {
		this.appDesp = appDesp;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		appCategoryId = stream.readInt(false, 3000);
		setAppCode(stream.readPacketByteString(3000));
		setAppDesp(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writeInt(appCategoryId, false, 3000);
		stream.writePacketByteString(getAppCode(), 3000);
		stream.writePacketByteString(getAppDesp(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedApp) {
			CachedApp o = (CachedApp) src;
			setAppCode(o.getAppCode());
			setAppDesp(o.getAppDesp());
			this.appCategoryId = o.appCategoryId;
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedApp();
	}

	@Override
	public String getText() {
		return appDesp;
	}

	@Override
	public void setText(String text) {
		appDesp = text;
	}

	public int getAppCategoryId() {
		return appCategoryId;
	}

	public void setAppCategoryId(int appCategoryId) {
		this.appCategoryId = appCategoryId;
	}

	private long lastUpdateTime = 0;

	public CachedAppCategory getAppCategory() {
		if (appCategory == null || isRelatedObjectNeedUpdate(lastUpdateTime)) {
			lastUpdateTime = System.currentTimeMillis();
			appCategory = CacheHelper.appCategoryMap.get(appCategoryId);
		}
		return appCategory;
	}

}
