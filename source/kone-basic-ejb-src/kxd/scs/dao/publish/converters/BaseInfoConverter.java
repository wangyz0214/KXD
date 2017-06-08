package kxd.scs.dao.publish.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseInfo;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.util.emun.AuditStatus;
import kxd.util.DataUnit;

public class BaseInfoConverter extends BaseDaoConverter<BaseInfo> {

	public BaseInfoConverter() {

	}

	public BaseInfo doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseInfo r = new BaseInfo();
		r.setId(Long.valueOf(o[0].toString()));
		r.setOrg(new BaseOrg(Integer.valueOf(o[1].toString()), (String) o[2]));
		BaseInfoCategory a = new BaseInfoCategory(
				Short.valueOf(o[3].toString()));
		a.setInfoCategoryDesp((String) o[4]);
		r.setInfoCategory(a);
		r.setTitle((String) o[5]);
		r.setPublishTime(DataUnit.parseDateTimeDef((String) o[6],
				"yyyy-MM-dd HH:mm:ss", null));
		r.setAuditStatus(AuditStatus.valueOfIntString(o[7]));
		return r;
	}
}
