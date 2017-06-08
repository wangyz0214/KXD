package kxd.scs.dao.appdeploy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortData;
import kxd.engine.cache.beans.sts.CachedPayItem;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BasePayItem;
import kxd.remote.scs.beans.appdeploy.EditedPayItem;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BasePayItemConverter;
import kxd.scs.dao.appdeploy.converters.EditedPayItemConverter;
import kxd.scs.dao.cache.converters.CachedPayItemConverter;
import kxd.util.KeyValue;

public class PayItemDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_PAYITEM_BASIC = "select a.payitem,a.payitemdesp,a.needtrade,"
			+ "a.price,a.memo from payitem a";
	final public static String SQL_QUERY_ALL_CACHED_PAYITEM = SQL_QUERY_CACHED_PAYITEM_BASIC
			+ " order by a.payitem";
	final public static String SQL_QUERY_ALL_PAYITEM_ID = "select a.payitem from"
			+ " payitem a order by a.payitem";
	final public static String SQL_QUERY_CACHED_PAYITEM_BYID = SQL_QUERY_CACHED_PAYITEM_BASIC
			+ " where a.payitem=?1";
	final public static CachedPayItemConverter cachedPayItemConverter = new CachedPayItemConverter(
			CacheHelper.payItemMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedPayItemHashMapKeys(Dao dao) {
		final CachedHashMap<Short, CachedPayItem> map = CacheHelper.payItemMap;
		map.setKeysLoading(true);
		try {
			List<CachedPayItem> ls = dao.queryAndSetCache(
					CacheHelper.payItemMap, cachedPayItemConverter,
					SQL_QUERY_ALL_CACHED_PAYITEM);
			List<ShortData> idList = new ArrayList<ShortData>();
			for (Object o : ls) {
				CachedPayItem r = (CachedPayItem) o;
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
	static public List<?> getCachedPayItemsList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_PAYITEM_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.payitem=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.payItemMap,
				cachedPayItemConverter, sql.toString(), keys);
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
	static public CachedPayItem getCachedPayItem(Dao dao, int id) {
		List<CachedPayItem> ls = dao.queryAndSetCache(CacheHelper.payItemMap,
				cachedPayItemConverter, SQL_QUERY_CACHED_PAYITEM_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BasePayItemConverter baseConverter = new BasePayItemConverter();
	final public static EditedPayItemConverter converter = new EditedPayItemConverter();
	final private static String fieldDefs = "a.payitem,a.payitemdesp,a.needtrade,price,memo";
	final private static String tableName = "payitem";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.payitemdesp like '" + keyword + "%'";
			}
		}

		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.payitem";
	}

	static public List<BasePayItem> getPayItemList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter,
				"select " + fieldDefs + jionWhereSql(false, keyword));
	}

	static public boolean payItemExists(Dao dao, String payItemDesp) {
		return !dao.query("select * from payitem a where payitemdesp=?1",
				payItemDesp).isEmpty();
	}

	static public EditedPayItem find(Dao dao, int payItem) {
		String sql = "select " + fieldDefs + " from payitem a "
				+ "where payitem=?1";
		Iterator<EditedPayItem> it = dao.query(converter, sql, payItem)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public short add(Dao dao, long loginUserId, EditedPayItem payItem) {
		BasePayItem u = find(dao, payItem.getPayItemId());
		if (u != null)
			throw new AppException("收费项目ID重复.");
		if (payItemExists(dao, payItem.getPayItemDesp()))
			throw new AppException("收费项目描述[" + payItem.getPayItemDesp()
					+ "]已被占用.");
		dao.insert(payItem, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.payItemMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加收费项目[" + payItem.getPayItemDesp() + "]");
		return payItem.getId();
	}

	static public boolean hasTradeCode(Dao dao, int payitem) {
		return !dao.queryPage("select * from tradecode where payitem=?1", 0, 1,
				payitem).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Short[] payItem) {
		for (int i = 0; i < payItem.length; i++) {
			EditedPayItem u = find(dao, payItem[i]);
			if (u == null)
				throw new AppException("要删除的收费项目[" + payItem + "]不存在.");
			if (hasTradeCode(dao, payItem[i]))
				throw new AppException("收费项目[" + u.getPayItemDesp()
						+ "]下还配置有交易代码，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除收费项目[" + u.getPayItemDesp() + "]");
		}
		CacheHelper.payItemMap.removeAll(payItem);
	}

	static public void edit(Dao dao, long loginUserId, EditedPayItem payItem) {
		BasePayItem u = find(dao, payItem.getPayItemId());
		if (u == null)
			throw new AppException("要编辑的收费项目[" + payItem.getPayItemId()
					+ "]不存在.");
		if (!payItem.getPayItemDesp().equals(u.getPayItemDesp())
				&& payItemExists(dao, payItem.getPayItemDesp()))
			throw new AppException("收费项目描述[" + payItem.getPayItemDesp()
					+ "]已被占用.");
		dao.update(payItem, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.payItemMap.remove(payItem.getId());
		addUserLog(dao, loginUserId, "编辑收费项目[" + payItem.getPayItemId() + "]");
	}

	static public QueryResult<EditedPayItem> queryPayItem(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}

}
