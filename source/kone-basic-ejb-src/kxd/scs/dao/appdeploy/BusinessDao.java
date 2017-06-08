package kxd.scs.dao.appdeploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedBusiness;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.appdeploy.EditedBusiness;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BaseBusinessConverter;
import kxd.scs.dao.appdeploy.converters.EditedBusinessConverter;
import kxd.scs.dao.cache.converters.CachedBusinessConverter;
import kxd.util.KeyValue;

public class BusinessDao extends BaseDao {
	final public static BaseBusinessConverter baseConverter = new BaseBusinessConverter();
	final public static EditedBusinessConverter converter = new EditedBusinessConverter();
	final private static String fieldDefs = "a.businessid,a.businessdesp,a.businesscategoryid,businesscategorydesp";
	final private static String tableName = "business";
	final public static CachedBusinessConverter cachedConverter = new CachedBusinessConverter(
			CacheHelper.businessMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedHashMapKeys(Dao dao) {
		final CachedHashMap<Integer, CachedBusiness> map = CacheHelper.businessMap;
		map.setKeysLoading(true);
		try {
			List<CachedBusiness> ls = dao
					.queryAndSetCache(CacheHelper.businessMap, cachedConverter,
							//注掉为用户软改数据做准备
							//"select businessid,businessdesp,businesscategoryid from business"
							"select businessid,businessdesp,businesscategoryid from business where extdataforstate=0 ");
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedBusiness r = (CachedBusiness) o;
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
	static public List<?> getCachedList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(
				"select businessid,businessdesp,businesscategoryid from business a");
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.businessid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.businessMap, cachedConverter,
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
	static public CachedBusiness getCached(Dao dao, int id) {
		List<CachedBusiness> ls = dao
				.queryAndSetCache(
						CacheHelper.businessMap,
						cachedConverter,
						"select businessid,businessdesp,businesscategoryid from business a where businessyid=?1",
						id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (isDetail) {
			qlString += ",businesscategory b";
			//为businessCategory 表数据过滤
			whereString = "a.businesscategoryid=b.businesscategoryid and a.extdataforstate=0 ";
		//  whereString = "a.businesscategoryid=b.businesscategoryid ";
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "a.businessdesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.businessid";
	}

	static public List<BaseBusiness> getBusinessList(Dao dao,
			Collection<Integer> ls) {
		StringBuffer sb = new StringBuffer();
		for (Integer id : ls) {
			if (sb.length() > 0)
				sb.append(" or ");
			sb.append("businessid=" + id);

		}
		return dao
				.query(baseConverter,
						"select "
								+ fieldDefs
								+ " from business a,businesscategory b "
								+ "where a.businesscategoryid=b.businesscategoryid and ("
								+ sb.toString() + ") order by a.businessid");
	}

	static public List<BaseBusiness> getBusinessList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter,
				"select " + fieldDefs + jionWhereSql(true, keyword));
	}

	static public boolean businessDespExists(Dao dao, String businessCode) {
		return !dao.query("select * from business a where businessdesp=?1",
				businessCode).isEmpty();
	}

	static public EditedBusiness find(Dao dao, int businessId) {
		String sql = "select "
				+ fieldDefs
				+ " from business a,businesscategory b "
				+ "where a.businessid=?1 and a.businesscategoryid=b.businesscategoryid";
		Iterator<EditedBusiness> it = dao.query(converter, sql, businessId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedBusiness business) {
		if (businessDespExists(dao, business.getBusinessDesp()))
			throw new AppException("业务描述[" + business.getBusinessDesp()
					+ "]已被占用.");
		dao.insert(business, converter);
		CacheHelper.businessMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加业务[" + business.getBusinessDesp() + "]");
		return business.getBusinessId();
	}

	static public boolean hasTrade(Dao dao, int businessId) {
		return !dao.queryPage("select * from tradecode where businessid=?1", 0,
				1, businessId).isEmpty();
	}

	static public boolean hasPageElement(Dao dao, int businessId) {
		return !dao.queryPage("select * from pageelement where businessid=?1",
				0, 1, businessId).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] businessId) {
		for (int i = 0; i < businessId.length; i++) {
			EditedBusiness u = find(dao, businessId[i]);
			if (u == null)
				throw new AppException("要删除的业务[" + businessId + "]不存在.");
			if (hasTrade(dao, businessId[i]))
				throw new AppException("业务[" + u.getBusinessDesp()
						+ "]下定义了交易代码，不能删除.");
			if (hasPageElement(dao, businessId[i]))
				throw new AppException("业务[" + u.getBusinessDesp()
						+ "]下定义了页面元素，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除业务[" + u.getBusinessId() + "]");
		}
		CacheHelper.businessMap.removeAll(businessId);
	}

	static public void edit(Dao dao, long loginUserId, EditedBusiness business) {
		BaseBusiness u = find(dao, business.getBusinessId());
		if (u == null)
			throw new AppException("要编辑的业务[" + business.getBusinessId()
					+ "]不存在.");
		if (!business.getBusinessDesp().equals(u.getBusinessDesp())
				&& businessDespExists(dao, business.getBusinessDesp()))
			throw new AppException("业务描述[" + business.getBusinessDesp()
					+ "]已被占用.");
		dao.update(business, converter);
		CacheHelper.businessMap.remove(business.getId());
		addUserLog(dao, loginUserId, "编辑业务[" + business.getBusinessId() + "]");
	}

	static public QueryResult<EditedBusiness> queryBusiness(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}
}
