package kxd.scs.dao.maint;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseOrgBusinessOpenClose;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.maint.converters.BaseOrgBusinessOpenCloseConverter;

public class OrgBusinessOpenCloseDao extends BaseDao {
	final public static BaseOrgBusinessOpenCloseConverter baseConverter = new BaseOrgBusinessOpenCloseConverter();
	final public static BaseOrgBusinessOpenCloseConverter converter = new BaseOrgBusinessOpenCloseConverter();
	final private static String SQL = "select configid,orgid,businesscategroyids,businessids,startdate,enddate,openmode,opentimes,reason,payitems,payways from orgbusinessclose";

	static public List<BaseOrgBusinessOpenClose> getList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, SQL);
	}

	static public BaseOrgBusinessOpenClose find(Dao dao, long id) {
		Iterator<BaseOrgBusinessOpenClose> it = dao.query(converter,
				SQL + " where configid=?1", id).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	} 

	static public long add(Dao dao, long loginUserId, BaseOrgBusinessOpenClose o) {
		dao.insert(o, baseConverter);
		CacheHelper.orgMap.remove(o.getOrgId());
		addUserLog(dao, loginUserId, "添加业务开停配置[" + o.getOrgId() + "]");
		return o.getId();
	}

	static public void delete(Dao dao, long loginUserId, Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			BaseOrgBusinessOpenClose u = find(dao, ids[i]);
			if (u == null)
				throw new AppException("要删除的配置[" + ids + "]不存在.");
			dao.delete(u, converter);
			CacheHelper.orgMap.remove(u.getOrgId());
			addUserLog(dao, loginUserId, "删除业务停开配置[" + u.getId() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId,
			BaseOrgBusinessOpenClose o) {
		BaseOrgBusinessOpenClose u = find(dao, o.getId());
		if (u == null)
			throw new AppException("要编辑的业务开停配置[" + o.getId() + "]不存在.");
		dao.update(o, converter);
		CacheHelper.orgMap.remove(o.getOrgId());
		addUserLog(dao, loginUserId, "编辑业务开停配置[" + o.getId() + "]");
	}

	static public QueryResult<BaseOrgBusinessOpenClose> query(Dao dao,
			boolean queryCount, long loginUserId, Integer orgId,
			Integer filter, String filterContent, int firstResult,
			int maxResults) {
		return query(
				dao,
				converter,
				queryCount,
				"configid,orgid,businesscategroyids,businessids,startdate,enddate,openmode,opentimes,reason,payitems,payways",
				"from orgbusinessclose"
						+ (orgId == null ? "" : " where orgId=" + orgId)
						+ " order by orgid", firstResult, maxResults);
	}
}
