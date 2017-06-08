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
public class CachedBusinessCategory extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.businesscategory";
	private String businessCategoryDesp;

	public CachedBusinessCategory() {
		super();
	}

	public CachedBusinessCategory(int id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedBusinessCategory(int id) {
		super(id);
	}

	public String getBusinessCategoryDesp() {
		return businessCategoryDesp;
	}

	public void setBusinessCategoryDesp(String businessCategoryDesp) {
		this.businessCategoryDesp = businessCategoryDesp;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setBusinessCategoryDesp(stream.readPacketByteString(3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getBusinessCategoryDesp(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedBusinessCategory) {
			CachedBusinessCategory o = (CachedBusinessCategory) src;
			setBusinessCategoryDesp(o.getBusinessCategoryDesp());
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedBusinessCategory();
	}

	@Override
	public String getText() {
		return businessCategoryDesp;
	}

	@Override
	public void setText(String text) {
		businessCategoryDesp = text;
	}

}
