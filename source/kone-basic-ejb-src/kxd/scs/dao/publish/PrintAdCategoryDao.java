package kxd.scs.dao.publish;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.publish.converters.BasePrintAdCategoryConverter;

public class PrintAdCategoryDao extends BaseDao {
	final public static BasePrintAdCategoryConverter baseConverter = new BasePrintAdCategoryConverter();
	final public static BasePrintAdCategoryConverter converter = new BasePrintAdCategoryConverter();
	final private static String fieldDefs = "printadcategoryid,printadcategorydesp";
	final private static String tableName = "printadcategory";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString = "a.printadcategorydesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.printadcategoryid";
	}

	static public List<BasePrintAdCategory> getPrintAdCategoryList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select printadcategoryid,printadcategorydesp"
						+ jionWhereSql(keyword));
	}

	static public boolean printadCategoryDespExists(Dao dao,
			String printAdCategoryCode) {
		return !dao.query(
				"select * from printadcategory a where printadcategorydesp=?1",
				printAdCategoryCode).isEmpty();
	}

	static public BasePrintAdCategory find(Dao dao, int printAdCategoryId) {
		String sql = "select " + fieldDefs + " from printadcategory a "
				+ "where printadcategoryid=?1";
		Iterator<BasePrintAdCategory> it = dao.query(converter, sql,
				printAdCategoryId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId,
			BasePrintAdCategory printAdCategory) {
		if (printadCategoryDespExists(dao, printAdCategory
				.getPrintAdCategoryDesp()))
			throw new AppException("打印广告分类描述["
					+ printAdCategory.getPrintAdCategoryDesp() + "]已被占用.");
		dao.insert(printAdCategory, converter);
		addUserLog(dao, loginUserId, "添加打印广告分类["
				+ printAdCategory.getPrintAdCategoryDesp() + "]");
		return printAdCategory.getPrintAdCategoryId();
	}

	static public boolean hasPrintAd(Dao dao, int printAdCategory) {
		return !dao.queryPage(
				"select * from orgprintad where printadcategoryid=?1", 0, 1,
				printAdCategory).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId,
			short[] printAdCategoryId) {
		for (int i = 0; i < printAdCategoryId.length; i++) {
			BasePrintAdCategory u = find(dao, printAdCategoryId[i]);
			if (u == null)
				throw new AppException("要删除的打印广告分类[" + printAdCategoryId
						+ "]不存在.");
			if (hasPrintAd(dao, printAdCategoryId[i]))
				throw new AppException("打印广告分类[" + u.getPrintAdCategoryDesp()
						+ "]下定义了打印广告，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除打印广告分类[" + u.getPrintAdCategoryId()
					+ "]");
		}
	}

	static public void edit(Dao dao, long loginUserId,
			BasePrintAdCategory printAdCategory) {
		BasePrintAdCategory u = find(dao, printAdCategory
				.getPrintAdCategoryId());
		if (u == null)
			throw new AppException("要编辑的打印广告分类["
					+ printAdCategory.getPrintAdCategoryId() + "]不存在.");
		if (!printAdCategory.getPrintAdCategoryDesp().equals(
				u.getPrintAdCategoryDesp())
				&& printadCategoryDespExists(dao, printAdCategory
						.getPrintAdCategoryDesp()))
			throw new AppException("打印广告分类描述["
					+ printAdCategory.getPrintAdCategoryDesp() + "]已被占用.");
		dao.update(printAdCategory, converter);
		addUserLog(dao, loginUserId, "编辑打印广告分类["
				+ printAdCategory.getPrintAdCategoryId() + "]");
	}

	static public QueryResult<BasePrintAdCategory> queryPrintAdCategory(
			Dao dao, boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}
}
