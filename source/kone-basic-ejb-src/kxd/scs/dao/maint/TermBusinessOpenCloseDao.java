package kxd.scs.dao.maint;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseTermBusinessOpenClose;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.maint.converters.BaseTermBusinessOpenCloseConverter;

public class TermBusinessOpenCloseDao extends BaseDao {
	final public static BaseTermBusinessOpenCloseConverter baseConverter = new BaseTermBusinessOpenCloseConverter();
	final public static BaseTermBusinessOpenCloseConverter converter = new BaseTermBusinessOpenCloseConverter();
	final private static String SQL = "select configid,termid,businesscategroyids,businessids,startdate,enddate,openmode,opentimes,reason,payitems,payways from termbusinessclose";

	static public List<BaseTermBusinessOpenClose> getList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, SQL);
	}

	static public BaseTermBusinessOpenClose find(Dao dao, long id) {
		Iterator<BaseTermBusinessOpenClose> it = dao.query(converter,
				SQL + " where configid=?1", id).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public long add(Dao dao, long loginUserId,
			BaseTermBusinessOpenClose o) {
		dao.insert(o, baseConverter);
		CacheHelper.termMap.remove(o.getTermId());
		addUserLog(dao, loginUserId, "添加业务开停配置[" + o.getTermId() + "]");
		return o.getId();
	}

	static public void delete(Dao dao, long loginUserId, Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			BaseTermBusinessOpenClose u = find(dao, ids[i]);
			if (u == null)
				throw new AppException("要删除的配置[" + ids + "]不存在.");
			dao.delete(u, converter);
			CacheHelper.termMap.remove(u.getTermId());
			addUserLog(dao, loginUserId, "删除业务停开配置[" + u.getId() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId,
			BaseTermBusinessOpenClose o) {
		BaseTermBusinessOpenClose u = find(dao, o.getId());
		if (u == null)
			throw new AppException("要编辑的业务开停配置[" + o.getId() + "]不存在.");
		dao.update(o, converter);
		CacheHelper.termMap.remove(o.getTermId());
		addUserLog(dao, loginUserId, "编辑业务开停配置[" + o.getId() + "]");
	}

	static public QueryResult<BaseTermBusinessOpenClose> query(Dao dao,
			boolean queryCount, long loginUserId, Integer termId,
			Integer filter, String filterContent, int firstResult,
			int maxResults) {
		return query(
				dao, 
				converter,
				queryCount,
				"configid,termid,businesscategroyids,businessids,startdate,enddate,openmode,opentimes,reason,payitems,payways",
				"from termbusinessclose"
						+ (termId == null ? "" : " where termid=" + termId)
						+ " order by termid", firstResult, maxResults);
	}
}
