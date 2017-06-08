package kxd.scs.dao.appdeploy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedBusinessCategory;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BaseBusinessCategoryConverter;
import kxd.scs.dao.cache.converters.CachedBusinessCategoryConverter;
import kxd.util.KeyValue;

public class BusinessCategoryDao extends BaseDao {
	final public static BaseBusinessCategoryConverter baseConverter = new BaseBusinessCategoryConverter();
	final public static BaseBusinessCategoryConverter converter = new BaseBusinessCategoryConverter();
	final private static String fieldDefs = "businesscategoryid,businesscategorydesp";
	final private static String tableName = "businesscategory";
	final public static CachedBusinessCategoryConverter cachedConverter = new CachedBusinessCategoryConverter(
			CacheHelper.businessCategoryMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedHashMapKeys(Dao dao) {
		final CachedHashMap<Integer, CachedBusinessCategory> map = CacheHelper.businessCategoryMap;
		map.setKeysLoading(true);
		try {
			List<CachedBusinessCategory> ls = dao
					.queryAndSetCache(CacheHelper.businessCategoryMap,
							cachedConverter,
							//注掉为后台软修改数据做准备
							//"select businesscategoryid,businesscategorydesp from businesscategory"
							"select businesscategoryid,businesscategorydesp from businesscategory where extdataforstate=0 ");
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedBusinessCategory r = (CachedBusinessCategory) o;
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
				"select businesscategoryid,businesscategorydesp from businesscategory a ");
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.businesscategoryid=?" + (i + 1));
			}
			sql.append(")  a.extdataforstate=0 ");
		}else {
			sql.append("where  a.extdataforstate=0 ");
		}
		return dao.queryAndSetCache(CacheHelper.businessCategoryMap,
				cachedConverter, sql.toString(), keys);
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
	static public CachedBusinessCategory getCached(Dao dao, int id) {
		List<CachedBusinessCategory> ls = dao
				.queryAndSetCache(
						CacheHelper.businessCategoryMap,
						cachedConverter,
						"select businesscategoryid,businesscategorydesp from businesscategory a where businesscategoryid=?1",
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
				whereString = "a.businesscategorydesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0){
			whereString = " where " + whereString;}
		else{//为businessCategory 表数据过滤
			whereString=" where extdataforstate=0 ";
		} ;
		return qlString + whereString + " order by a.businesscategoryid";
	}

	static public List<BaseBusinessCategory> getBusinessCategoryList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select businesscategoryid,businesscategorydesp"
						+ jionWhereSql(keyword));
	}

	static public boolean businessCategoryDespExists(Dao dao,
			String businessCategoryCode) {
		return !dao
				.query("select * from businesscategory a where businesscategorydesp=?1",
						businessCategoryCode).isEmpty();
	}

	static public BaseBusinessCategory find(Dao dao, int businessCategoryId) {
		String sql = "select " + fieldDefs + " from businesscategory a "
				+ "where businesscategoryid=?1";
		Iterator<BaseBusinessCategory> it = dao.query(converter, sql,
				businessCategoryId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId,
			BaseBusinessCategory businessCategory) {
		if (businessCategoryDespExists(dao,
				businessCategory.getBusinessCategoryDesp()))
			throw new AppException("业务分类描述["
					+ businessCategory.getBusinessCategoryDesp() + "]已被占用.");
		dao.insert(businessCategory, converter);
		CacheHelper.businessCategoryMap.deleteCheckSum();
		addUserLog(dao, loginUserId,
				"添加业务分类[" + businessCategory.getBusinessCategoryDesp() + "]");
		return businessCategory.getBusinessCategoryId();
	}

	static public boolean hasBusiness(Dao dao, int businessCategory) {
		return !dao.queryPage(
				"select * from business where businesscategoryid=?1", 0, 1,
				businessCategory).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId,
			Integer[] businessCategoryId) {
		for (int i = 0; i < businessCategoryId.length; i++) {
			BaseBusinessCategory u = find(dao, businessCategoryId[i]);
			if (u == null)
				throw new AppException("要删除的业务分类[" + businessCategoryId
						+ "]不存在.");
			if (hasBusiness(dao, businessCategoryId[i]))
				throw new AppException("业务分类[" + u.getBusinessCategoryDesp()
						+ "]下定义了业务，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除业务分类[" + u.getBusinessCategoryId()
					+ "]");
		}
		CacheHelper.businessCategoryMap.removeAll(businessCategoryId);
	}

	static public void edit(Dao dao, long loginUserId,
			BaseBusinessCategory businessCategory) {
		BaseBusinessCategory u = find(dao,
				businessCategory.getBusinessCategoryId());
		if (u == null)
			throw new AppException("要编辑的业务分类["
					+ businessCategory.getBusinessCategoryId() + "]不存在.");
		if (!businessCategory.getBusinessCategoryDesp().equals(
				u.getBusinessCategoryDesp())
				&& businessCategoryDespExists(dao,
						businessCategory.getBusinessCategoryDesp()))
			throw new AppException("业务分类描述["
					+ businessCategory.getBusinessCategoryDesp() + "]已被占用.");
		dao.update(businessCategory, converter);
		CacheHelper.businessCategoryMap.remove(businessCategory.getId());
		addUserLog(dao, loginUserId,
				"编辑业务分类[" + businessCategory.getBusinessCategoryId() + "]");
	}

	static public QueryResult<BaseBusinessCategory> queryBusinessCategory(
			Dao dao, boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}
}
