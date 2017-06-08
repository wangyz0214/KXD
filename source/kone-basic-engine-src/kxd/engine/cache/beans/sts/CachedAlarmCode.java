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
public class CachedAlarmCode extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	private int alarmCategoryId;
	protected String alarmDesp;
	private CachedAlarmCategory alarmCategory;

	public CachedAlarmCode(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedAlarmCode() {
		super();
	}

	public CachedAlarmCode(int id) {
		super(id);
	}

	public int getAlarmCode() {
		return getId();
	}

	public void setAlarmCode(int alarmCode) {
		setId(alarmCode);
	}

	public int getAlarmCategoryId() {
		return alarmCategoryId;
	}

	public void setAlarmCategoryId(int alarmCategoryId) {
		this.alarmCategoryId = alarmCategoryId;
	}

	private long lastAlarmCategoryUpdateTime = 0;

	public CachedAlarmCategory getAlarmCategory() {
		if (alarmCategory == null
				|| isRelatedObjectNeedUpdate(lastAlarmCategoryUpdateTime)) {
			lastAlarmCategoryUpdateTime = System.currentTimeMillis();
			alarmCategory = CacheHelper.alarmCategoryMap.get(alarmCategoryId);
		}
		return alarmCategory;
	}

	public String getAlarmDesp() {
		return alarmDesp;
	}

	public void setAlarmDesp(String alarmDesp) {
		this.alarmDesp = alarmDesp;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setId(stream.readInt(false, 3000));
		setAlarmDesp(stream.readPacketByteString(3000));
		setAlarmCategoryId(stream.readInt(false, 3000));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writeInt(getId(), false, 3000);
		stream.writePacketByteString(getAlarmDesp(), 3000);
		stream.writeInt(alarmCategoryId, false, 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedAlarmCode) {
			CachedAlarmCode o = (CachedAlarmCode) src;
			setAlarmCategoryId(o.alarmCategoryId);
			setAlarmDesp(o.alarmDesp);
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedAlarmCode();
	}

	@Override
	public String getText() {
		return alarmDesp;
	}

	@Override
	public void setText(String text) {
		alarmDesp = text;
	}
}
