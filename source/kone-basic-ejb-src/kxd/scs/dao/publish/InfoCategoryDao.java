package kxd.scs.dao.publish;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.publish.converters.BaseInfoCategoryConverter;

public class InfoCategoryDao extends BaseDao {
	final public static BaseInfoCategoryConverter baseConverter = new BaseInfoCategoryConverter();
	final public static BaseInfoCategoryConverter converter = new BaseInfoCategoryConverter();
	final private static String fieldDefs = "infocategoryid,infocategorydesp";
	final private static String tableName = "infocategory";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString = "a.infocategorydesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.infocategoryid";
	}

	static public List<BaseInfoCategory> getInfoCategoryList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select infocategoryid,infocategorydesp"
						+ jionWhereSql(keyword));
	}

	static public boolean infoCategoryDespExists(Dao dao,
			String infoCategoryCode) {
		return !dao.query(
				"select * from infocategory a where infocategorydesp=?1",
				infoCategoryCode).isEmpty();
	}

	static public BaseInfoCategory find(Dao dao, int infoCategoryId) {
		String sql = "select " + fieldDefs + " from infocategory a "
				+ "where infocategoryid=?1";
		Iterator<BaseInfoCategory> it = dao.query(converter, sql,
				infoCategoryId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId,
			BaseInfoCategory infoCategory) {
		if (infoCategoryDespExists(dao, infoCategory.getInfoCategoryDesp()))
			throw new AppException("信息分类描述["
					+ infoCategory.getInfoCategoryDesp() + "]已被占用.");
		dao.insert(infoCategory, converter);
		addUserLog(dao, loginUserId, "添加信息分类["
				+ infoCategory.getInfoCategoryDesp() + "]");
		return infoCategory.getInfoCategoryId();
	}

	static public boolean hasInfo(Dao dao, int infoCategory) {
		return !dao.queryPage("select * from info where infocategoryid=?1", 0,
				1, infoCategory).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, short[] infoCategoryId) {
		for (int i = 0; i < infoCategoryId.length; i++) {
			BaseInfoCategory u = find(dao, infoCategoryId[i]);
			if (u == null)
				throw new AppException("要删除的信息分类[" + infoCategoryId + "]不存在.");
			if (hasInfo(dao, infoCategoryId[i]))
				throw new AppException("信息分类[" + u.getInfoCategoryDesp()
						+ "]下定义了信息，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除信息分类[" + u.getInfoCategoryId()
					+ "]");
		}
	}

	static public void edit(Dao dao, long loginUserId,
			BaseInfoCategory infoCategory) {
		BaseInfoCategory u = find(dao, infoCategory.getInfoCategoryId());
		if (u == null)
			throw new AppException("要编辑的信息分类["
					+ infoCategory.getInfoCategoryId() + "]不存在.");
		if (!infoCategory.getInfoCategoryDesp().equals(u.getInfoCategoryDesp())
				&& infoCategoryDespExists(dao, infoCategory
						.getInfoCategoryDesp()))
			throw new AppException("信息分类描述["
					+ infoCategory.getInfoCategoryDesp() + "]已被占用.");
		dao.update(infoCategory, converter);
		addUserLog(dao, loginUserId, "编辑信息分类["
				+ infoCategory.getInfoCategoryId() + "]");
	}

	static public QueryResult<BaseInfoCategory> queryInfoCategory(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}
}
