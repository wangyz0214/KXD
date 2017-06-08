package kxd.scs.dao.appdeploy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortData;
import kxd.engine.cache.beans.sts.CachedCommInterface;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseCommInterface;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BaseCommInterfaceConverter;
import kxd.scs.dao.cache.converters.CachedCommInterfaceConverter;
import kxd.util.KeyValue;

public class CommInterfaceDao extends BaseDao {
	final public static BaseCommInterfaceConverter baseConverter = new BaseCommInterfaceConverter();
	final public static BaseCommInterfaceConverter converter = new BaseCommInterfaceConverter();
	final public static CachedCommInterfaceConverter cachedConverter = new CachedCommInterfaceConverter(
			CacheHelper.commInterfaceMap);
	final private static String fieldDefs = "interfaceid,interfacedesp,interfacetype";
	final private static String tableName = "comm_interface";

	/**
	 * 从数据库获取全部的接口数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存接口的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedHashMapKeys(Dao dao) {
		final CachedHashMap<Short, CachedCommInterface> map = CacheHelper.commInterfaceMap;
		map.setKeysLoading(true);
		try {
			List<CachedCommInterface> ls = dao
					.queryAndSetCache(CacheHelper.commInterfaceMap,
							cachedConverter,
							"select interfaceid,interfacedesp,interfacetype from comm_interface");
			List<ShortData> idList = new ArrayList<ShortData>();
			for (Object o : ls) {
				CachedCommInterface r = (CachedCommInterface) o;
				idList.add(new ShortData(r.getId()));
			}
			return new KeyValue<byte[], List<?>>(map.setKeys(idList), idList);
		} finally {
			map.setKeysLoading(false);
		}
	}

	/**
	 * 从数据库获取指定的接口数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的接口ID列表
	 * @return 缓存接口列表
	 */
	static public List<?> getCachedList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(
				"select interfaceid,interfacedesp,interfacetype from comm_interface a");
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.interfaceid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.commInterfaceMap,
				cachedConverter, sql.toString(), keys);
	}

	/**
	 * 通过接口ID，获取接口数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param id
	 *            接口ID
	 * @return 缓存接口对象
	 */
	static public CachedCommInterface getCached(Dao dao, short id) {
		List<CachedCommInterface> ls = dao
				.queryAndSetCache(
						CacheHelper.commInterfaceMap,
						cachedConverter,
						"select interfaceid,interfacedesp,interfacetype from comm_interface a where interfaceid=?1",
						id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString = "a.interfacedesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by interfaceid";
	}

	static public List<BaseCommInterface> getCommInterfaceList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select interfaceid,interfacedesp,interfacetype"
						+ jionWhereSql(keyword));
	}

	static public BaseCommInterface find(Dao dao, short commInterfaceId) {
		String sql = "select " + fieldDefs + " from comm_interface a "
				+ "where interfaceid=?1";
		Iterator<BaseCommInterface> it = dao.query(converter, sql,
				commInterfaceId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public short add(Dao dao, long loginUserId,
			BaseCommInterface commInterface) {
		dao.insert(commInterface, converter);
		CacheHelper.commInterfaceMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加接口[" + commInterface.getDesp() + "]");
		return commInterface.getId();
	}

	static public boolean hasApp(Dao dao, int commInterface) {
		return !dao.queryPage(
				"select * from comm_interface where interfaceid=?1", 0, 1,
				commInterface).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Short[] commInterfaceId) {
		for (int i = 0; i < commInterfaceId.length; i++) {
			BaseCommInterface u = find(dao, commInterfaceId[i]);
			if (u == null)
				throw new AppException("要删除的接口[" + commInterfaceId + "]不存在.");
			if (hasApp(dao, commInterfaceId[i]))
				throw new AppException("接口[" + u.getDesp() + "]下定义了接口，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除接口[" + u.getId() + "]");
		}
		CacheHelper.commInterfaceMap.removeAll(commInterfaceId);
	}

	static public void edit(Dao dao, long loginUserId,
			BaseCommInterface commInterface) {
		BaseCommInterface u = find(dao, commInterface.getId());
		if (u == null)
			throw new AppException("要编辑的接口[" + commInterface.getId() + "]不存在.");
		dao.update(commInterface, converter);
		CacheHelper.commInterfaceMap.remove(commInterface.getId());
		addUserLog(dao, loginUserId, "编辑接口[" + commInterface.getId() + "]");
	}

	static public QueryResult<BaseCommInterface> queryCommInterface(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}
}
