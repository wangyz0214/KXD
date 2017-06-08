package kxd.engine.scs.monitor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.EjbCacheHelper;
import kxd.remote.scs.util.emun.AlarmStatus;

public class CachedMonitoredTermMap extends
		ConcurrentHashMap<Integer, CachedMonitoredTerm> {
	private static final long serialVersionUID = 1L;
	final private ConcurrentHashMap<Integer, CopyOnWriteArrayList<CachedMonitoredTerm>> termTypeTermsMap = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<CachedMonitoredTerm>>();
	final private ConcurrentHashMap<String, CachedMonitoredTerm> termCodeMap = new ConcurrentHashMap<String, CachedMonitoredTerm>();
	final private static CopyOnWriteArrayList<CachedMonitoredTerm> onlineTermList = new CopyOnWriteArrayList<CachedMonitoredTerm>();

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

	public CachedMonitoredTerm getTermByCode(String code) {
		return termCodeMap.get(code);
	}
	
	//add by snail
	public ArrayList<CachedMonitoredTerm> getTermsByName(String name){
		ArrayList<CachedMonitoredTerm> termList = new ArrayList<CachedMonitoredTerm>();
		for(CachedMonitoredTerm o:termCodeMap.values()){
			TermMonitoredOrgNode org = (TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode
			.getGlobal(o.getOrg().getId());
			if(org.getOrgName().indexOf(name)>=0){
				termList.add(o);
			}
		}
		return termList;
	}
	
	//add by shenyang 2013-03-14
	public CachedMonitoredTerm getTermByIP(String ip) {
		CachedMonitoredTerm cmterm = null;
		for(CachedMonitoredTerm o:termCodeMap.values()){
			CachedTerm term = CacheHelper.termMap.get(o.getId()).getTerm();
			if(term.getIp()!=null && term.getIp()!=""){
				if(term.getIp().indexOf(ip)>=0){
					cmterm = o;
					break;
				}
			}
		}
		return cmterm;
	}

	public synchronized static ArrayList<CachedMonitoredTerm> checkOfflineTerms() {
		ArrayList<CachedMonitoredTerm> ls = new ArrayList<CachedMonitoredTerm>();
		for (CachedMonitoredTerm o : onlineTermList) {
			if (o.getOnlineStatus() != 1) {
				ls.add(o);
			}
		}
		return ls;
	}

	private void removeTermFromTermType(CachedMonitoredTerm o) {
		CopyOnWriteArrayList<CachedMonitoredTerm> s = termTypeTermsMap.get(o
				.getTermTypeId());
		if (s != null) {
			o.clearDevices();
			s.remove(o);
		}
	}

	public CopyOnWriteArrayList<CachedMonitoredTerm> getTermTypeTerms(
			int termType) {
		return termTypeTermsMap.get(termType);
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
				o.removeDevice(deviceId);
			}
		}
	}

	public static synchronized void addOnlineTerm(CachedMonitoredTerm o) {
		if (!onlineTermList.contains(o))
			onlineTermList.add(o);
	}

	public static synchronized void removeOnlineTerm(CachedMonitoredTerm o) {
		if (onlineTermList.contains(o))
			onlineTermList.remove(o);
	}

	public CachedMonitoredTerm putOnly(Integer key, CachedMonitoredTerm o) {
		termCodeMap.put(o.getTermCode(), o);
		return super.put(key, o);
	}

	@Override
	public CachedMonitoredTerm put(Integer key, CachedMonitoredTerm o) {
		addOnlineTerm(o);
		CachedMonitoredTerm old = super.put(key, o);
		if (old != null) {
			if (old.getTermTypeId() != o.getTermTypeId()) {
				// 型号发生变化
				removeTermFromTermType(old);
				addTermToTermType(o);
			}
		} else
			addTermToTermType(o);
		termCodeMap.put(o.getTermCode(), o);
		return old;
	}

	@Override
	public CachedMonitoredTerm remove(Object key) {
		CachedMonitoredTerm o = super.remove(key);
		if (o != null) {
			termCodeMap.remove(o.getTermCode());
			removeTermFromTermType(o);
			removeOnlineTerm(o);
		}
		return o;
	}

	@Override
	public void clear() {
		termTypeTermsMap.clear();
		onlineTermList.clear();
		termCodeMap.clear();
		super.clear();
	}

	@Override
	public boolean remove(Object key, Object value) {
		if (super.remove(key, value)) {
			CachedMonitoredTerm o = (CachedMonitoredTerm) value;
			termCodeMap.remove(o.getTermCode());
			removeTermFromTermType(o);
			removeOnlineTerm(o);
			return true;
		} else
			return false;
	}

	@Override
	public boolean replace(Integer key, CachedMonitoredTerm oldValue,
			CachedMonitoredTerm newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CachedMonitoredTerm replace(Integer key, CachedMonitoredTerm value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CachedMonitoredTerm putIfAbsent(Integer key,
			CachedMonitoredTerm value) {
		if (!containsKey(key)) {
			CachedMonitoredTerm o = (CachedMonitoredTerm) value;
			removeTermFromTermType(o);
			addOnlineTerm(o);
			return put(key, value);
		} else
			return get(key);
	}

}
