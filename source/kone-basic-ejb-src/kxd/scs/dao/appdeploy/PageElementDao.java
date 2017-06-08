package kxd.scs.dao.appdeploy;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.remote.scs.beans.BasePageElement;
import kxd.remote.scs.beans.appdeploy.EditedPageElement;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BasePageElementConverter;
import kxd.scs.dao.appdeploy.converters.EditedPageElementConverter;

public class PageElementDao extends BaseDao {
	final public static BasePageElementConverter baseConverter = new BasePageElementConverter();
	final public static EditedPageElementConverter converter = new EditedPageElementConverter();
	final private static String fieldDefs = "a.pageid,a.pagecode,a.pagedesp,a.businessid,businessdesp";
	final private static String tableName = "pageelement";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (isDetail) {
			qlString += ",business b";
			whereString = "a.businessid=b.businessid";
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "a.pagedesp like '" + keyword
						+ "%' or a.pagecode like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.pageid";
	}

	static public List<BasePageElement> getPageElementList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, "select pageid,pagedesp"
				+ jionWhereSql(false, keyword));
	}

	static public boolean pageCodeExists(Dao dao, String pageCode) {
		return !dao.query("select * from pageelement a where pagecode=?1",
				pageCode).isEmpty();
	}

	static public EditedPageElement find(Dao dao, int pageId) {
		String sql = "select " + fieldDefs + " from pageelement a,business b "
				+ "where a.pageid=?1 and a.businessid=b.businessid";
		Iterator<EditedPageElement> it = dao.query(converter, sql, pageId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedPageElement page) {
		if (pageCodeExists(dao, page.getPageCode()))
			throw new AppException("页面元素编码[" + page.getPageCode() + "]已被占用.");
		dao.insert(page, converter);
		addUserLog(dao, loginUserId, "添加页面元素[" + page.getPageDesp() + "]");
		return page.getPageId();
	}

	static public void delete(Dao dao, long loginUserId, int[] pageId) {
		for (int i = 0; i < pageId.length; i++) {
			EditedPageElement u = find(dao, pageId[i]);
			if (u == null)
				throw new AppException("要删除的页面元素[" + pageId + "]不存在.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除页面元素[" + u.getPageId() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId, EditedPageElement page) {
		EditedPageElement u = find(dao, page.getPageId());
		if (u == null)
			throw new AppException("要编辑的页面元素[" + page.getPageId() + "]不存在.");
		if (!page.getPageCode().equals(u.getPageCode())
				&& pageCodeExists(dao, page.getPageCode()))
			throw new AppException("页面元素编码[" + page.getPageCode() + "]已被占用.");
		dao.update(page, converter);
		addUserLog(dao, loginUserId, "编辑页面元素[" + page.getPageId() + "]");
	}

	static public QueryResult<EditedPageElement> queryPageElement(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs, jionWhereSql(true,
				filterContent), firstResult, maxResults);
	}
}
