package kxd.scs.dao.system.converters;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.remote.scs.beans.BaseDisabledPrintUser;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;

public class DisablePrintUserDao extends BaseDao {
	final public static BaseDisabledPrintUserConverter baseConverter = new BaseDisabledPrintUserConverter();
	final public static BaseDisabledPrintUserConverter converter = new BaseDisabledPrintUserConverter();
	final private static String fieldDefs = "a.userid,a.userno";
	final private static String tableName = "disableprintuser";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			whereString += "a.userno like '" + keyword + "%'";
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString;
	}

	static public List<BaseDisabledPrintUser> getDisabledPrintUserList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, "select " + fieldDefs
				+ jionWhereSql(keyword));
	}

	static public BaseDisabledPrintUser find(Dao dao, long printTypeId) {
		String sql = "select " + fieldDefs + " from disableprintuser a "
				+ "where userid=?1";
		Iterator<BaseDisabledPrintUser> it = dao.query(converter, sql,
				printTypeId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public long add(Dao dao, long loginUserId, BaseDisabledPrintUser o) {
		if (userNoExists(dao, o.getUserno()))
			throw new AppException("用户[" + o.getUserno() + "]已被占用");
		dao.insert(o, converter);
		addUserLog(dao, loginUserId, "添加用户[" + o.getUserno() + "]");
		return o.getId();
	}

	static public boolean userNoExists(Dao dao, String userNo) {
		return !dao.query("select * from disableprintuser where userno=?1",
				userNo).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, long[] printTypeId) {
		for (long id : printTypeId) {
			BaseDisabledPrintUser u = find(dao, id);
			if (u == null)
				throw new AppException("要删除的用户[" + id + "]不存在.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除用户[" + u.getId() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId, BaseDisabledPrintUser o) {
		BaseDisabledPrintUser u = find(dao, o.getId());
		if (u == null)
			throw new AppException("要编辑的用户[" + o.getId() + "]不存在.");
		if (!u.getUserno().equals(o.getUserno())) {
			if (userNoExists(dao, o.getUserno()))
				throw new AppException("用户[" + o.getUserno() + "]已被占用");
		}
		dao.update(o, converter);
		addUserLog(dao, loginUserId, "编辑用户[" + o.getId() + "]");
	}

	static public QueryResult<BaseDisabledPrintUser> queryDisabledPrintUser(
			Dao dao, boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}
}
