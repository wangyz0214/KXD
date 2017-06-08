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
public class CachedBusiness extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.business";
	private String businessDesp;
	private int businessCategoryId;
	private CachedBusinessCategory businessCategory;

	public CachedBusiness(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedBusiness() {
		super();
	}

	public CachedBusiness(int id) {
		super(id);
	}

	public String getBusinessDesp() {
		return businessDesp;
	}

	public void setBusinessDesp(String businessDesp) {
		this.businessDesp = businessDesp;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		businessCategoryId = stream.readInt(false, 3000);
		setBusinessDesp(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writeInt(businessCategoryId, false, 3000);
		stream.writePacketByteString(getBusinessDesp(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedBusiness) {
			CachedBusiness o = (CachedBusiness) src;
			businessDesp = o.businessDesp;
			businessCategoryId = o.businessCategoryId;
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedBusiness();
	}

	@Override
	public String getText() {
		return businessDesp;
	}

	@Override
	public void setText(String text) {
		businessDesp = text;
	}

	public int getBusinessCategoryId() {
		return businessCategoryId;
	}

	public void setBusinessCategoryId(int businessCategoryId) {
		this.businessCategoryId = businessCategoryId;
	}

	private long lastUpdateTime = 0;

	public CachedBusinessCategory getBusinessCategory() {
		if (businessCategory == null
				|| isRelatedObjectNeedUpdate(lastUpdateTime)) {
			lastUpdateTime = System.currentTimeMillis();
			businessCategory = CacheHelper.businessCategoryMap
					.get(businessCategoryId);
		}
		return businessCategory;
	}

}
