package kxd.scs.dao.cache.converters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceDriverFile;

public class CachedDeviceDriverConverter extends
		CachedDaoConverter<Integer, CachedDeviceDriver> {

	public CachedDeviceDriverConverter(
			CachedMap<Integer, CachedDeviceDriver> map) {
		super(map);
	}

	
	public CachedDeviceDriver doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedDeviceDriver r = new CachedDeviceDriver();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceDriverDesp(o[1].toString());
		r.setDriverFile((String) o[2]);
		return r;
	}

	
	public List<CachedDeviceDriver> convert(List<?> results) {
		List<CachedDeviceDriver> ls = new ArrayList<CachedDeviceDriver>();
		Iterator<?> it = results.iterator();
		CachedDeviceDriver curDeviceDriver = null;
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			if (curDeviceDriver == null) {
				curDeviceDriver = doConvert(o);
			} else {
				CachedDeviceDriver o1 = doConvert(o);
				if (!o1.getId().equals(curDeviceDriver.getId())) {
					ls.add(curDeviceDriver);
					curDeviceDriver = o1;
				}
			}
			if (o[3] != null) { // 文件信息
				BaseDeviceDriverFile st = new BaseDeviceDriverFile(Integer
						.valueOf(o[3].toString()));
				st.setFileName((String) o[4]);
				curDeviceDriver.getFiles().add(st);
			}
		}
		if (curDeviceDriver != null) {
			ls.add(curDeviceDriver);
		}
		//map.putCacheAll(ls);
		return ls;
	}

}
