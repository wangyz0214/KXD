package kxd.scs.dao.cache.converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedBusinessOpenClose;
import kxd.engine.cache.beans.sts.CachedBusinessOpenCloseList;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermConfig;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.DateTime;
import kxd.util.StringUnit;

public class CachedTermConverter extends
		CachedDaoConverter<Integer, CachedTermConfig> {

	public CachedTermConverter(CachedMap<Integer, CachedTermConfig> map) {
		super(map);
	}

	public CachedTermConfig doConvert(Object result) {
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
		term.getParentOrgIds().add(term.getOrgId());
		String p = (String) o[31];
		if (p != null)
			term.getParentOrgIds().addAll(StringUnit.splitToInt1(p, "/"));
		
		String termip =(String) o[42];
		if(termip != null)
			term.setIp(termip);//add by jurstone 20120611
		CachedTermConfig config = new CachedTermConfig(term.getId());
		config.setTerm(term);
		config.getMaintConfig().put("businessopenclose",
				new CachedBusinessOpenCloseList());
		return config;
	}

	public CachedTermConfig convert(Object result) {
		CachedTermConfig o = super.convert(result);
		return o;
	}

	@Override
	public List<CachedTermConfig> convert(List<?> results) {
		List<CachedTermConfig> ls = new ArrayList<CachedTermConfig>();
		Iterator<?> it = results.iterator();
		CachedTermConfig curItem = null;
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			if (curItem == null) {
				curItem = doConvert(o);
			} else {
				CachedTermConfig term = doConvert(o);
				if (!term.getId().equals(curItem.getId())) {
					ls.add(curItem);
					curItem = term;
				}
			}
			if (o[32] != null) { //
				CachedBusinessOpenCloseList c = (CachedBusinessOpenCloseList) curItem
						.getMaintConfig().get("businessopenclose");
				CachedBusinessOpenClose bo;
				if (c.getConfigList().size() == 0) {
					bo = new CachedBusinessOpenClose();
					c.getConfigList().add(bo);
				} else
					bo = c.getConfigList().get(0);
				bo.addItem((String) o[33], (String) o[34], (String) o[38],
						(Date) o[35], (Date) o[36],
						Integer.valueOf(o[37].toString()), (String) o[39],
						(String) o[40], (String) o[41]);
			}
		}
		if (curItem != null) {
			ls.add(curItem);
		}
		// map.putCacheAll(ls);
		return ls;
	}

}
