package kxd.scs.dao.right;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.right.EditedManuf;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedManufConverter;
import kxd.scs.dao.right.converters.BaseManufConverter;
import kxd.scs.dao.right.converters.EditedManufConverter;
import kxd.util.KeyValue;

public class ManufDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_MANUF_BASIC = "select a.manufid,a.manufcode"
			+ ",a.manufname,a.serialnumber,a.manuftype from manuf a";
	final public static String SQL_QUERY_ALL_CACHED_MANUF = SQL_QUERY_CACHED_MANUF_BASIC
			+ " order by a.manufid";
	final public static String SQL_QUERY_ALL_MANUF_ID = "select a.manufid from"
			+ " manuf a order by a.manufid";
	final public static String SQL_QUERY_CACHED_MANUF_BYID = SQL_QUERY_CACHED_MANUF_BASIC
			+ " where a.manufid=?1";
	final public static CachedManufConverter cachedManufConverter = new CachedManufConverter(
			CacheHelper.manufMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedManufHashMapKeys(Dao dao) {
		final CachedHashMap<Integer, CachedManuf> map = CacheHelper.manufMap;
		map.setKeysLoading(true);
		try {
			List<CachedManuf> ls = dao.queryAndSetCache(CacheHelper.manufMap,
					cachedManufConverter, SQL_QUERY_ALL_CACHED_MANUF);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedManuf r = (CachedManuf) o;
				idList.add(new IntegerData(r.getId()));
			}
			return new KeyValue<byte[], List<?>>(map.setKeys(idList), idList);
		} finally {
			map.setKeysLoading(false);
		}
	}

	/**
	 * 从数据库获取指定的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的应用ID列表
	 * @return 缓存应用列表
	 */
	static public List<?> getCachedManufsList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_MANUF_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.manufid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.manufMap, cachedManufConverter,
				sql.toString(), keys);
	}

	/**
	 * 通过应用ID，获取应用数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param id
	 *            应用ID
	 * @return 缓存应用对象
	 */
	static public CachedManuf getCachedManuf(Dao dao, int id) {
		List<CachedManuf> ls = dao.queryAndSetCache(CacheHelper.manufMap,
				cachedManufConverter, SQL_QUERY_CACHED_MANUF_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseManufConverter baseConverter = new BaseManufConverter();
	final public static EditedManufConverter converter = new EditedManufConverter();
	final private static String fields = "a.manufid,a.manufname,a.manufcode,a.serialnumber,a.manuftype";
	final private static String tableName = "manuf";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.manufname like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.manufid";
	}

	static public List<BaseManuf> getManufList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter, "select a.manufid,a.manufname"
				+ jionWhereSql(keyword));
	}

	static public boolean manufCodeExists(Dao dao, String code) {
		return !dao.query("select * from manuf a where manufcode=?1", code)
				.isEmpty();
	}

	static public boolean hasUser(Dao dao, int id) {
		return !dao.queryPage("select * from systemuser a where manufid=?1", 0,
				1, id).isEmpty();
	}

	static public boolean hasTermType(Dao dao, int id) {
		return !dao.queryPage("select * from termtype a where manufid=?1", 0,
				1, id).isEmpty();
	}

	static public EditedManuf find(Dao dao, int id) {
		String sql = "select " + fields + " from manuf a "
				+ "where a.manufid=?1";
		Iterator<EditedManuf> it = dao.query(converter, sql, id).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedManuf o) {
		if (manufCodeExists(dao, o.getManufCode()))
			throw new AppException("厂商编码[" + o.getManufCode() + "]已被占用.");
		dao.insert(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.manufMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加厂商[" + o.getManufName() + "]");
		return o.getId();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] manufId) {
		for (int i = 0; i < manufId.length; i++) {
			EditedManuf u = find(dao, manufId[i]);
			if (u == null)
				throw new AppException("要删除的厂商[" + manufId[i] + "]不存在.");
			if (hasUser(dao, u.getId()))
				throw new AppException("厂商[" + u.getManufName()
						+ "]下定义了用户，不能删除.");
			if (hasTermType(dao, u.getId()))
				throw new AppException("厂商[" + u.getManufName()
						+ "]下配置了终端型号，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除厂商[" + u.getId() + "]");
		}
		CacheHelper.manufMap.removeAll(manufId);
	}

	static public void edit(Dao dao, long loginUserId, EditedManuf o) {
		EditedManuf u = find(dao, o.getId());
		if (u == null)
			throw new AppException("要编辑的厂商[" + o.getId() + "]不存在.");
		if (!o.getManufCode().equals(u.getManufCode())
				&& manufCodeExists(dao, o.getManufCode()))
			throw new AppException("厂商编码[" + o.getManufCode() + "]已被占用.");
		o.setManufType(u.getManufType());
		dao.update(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.manufMap.remove(o.getId());
		addUserLog(dao, loginUserId, "编辑厂商[" + o.getId() + "]");
	}

	static public QueryResult<EditedManuf> queryManuf(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fields,
				jionWhereSql(filterContent), firstResult, maxResults);
	}

}
