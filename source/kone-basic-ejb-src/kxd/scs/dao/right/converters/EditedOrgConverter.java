package kxd.scs.dao.right.converters;

import java.sql.Clob;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.util.AppException;

public class EditedOrgConverter extends BaseDaoConverter<EditedOrg> {

	public EditedOrgConverter() {
	}

	public EditedOrg doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedOrg r = new EditedOrg();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setOrgName(o[1].toString());
		r.setOrgFullName((String) o[2]);
		r.setDepth(Integer.valueOf(o[3].toString()));
		r.setIdent(Integer.valueOf(o[4].toString()));
		if (o[5] == null)
			r.setParentOrg(new BaseOrg());
		else
			r.setParentOrg(new BaseOrg(Integer.valueOf(o[5].toString())));
		if (o[6] == null)
			r.setLastChildOrg(new BaseOrg());
		else
			r.setLastChildOrg(new BaseOrg(Integer.valueOf(o[6].toString())));
		r.setOrgCode((String) o[7]);
		r.setAddress((String) o[8]);
		r.setTelphone((String) o[9]);
		r.setContacter((String) o[10]);
		r.setEmail((String) o[11]);
		r.setSerialNumber(Integer.valueOf(o[12].toString()));
		r.setOrgType(Integer.valueOf(o[13].toString()));
		r.setParentPath((String) o[14]);
		r.setExtField0((String) o[15]);
		r.setExtField1((String) o[16]);
		r.setExtField2((String) o[17]);
		r.setExtField3((String) o[18]);
		r.setExtField4((String) o[19]);
		r.setStandardAreaCode((String) o[20]);
		Clob clob = (Clob) o[21];
		try {
			if (clob == null)
				r.setChildren(null);
			else
				r.setChildren(clob.getSubString(1, (int) clob.length()));
		} catch (Exception e) {
			throw new AppException(e);
		}
		clob = (Clob) o[22];
		try {
			if (clob == null)
				r.setTerms(null);
			else
				r.setTerms(clob.getSubString(1, (int) clob.length()));
		} catch (Exception e) {
			throw new AppException(e);
		}
		r.setSimpleName((String)o[23]);
		return r;
	}
}
