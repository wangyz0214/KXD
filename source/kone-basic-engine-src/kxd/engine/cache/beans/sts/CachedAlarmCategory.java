package kxd.engine.cache.beans.sts;

import java.io.IOException;

import kxd.engine.cache.beans.CachedIntegerObject;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.util.IdableObject;
import kxd.util.stream.Stream;

/**
 * 缓存终端数据
 * 
 * @author zhaom
 * 
 */
public class CachedAlarmCategory extends CachedIntegerObject {
	private static final long serialVersionUID = 1L;
	public final static String KEY_PREFIX = "$cache.alarmcategory";
	private String alarmCategoryDesp;
	private AlarmLevel alarmLevel;

	public CachedAlarmCategory() {
		super();
	}

	public CachedAlarmCategory(int id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedAlarmCategory(int id) {
		super(id);
	}

	public String getAlarmCategoryDesp() {
		return alarmCategoryDesp;
	}

	public void setAlarmCategoryDesp(String alarmCategoryDesp) {
		this.alarmCategoryDesp = alarmCategoryDesp;
	}

	public AlarmLevel getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(AlarmLevel alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	@Override
	public void doReadData(Stream stream) throws IOException {
		setAlarmCategoryDesp(stream.readPacketByteString(3000));
		setAlarmLevel(AlarmLevel.valueOf(stream.readByte(3000)));
	}

	@Override
	public void doWriteData(Stream stream) throws IOException {
		stream.writePacketByteString(getAlarmCategoryDesp(), 3000);
		stream.writeByte((byte) getAlarmLevel().getValue(), 3000);
	}

	@Override
	public void copyData(Object src) {
		if (src instanceof CachedAlarmCategory) {
			CachedAlarmCategory o = (CachedAlarmCategory) src;
			setAlarmCategoryDesp(o.getAlarmCategoryDesp());
			setAlarmLevel(o.getAlarmLevel());
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new CachedAlarmCategory();
	}

	@Override
	public String getText() {
		return alarmCategoryDesp;
	}

	@Override
	public void setText(String text) {
		alarmCategoryDesp = text;
	}

}
