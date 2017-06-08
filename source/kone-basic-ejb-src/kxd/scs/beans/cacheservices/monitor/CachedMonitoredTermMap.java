package kxd.scs.beans.cacheservices.monitor;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.util.emun.AlarmStatus;

public class CachedMonitoredTermMap extends
		ConcurrentHashMap<Integer, CachedMonitoredTerm> {
	private static final long serialVersionUID = 1L;
	final public static ConcurrentHashMap<Integer, CopyOnWriteArrayList<CachedMonitoredTerm>> termTypeTermsMap = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<CachedMonitoredTerm>>();

	public CachedMonitoredTermMap() {
		super();
	}

	public CachedMonitoredTermMap(int initialCapacity, float loadFactor,
			int concurrencyLevel) {
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	public CachedMonitoredTermMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CachedMonitoredTermMap(int initialCapacity) {
		super(initialCapacity);
	}

	public CachedMonitoredTermMap(
			Map<? extends Integer, ? extends CachedMonitoredTerm> m) {
		super(m);
	}

	private void removeTermFromTermType(CachedMonitoredTerm o) {
		CopyOnWriteArrayList<CachedMonitoredTerm> s = termTypeTermsMap.get(o
				.getTermTypeId());
		if (s != null) {
			o.deviceStatusMap.clear();
			s.remove(o);
		}
	}

	private void addTermToTermType(CachedMonitoredTerm o) {
		CopyOnWriteArrayList<CachedMonitoredTerm> s = termTypeTermsMap.get(o
				.getTermTypeId());
		if (s == null) {
			s = new CopyOnWriteArrayList<CachedMonitoredTerm>();
			termTypeTermsMap.put(o.getTermTypeId(), s);
		}
		CachedTermType type = CacheHelper.termTypeMap.get(o.getTermTypeId());
		if (type != null) {
			Enumeration<Integer> en = type.getDeviceMap().keys();
			while (en.hasMoreElements()) {
				CachedDeviceStatus st = new CachedDeviceStatus(en.nextElement());
				st.setMessage("尚未上传状态");
				st.setStatus(0);
				o.getDeviceStatusMap().put(st.getId(), st);
			}
		}
		o.setAlarmStatus(AlarmStatus.NORMAL);
		s.add(o);
	}

	/**
	 * 终端型号增加设备时，调用本函数，修改缓存的终端设备状态
	 * 
	 * @param termTypeId
	 * @param deviceId
	 */
	public void termTypeAddDevice(int termTypeId, int deviceId) {
		CopyOnWriteArrayList<CachedMonitoredTerm> s = termTypeTermsMap
				.get(termTypeId);
		if (s != null) {
			for (CachedMonitoredTerm o : s) {
				CachedDeviceStatus st = new CachedDeviceStatus(deviceId);
				st.setMessage("尚未上传状态");
				st.setStatus(0);
				o.getDeviceStatusMap().put(st.getId(), st);
			}
		}
	}

	public void termTypeDeleteDevice(int termTypeId, int deviceId) {
		CopyOnWriteArrayList<CachedMonitoredTerm> s = termTypeTermsMap
				.get(termTypeId);
		if (s != null) {
			for (CachedMonitoredTerm o : s) {
				o.deviceStatusMap.remove(deviceId);
				o.refresAlarmStatus();
			}
		}
	}

	public CachedMonitoredTerm putOnly(Integer key, CachedMonitoredTerm o) {
		return super.put(key, o);
	}

	@Override
	public CachedMonitoredTerm put(Integer key, CachedMonitoredTerm o) {
		CachedMonitoredTerm old = super.put(key, o);
		if (old != null) {
			if (old.getTermTypeId() != o.getTermTypeId()) {
				// 型号发生变化
				removeTermFromTermType(old);
				addTermToTermType(o);
			}
		} else
			addTermToTermType(o);
		return old;
	}

	@Override
	public CachedMonitoredTerm remove(Object key) {
		CachedMonitoredTerm o = super.remove(key);
		if (o != null) {
			removeTermFromTermType(o);
		}
		return o;
	}

	@Override
	public void clear() {
		termTypeTermsMap.clear();
		super.clear();
	}

	@Override
	public boolean remove(Object key, Object value) {
		if (super.remove(key, value)) {
			removeTermFromTermType((CachedMonitoredTerm) value);
			return true;
		} else
			return false;
	}

	@Override
	public boolean replace(Integer key, CachedMonitoredTerm oldValue,
			CachedMonitoredTerm newValue) {
		if (containsKey(key) && get(key).equals(oldValue)) {
			put(key, newValue);
			return true;
		} else
			return false;
	}

	@Override
	public CachedMonitoredTerm replace(Integer key, CachedMonitoredTerm value) {
		if (containsKey(key)) {
			return put(key, value);
		} else
			return null;
	}

	@Override
	public CachedMonitoredTerm putIfAbsent(Integer key,
			CachedMonitoredTerm value) {
		if (!containsKey(key))
			return put(key, value);
		else
			return get(key);
	}

}
