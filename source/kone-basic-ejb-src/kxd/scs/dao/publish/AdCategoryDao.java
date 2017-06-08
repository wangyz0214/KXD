package kxd.scs.dao.publish;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.publish.converters.BaseAdCategoryConverter;

public class AdCategoryDao extends BaseDao {
	final public static BaseAdCategoryConverter baseConverter = new BaseAdCategoryConverter();
	final public static BaseAdCategoryConverter converter = new BaseAdCategoryConverter();
	final private static String fieldDefs = "adcategoryid,adcategorydesp";
	final private static String tableName = "adcategory";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString = "a.adcategorydesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.adcategoryid";
	}

	static public List<BaseAdCategory> getAdCategoryList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, "select adcategoryid,adcategorydesp"
				+ jionWhereSql(keyword));
	}

	static public boolean adCategoryDespExists(Dao dao, String adCategoryCode) {
		return !dao.query("select * from adcategory a where adcategorydesp=?1",
				adCategoryCode).isEmpty();
	}

	static public BaseAdCategory find(Dao dao, int adCategoryId) {
		String sql = "select " + fieldDefs + " from adcategory a "
				+ "where adcategoryid=?1";
		Iterator<BaseAdCategory> it = dao.query(converter, sql, adCategoryId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, BaseAdCategory adCategory) {
		if (adCategoryDespExists(dao, adCategory.getAdCategoryDesp()))
			throw new AppException("广告分类描述[" + adCategory.getAdCategoryDesp()
					+ "]已被占用.");
		dao.insert(adCategory, converter);
		addUserLog(dao, loginUserId, "添加广告分类[" + adCategory.getAdCategoryDesp()
				+ "]");
		return adCategory.getAdCategoryId();
	}

	static public boolean hasAd(Dao dao, int adCategory) {
		return !dao.queryPage("select * from orgad where adcategoryid=?1", 0,
				1, adCategory).isEmpty()
				|| !dao.queryPage("select * from termad where adcategoryid=?1",
						0, 1, adCategory).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, short[] adCategoryId) {
		for (int i = 0; i < adCategoryId.length; i++) {
			BaseAdCategory u = find(dao, adCategoryId[i]);
			if (u == null)
				throw new AppException("要删除的广告分类[" + adCategoryId + "]不存在.");
			if (hasAd(dao, adCategoryId[i]))
				throw new AppException("广告分类[" + u.getAdCategoryDesp()
						+ "]下定义了广告，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除广告分类[" + u.getAdCategoryId() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId, BaseAdCategory adCategory) {
		BaseAdCategory u = find(dao, adCategory.getAdCategoryId());
		if (u == null)
			throw new AppException("要编辑的广告分类[" + adCategory.getAdCategoryId()
					+ "]不存在.");
		if (!adCategory.getAdCategoryDesp().equals(u.getAdCategoryDesp())
				&& adCategoryDespExists(dao, adCategory.getAdCategoryDesp()))
			throw new AppException("广告分类描述[" + adCategory.getAdCategoryDesp()
					+ "]已被占用.");
		dao.update(adCategory, converter);
		addUserLog(dao, loginUserId, "编辑广告分类[" + adCategory.getAdCategoryId()
				+ "]");
	}

	static public QueryResult<BaseAdCategory> queryAdCategory(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}
}
