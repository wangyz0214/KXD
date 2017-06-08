package kxd.scs.dao.cache.converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.cache.beans.sts.CachedTermTypeDevice;
import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.monitor.CachedMonitoredTerm;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.FaultProcFlag;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.DateTime;

import org.apache.log4j.Logger;

public class CachedMonitoredTermConverter extends
		BaseDaoConverter<CachedMonitoredTerm> {
	final static Logger logger = Logger
			.getLogger(CachedMonitoredTermConverter.class);

	public CachedMonitoredTermConverter() {
	}

	public CachedTerm convertToCachedTerm(Object result) {
		Object[] o = (Object[]) result;
		CachedTerm term = new CachedTerm();
		term.setId(Integer.valueOf(o[0].toString()));
		term.setTermCode(o[1].toString());
		term.setTermDesp(o[2].toString());
		term.setTermTypeId(Integer.valueOf(o[3].toString()));
		term.setAppId(Integer.valueOf(o[4].toString()));
		if (o[5] == null)
			term.setBankTermId(null);
		else
			term.setBankTermId(Integer.valueOf(o[5].toString()));
		term.setOrgId(Integer.valueOf(o[6].toString()));
		term.setAlarmStatus(AlarmStatus.valueOfIntString(o[7]));
		String time = (String) o[10];
		if (time != null)
			term.setLastInlineTime(DateTime.parseDate((String) time,
					"yyyyMMddHHmmss").getTime());
		else
			term.setLastInlineTime(0);
		term.setLoginSessionID((String) o[12]);
		term.setStatus(TermStatus.valueOfIntString(o[14]));
		term.setAddress((String) o[15]);
		term.setAreaCode((String) o[16]);
		term.setOpenTime((String) o[17]);
		term.setCloseTime((String) o[18]);
		term.setContacter((String) o[19]);
		term.setDayRunTime(Short.valueOf(o[20].toString()));
		term.setGuid((String) o[21]);
		term.setManufNo((String) o[22]);
		if (o[23] == null)
			term.setStartTime(0);
		else
			term.setStartTime(((Date) o[23]).getTime());
		term.setWorkKey((String) o[24]);
		term.setExtField0((String) o[25]);
		term.setExtField1((String) o[26]);
		term.setExtField2((String) o[27]);
		term.setExtField3((String) o[28]);
		term.setExtField4((String) o[29]);
		term.setSettlementType(SettlementType.valueOfIntString(o[30]));
		return term;
	}

	public CachedMonitoredTerm doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedMonitoredTerm term = new CachedMonitoredTerm();
		term.setId(Integer.valueOf(o[0].toString()));
		term.setTermCode(o[1].toString());
		term.setTermDesp(o[2].toString());
		term.setTermTypeId(Integer.valueOf(o[3].toString()));
		term.setAppId(Integer.valueOf(o[4].toString()));
		term.setOrg(new BaseOrg(Integer.valueOf(o[6].toString())));
		term.setAlarmStatus(AlarmStatus.valueOfIntString(o[7]));
		term.setLastFaultRecordTime(DateTime.parseDate((String) o[8],
				"yyyyMMddHHmmss"));
		term.setLastFaultTime(DateTime.parseDate((String) o[9],
				"yyyyMMddHHmmss"));
		String time = (String) o[10];
		if (time != null)
			term.setLastOnlineTime(DateTime.parseDate((String) time,
					"yyyyMMddHHmmss"));
		else
			term.setLastOnlineTime(new Date(0));
		term.setLastProcessTime(DateTime.parseDate((String) o[11],
				"yyyyMMddHHmmss"));
		term.setProcessFlag(FaultProcFlag.valueOfIntString(o[13]));
		term.setStatus(TermStatus.valueOfIntString(o[14]));
		term.setManufId(Integer.valueOf(o[34].toString()));
		term.setSettlementType(SettlementType.valueOfIntString(o[36]));
		//term.setIp(o[37].toString());//add by jurstone 20120611
		return term;
	}

	private void processTermStatus(CachedMonitoredTerm term) {
		if (term.getDeviceStatusMap().size() == 0) {
			CachedTermType type = CacheHelper.termTypeMap.get(term
					.getTermTypeId());
			if (type != null) {
				Enumeration<Integer> en = type.getDeviceMap().keys();
				while (en.hasMoreElements()) {
					CachedDeviceStatus st = new CachedDeviceStatus(
							en.nextElement());
					st.setMessage("尚未上传状态");
					st.setStatus(0);
					st.setNotUploadStatus(true);
					st.setLevel(AlarmLevel.NORMAL);
					term.getDeviceStatusMap().put(st.getId(), st);
				}
			}
			term.setAlarmStatus(AlarmStatus.NORMAL);
		} else {
			Enumeration<CachedDeviceStatus> en = term.getDeviceStatusMap()
					.elements();
			AlarmStatus status = AlarmStatus.NORMAL;
			while (en.hasMoreElements()
					&& status.getValue() < AlarmStatus.FAULT.getValue()) {
				CachedDeviceStatus o = en.nextElement();
				if (o.getStatus() < 0) {
					status = AlarmStatus.FAULT;
					break;
				} else if (o.getStatus() > 0 && o.getStatus() < 1000)
					status = AlarmStatus.ALARM;
			}
			term.setAlarmStatus(status);
		}
	}

	public List<CachedMonitoredTerm> convert(List<?> results) {
		List<CachedMonitoredTerm> terms = new ArrayList<CachedMonitoredTerm>();
		List<CachedTerm> cachedTerms = new ArrayList<CachedTerm>();
		Iterator<?> it = results.iterator();
		CachedMonitoredTerm curTerm = null;
		CachedTerm curCachedTerm = null;
		logger.debug("monitor terms convert starting...");
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			if (curTerm == null) {
				curTerm = doConvert(o);
				curCachedTerm = convertToCachedTerm(o);
			} else {
				CachedMonitoredTerm term = doConvert(o);
				curCachedTerm = convertToCachedTerm(o);
				if (!term.getId().equals(curTerm.getId())) {
					terms.add(curTerm);
					cachedTerms.add(curCachedTerm);
					processTermStatus(curTerm);
					curTerm = term;
				}
			}
			if (o[31] != null) { // 状态信息
				CachedDeviceStatus st = new CachedDeviceStatus(
						Integer.valueOf(o[31].toString()));
				st.setStatus(Integer.valueOf(o[32].toString()));
				st.setMessage((String) o[33]);
				CachedTermType termType = CacheHelper.termTypeMap.get(curTerm
						.getTermTypeId());
				if (termType == null)
					throw new AppException("term[id=" + curTerm.getId()
							+ "] type[id=" + curTerm.getTermTypeId()
							+ "] not exists!");
				CachedTermTypeDevice cttd = termType.getDeviceMap().get(
						st.getId());
				if (cttd == null)
					logger.warn("term[id=" + curTerm.getId() + "] type[id="
							+ curTerm.getTermTypeId() + "] device[id="
							+ st.getIdString() + "]not exists!");
				else if (cttd.getDevice() == null)
					logger.warn("term[id=" + curTerm.getId() + "] type[id="
							+ curTerm.getTermTypeId() + "] device[id="
							+ st.getIdString() + "]: device is null!");
				else if (cttd.getDevice().getDeviceType() == null)
					logger.warn("term[id=" + curTerm.getId() + "] type[id="
							+ curTerm.getTermTypeId() + "] device[id="
							+ st.getIdString() + "]: device type is null!");
				else {
					AlarmLevel level = cttd.getDevice().getDeviceType()
							.getAlarmLevel(st.getStatus()).getValue();
					st.setLevel(level);
					if (o[35] != null)
						st.setFaultWorkFormId(Long.valueOf(o[35].toString()));
					else
						st.setFaultWorkFormId(null);
					curTerm.getDeviceStatusMap().put(st.getId(), st);
				}
			}
		}
		if (curTerm != null) {
			terms.add(curTerm);
			cachedTerms.add(curCachedTerm);
			processTermStatus(curTerm);
		}
		logger.debug("monitor terms convert end");
		// logger.debug("put terms cache all");
		// CacheHelper.termMap.putCacheAll(cachedTerms);
		// logger.debug("put terms cache all complete");
		return terms;
	}
}
