package kxd.scs.dao.system.converters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortData;
import kxd.engine.cache.beans.sts.CachedPrintType;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BasePrintType;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedPrintTypeConverter;
import kxd.util.KeyValue;

public class PrintTypeDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_PRINTTYPE_BASIC = "select a.printtype,a.printtypedesp"
			+ " from printtype a";
	final public static String SQL_QUERY_ALL_CACHED_PRINTTYPE = SQL_QUERY_CACHED_PRINTTYPE_BASIC
			+ " order by a.printtype";
	final public static String SQL_QUERY_ALL_PRINTTYPE_ID = "select a.printtype from"
			+ " printtype a order by a.printtype";
	final public static String SQL_QUERY_CACHED_PRINTTYPE_BYID = SQL_QUERY_CACHED_PRINTTYPE_BASIC
			+ " where a.printtype=?1";
	final public static CachedPrintTypeConverter cachedPrintTypeConverter = new CachedPrintTypeConverter(
			CacheHelper.printTypeMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedPrintTypeHashMapKeys(
			Dao dao) {
		final CachedHashMap<Short, CachedPrintType> map = CacheHelper.printTypeMap;
		map.setKeysLoading(true);
		try {
			List<CachedPrintType> ls = dao.queryAndSetCache(
					CacheHelper.printTypeMap, cachedPrintTypeConverter,
					SQL_QUERY_ALL_CACHED_PRINTTYPE);
			List<ShortData> idList = new ArrayList<ShortData>();
			for (Object o : ls) {
				CachedPrintType r = (CachedPrintType) o;
				idList.add(new ShortData(r.getId()));
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
	static public List<?> getCachedPrintTypesList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_PRINTTYPE_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.printtype=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.printTypeMap,
				cachedPrintTypeConverter, sql.toString(), keys);
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
	static public CachedPrintType getCachedPrintType(Dao dao, short id) {
		List<CachedPrintType> ls = dao.queryAndSetCache(
				CacheHelper.printTypeMap, cachedPrintTypeConverter,
				SQL_QUERY_CACHED_PRINTTYPE_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BasePrintTypeConverter baseConverter = new BasePrintTypeConverter();
	final public static BasePrintTypeConverter converter = new BasePrintTypeConverter();
	final private static String fieldDefs = "a.printtype,a.printtypedesp";
	final private static String tableName = "printtype";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			whereString += "a.printtypedesp like '" + keyword + "%'";
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString;
	}

	static public List<BasePrintType> getPrintTypeList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, "select " + fieldDefs
				+ jionWhereSql(keyword));
	}

	static public BasePrintType find(Dao dao, int printTypeId) {
		String sql = "select " + fieldDefs + " from printtype a"
				+ " where printtype=?1";
		Iterator<BasePrintType> it = dao.query(converter, sql, printTypeId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, BasePrintType printType) {
		if (find(dao, printType.getId()) != null)
			throw new AppException("打印类型编码[" + printType.getPrintTypeDesp()
					+ "]已被占用");
		if (printTypeDespExists(dao, printType.getPrintTypeDesp()))
			throw new AppException("打印类型[" + printType.getPrintTypeDesp()
					+ "]已被占用");
		dao.insert(printType, converter);
		addUserLog(dao, loginUserId, "添加打印类型[" + printType.getPrintTypeDesp()
				+ "]");
		CacheHelper.printTypeMap.deleteCheckSum();
		return printType.getId();
	}

	static public boolean printTypeDespExists(Dao dao, String printTypeDesp) {
		return !dao.query("select * from printtype where printtypedesp=?1",
				printTypeDesp).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Short[] printTypeId) {
		for (Short id : printTypeId) {
			BasePrintType u = find(dao, id);
			if (u == null)
				throw new AppException("要删除的打印类型[" + id + "]不存在.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除打印类型[" + u.getId() + "]");
		}
		CacheHelper.printTypeMap.removeAll(printTypeId);
	}

	static public void edit(Dao dao, long loginUserId, BasePrintType printType) {
		BasePrintType u = find(dao, printType.getId());
		if (u == null)
			throw new AppException("要编辑的打印类型[" + printType.getId() + "]不存在.");
		if (!u.getPrintTypeDesp().equals(printType.getPrintTypeDesp())) {
			if (printTypeDespExists(dao, printType.getPrintTypeDesp()))
				throw new AppException("打印类型[" + printType.getPrintTypeDesp()
						+ "]已被占用");
		}
		dao.update(printType, converter);
		CacheHelper.printTypeMap.remove(printType.getId());
		addUserLog(dao, loginUserId, "编辑打印类型[" + printType.getId() + "]");
	}

	static public QueryResult<BasePrintType> queryPrintType(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}

	static public void addUserPrintTimes(Dao dao, String userno, int month,
			int printType, int times) {
		String tableName = "printlog_" + month;
		if (dao.tableExists(tableName))
			dao.execute("update " + tableName + " set times=times-" + times
					+ " where userno='" + userno + "' and printtype="
					+ printType);
	}

}
