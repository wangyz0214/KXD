package kxd.scs.dao.publish.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BasePrintAd;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.util.emun.AuditStatus;

public class BasePrintAdConverter extends BaseDaoConverter<BasePrintAd>{

	public BasePrintAdConverter() {

	}

	
	public BasePrintAd doConvert(Object result) {
		Object[] o = (Object[]) result;
		BasePrintAd r = new BasePrintAd();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setOrg(new BaseOrg(Integer.valueOf(o[1].toString()), (String) o[2]));
		BasePrintAdCategory a = new BasePrintAdCategory(Short.valueOf(o[3]
				.toString()));
		a.setPrintAdCategoryDesp((String) o[4]);
		r.setAdCategory(a);
		r.setAuditStatus(AuditStatus.valueOfIntString(o[5]));
		return r;
	}
}
