package kxd.scs.dao.appdeploy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedApp;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseAppFile;
import kxd.remote.scs.beans.appdeploy.EditedApp;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BaseAppConverter;
import kxd.scs.dao.appdeploy.converters.BaseAppFileConverter;
import kxd.scs.dao.appdeploy.converters.EditedAppConverter;
import kxd.scs.dao.cache.converters.CachedAppConverter;
import kxd.util.KeyValue;

public class AppDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_ALARMCATEGORY_BASIC = "select a.appid,a.appdesp,"
			+ "a.appcode from app a";
	final public static String SQL_QUERY_ALL_CACHED_ALARMCATEGORY = "select a.appid,a.appdesp,a.appcode,a.appcategoryid from app a order by a.appid";
	final public static String SQL_QUERY_ALL_ALARMCATEGORY_ID = "select a.appid from"
			+ " app a order by a.appid";
	final public static String SQL_QUERY_CACHED_ALARMCATEGORY_BYID = "select a.appid,a.appdesp,a.appcode,a.appcategoryid from app a where a.appid=?1";
	final public static CachedAppConverter cachedAppConverter = new CachedAppConverter(
			CacheHelper.appMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedAppHashMapKeys(Dao dao) {
		final CachedHashMap<Integer, CachedApp> map = CacheHelper.appMap;
		map.setKeysLoading(true);
		try {
			List<CachedApp> ls = dao.queryAndSetCache(CacheHelper.appMap,
					cachedAppConverter, SQL_QUERY_ALL_CACHED_ALARMCATEGORY);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedApp r = (CachedApp) o;
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
	static public List<?> getCachedAppsList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(
				"select a.appid,a.appdesp,a.appcode,a.appcategoryid from app a");
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.appid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.appMap, cachedAppConverter,
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
	static public CachedApp getCachedApp(Dao dao, int id) {
		List<CachedApp> ls = dao.queryAndSetCache(CacheHelper.appMap,
				cachedAppConverter, SQL_QUERY_CACHED_ALARMCATEGORY_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseAppConverter baseConverter = new BaseAppConverter();
	final public static EditedAppConverter converter = new EditedAppConverter();
	final private static String fieldDefs = "a.appid,a.appcode,a.appdesp,a.appcategoryid,appcategorydesp";
	final private static String tableName = "app";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (isDetail) {
			qlString += ",appcategory b";
			whereString = "a.appcategoryid=b.appcategoryid";
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "(a.appdesp like '" + keyword
						+ "%' or a.appcode like '" + keyword + "%')";
			}
		}

		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.appid";
	}

	static public List<BaseApp> getAppList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter,
				"select appid,appdesp" + jionWhereSql(false, keyword));
	}

	static public boolean appCodeExists(Dao dao, String appCode) {
		return !dao.query("select * from app a where appcode=?1", appCode)
				.isEmpty();
	}

	static public EditedApp find(Dao dao, int appId) {
		String sql = "select " + fieldDefs + " from app a,appcategory b "
				+ "where appid=?1 and a.appcategoryid=b.appcategoryid";
		Iterator<EditedApp> it = dao.query(converter, sql, appId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}
	
	static public EditedApp find(Dao dao, String appCode) {
		String sql = "select " + fieldDefs + " from app a,appcategory b "
				+ "where appcode=?1 and a.appcategoryid=b.appcategoryid";
		Iterator<EditedApp> it = dao.query(converter, sql, appCode).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}	

	static public int add(Dao dao, long loginUserId, EditedApp app) {
		if (appCodeExists(dao, app.getAppCode()))
			throw new AppException("应用编码[" + app.getAppCode() + "]已被占用.");
		dao.insert(app, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.appMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加应用[" + app.getAppCode() + "]");
		return app.getAppId();
	}

	static public boolean hasTerm(Dao dao, int app) {
		return !dao.queryPage("select * from term where appid=?1", 0, 1, app)
				.isEmpty();
	}

	static public boolean hasTermType(Dao dao, int app) {
		return !dao.queryPage("select * from termtype where appid=?1", 0, 1,
				app).isEmpty();
	}

	static public boolean hasFile(Dao dao, int app) {
		return !dao.queryPage("select * from appfiles where appid=?1", 0, 1,
				app).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] appId) {
		for (int i = 0; i < appId.length; i++) {
			EditedApp u = find(dao, appId[i]);
			if (u == null)
				throw new AppException("要删除的应用[" + appId + "]不存在.");
			if (hasTerm(dao, appId[i]))
				throw new AppException("还有终端使用了应用[" + u.getAppDesp()
						+ "]，不能删除.");
			if (hasTermType(dao, appId[i]))
				throw new AppException("还有终端型号使用了应用[" + u.getAppDesp()
						+ "]，不能删除.");
			if (hasFile(dao, appId[i]))
				throw new AppException("应用[" + u.getAppDesp()
						+ "]下还保存有文件，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除应用[" + u.getAppId() + "]");
		}
		CacheHelper.appMap.removeAll(appId);
	}

	static public void edit(Dao dao, long loginUserId, EditedApp app) {
		EditedApp u = find(dao, app.getAppId());
		if (u == null)
			throw new AppException("要编辑的应用[" + app.getAppId() + "]不存在.");
		if (!app.getAppCode().equals(u.getAppCode())
				&& appCodeExists(dao, app.getAppCode()))
			throw new AppException("应用编码[" + app.getAppCode() + "]已被占用.");
		dao.update(app, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.appMap.remove(app.getId());
		addUserLog(dao, loginUserId, "编辑应用[" + app.getAppId() + "]");
	}

	static public QueryResult<EditedApp> queryApp(Dao dao, boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}

	final public static BaseAppFileConverter appFileConverter = new BaseAppFileConverter();
	final private static String appFileFieldDefs = "a.appfileid,a.appfilename,a.appid,b.appdesp";

	static private boolean appFileExists(Dao dao, int appId, String filename) {
		return !dao.queryPage(
				"select * from appfiles where appid=?1 and appfilename=?2", 0,
				1, appId, filename).isEmpty();
	}

	static public BaseAppFile findAppFile(Dao dao, int appFileId) {
		String sql = "select " + appFileFieldDefs + " from appfiles a,app b "
				+ "where appfileid=?1 and a.appid=b.appid";
		Iterator<BaseAppFile> it = dao.query(appFileConverter, sql, appFileId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public void addAppFile(Dao dao, long loginUserId, BaseAppFile o) {
		if (appFileExists(dao, o.getApp().getId(), o.getAppFilename()))
			throw new AppException("文件名[" + o.getAppFilename() + "]已经被占用.");
		dao.insert(o, appFileConverter);
		addUserLog(dao, loginUserId, "在应用[" + o.getApp().getAppId() + "]中添加文件["
				+ o.getAppFilename() + "]");
	}

	static public String[] deleteAppFile(Dao dao, long loginUserId, int[] id) {
		String ret[] = new String[id.length];
		for (int i = 0; i < id.length; i++) {
			BaseAppFile u = findAppFile(dao, id[i]);
			if (u != null) {
				dao.delete(u, appFileConverter);
				addUserLog(dao, loginUserId, "删除应用[" + u.getApp().getAppDesp()
						+ "]的文件[" + u.getAppFilename() + "]");
			}
			ret[i] = u.getApp().getAppId() + "/" + u.getAppFilename();
		}
		return ret;
	}

	static public String editAppFile(Dao dao, long loginUserId, BaseAppFile o) {
		BaseAppFile u = findAppFile(dao, o.getAppFileId());
		if (u == null)
			throw new AppException("未找到文件[id=" + o.getIdString() + "].");
		if (!o.getAppFileId().equals(u.getAppFileId())
				&& appFileExists(dao, o.getApp().getAppId(), o.getAppFilename()))
			throw new AppException("文件名[" + o.getAppFilename() + "]已经被占用.");
		dao.update(o, appFileConverter);
		addUserLog(dao, loginUserId, "编辑应用[" + o.getApp().getAppId() + "]中的文件["
				+ o.getAppFileId() + "]的名称为[" + o.getAppFilename() + "]");
		return u.getApp().getIdString() + "/" + u.getAppFilename();
	}

	static public List<BaseAppFile> getAppFileList(Dao dao, long loginUserId,
			int appId) {
		String sql = "select " + appFileFieldDefs + " from appfiles a,app b "
				+ "where a.appid=?1 and a.appid=b.appid";
		return dao
				.query(appFileConverter, sql + " order by a.appfileid", appId);
	}

}
