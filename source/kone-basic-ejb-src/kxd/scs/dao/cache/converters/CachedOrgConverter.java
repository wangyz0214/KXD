package kxd.scs.dao.cache.converters;

import java.sql.Clob;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.beans.sts.CachedBusinessOpenClose;
import kxd.engine.cache.beans.sts.CachedBusinessOpenCloseList;
import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.util.AppException;
import kxd.util.StringUnit;

public class CachedOrgConverter extends BaseDaoConverter<CachedOrg> {

	public CachedOrgConverter() {
	}

	public CachedOrg doConvert(Object result) {
		Object[] d = (Object[]) result;
		CachedOrg o = new CachedOrg();
		o.setId(Integer.valueOf(d[0].toString()));
		o.setAddress((String) d[1]);
		o.setContacter((String) d[2]);
		o.setEmail((String) d[3]);
		if (d[4] == null)
			o.setSerialNumber(0);
		else
			o.setSerialNumber(Integer.valueOf(d[4].toString()));
		o.setOrgCode((String) d[5]);
		o.setOrgFullName((String) d[6]);
		o.setOrgName((String) d[7]);
		o.setTelphone((String) d[8]);
		o.setDepth(Integer.valueOf(d[9].toString()));
		o.setOrgType(Integer.valueOf(d[10].toString()));
		o.setParentPath((String) d[11]);
		o.setExtField0((String) d[12]);
		o.setExtField1((String) d[13]);
		o.setExtField2((String) d[14]);
		o.setExtField3((String) d[15]);
		o.setExtField4((String) d[16]);
		o.getParents().clear();
		if (o.getParentPath() != null)
			o.getParents().addAll(
					StringUnit.splitToInt1(o.getParentPath(), "/"));
		o.setStandardAreaCode((String) d[17]);
		Clob clob = (Clob) d[18];
		try {
			o.getChildren().clear();
			if (clob != null) {
				o.getChildren()
						.addAll(StringUnit.splitToInt1(
								clob.getSubString(1, (int) clob.length()), ","));
			}
		} catch (Exception e) {
			throw new AppException(e);
		}
		// clob = (Clob) d[19];
		// try {
		// o.getTerms().clear();
		// if (clob != null) {
		// o.getTerms()
		// .addAll(StringUnit.splitToInt1(
		// clob.getSubString(1, (int) clob.length()), ","));
		// }
		// } catch (Exception e) {
		// throw new AppException(e);
		// }
		o.setSimpleName((String) d[19]);
		return o;
	}

	@Override
	public List<CachedOrg> convert(List<?> results) {
		List<CachedOrg> ls = new ArrayList<CachedOrg>();
		Iterator<?> it = results.iterator();
		CachedOrg curItem = null;
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			if (curItem == null) {
				curItem = doConvert(o);
			} else {
				CachedOrg term = doConvert(o);
				if (!term.getId().equals(curItem.getId())) {
					ls.add(curItem);
					curItem = term;
				}
			}
			if (o.length > 20 && o[20] != null) {
				CachedBusinessOpenCloseList c = (CachedBusinessOpenCloseList) curItem
						.getMaintConfigMap().get("businessopenclose");
				if (c == null) {
					c = new CachedBusinessOpenCloseList();
					curItem.getMaintConfigMap().put("businessopenclose", c);
				}
				CachedBusinessOpenClose bo;
				if (c.getConfigList().size() == 0) {
					bo = new CachedBusinessOpenClose();
					c.getConfigList().add(bo);
				} else
					bo = c.getConfigList().get(0);
				bo.addItem((String) o[21], (String) o[22], (String) o[26],
						(Date) o[23], (Date) o[24],
						Integer.valueOf(o[25].toString()), (String) o[27],
						(String) o[28], (String) o[29]);
			}
		}
		if (curItem != null) {
			ls.add(curItem);
		}
		// map.putCacheAll(ls);
		return ls;
	}
}
