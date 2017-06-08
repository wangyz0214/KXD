package kxd.scs.dao.fileservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.ShortStringData;
import kxd.engine.cache.beans.sts.CachedFileCategory;
import kxd.engine.cache.beans.sts.CachedFileItem;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseFileItem;
import kxd.remote.scs.util.AppException;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedFileItemConverter;
import kxd.scs.dao.fileservice.converters.BaseFileItemConverter;
import kxd.util.KeyValue;

public class FileListDao extends BaseDao {

	final public static BaseFileItemConverter baseConverter = new BaseFileItemConverter();
	final public static BaseFileItemConverter converter = new BaseFileItemConverter();
	final private static String fieldDefs = "a.fileid,a.categoryid,a.hostid,a.fileownerid,a.savepath,a.originalfilename,a.md5";
	final private static String cacheSql = "select " + fieldDefs
			+ " from filelist a";

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedFileItemHashMapKeys(
			Dao dao, short fileCateoryId) {
		final CachedHashMap<String, CachedFileItem> map = (CachedHashMap<String, CachedFileItem>) CacheHelper
				.getFileItemMap(new CachedFileCategory(fileCateoryId));
		map.setKeysLoading(true);
		try {
			List<CachedFileItem> ls = dao.queryAndSetCache(map,
					new CachedFileItemConverter(map), cacheSql
							+ " where a.categoryid=?1", fileCateoryId);
			List<ShortStringData> idList = new ArrayList<ShortStringData>();
			for (Object o : ls) {
				CachedFileItem r = (CachedFileItem) o;
				idList.add(new ShortStringData(r.getId()));
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
	static public List<?> getCachedFileItemsList(Dao dao, List<?> keys,
			short fileCateoryId) {
		final CachedMap<String, CachedFileItem> map = (CachedMap<String, CachedFileItem>) CacheHelper
				.getFileItemMap(new CachedFileCategory(fileCateoryId));
		StringBuffer sql = new StringBuffer(cacheSql);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.fileid='" + keys.get(i) + "'");
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(map, new CachedFileItemConverter(map),
				sql.toString());
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
	static public CachedFileItem getCachedFileItem(Dao dao, String id,
			short fileCateoryId) {
		final CachedMap<String, CachedFileItem> map = (CachedMap<String, CachedFileItem>) CacheHelper
				.getFileItemMap(new CachedFileCategory(fileCateoryId));
		List<CachedFileItem> ls = dao.queryAndSetCache(map,
				new CachedFileItemConverter(map), cacheSql
						+ " where a.fileid=?1", id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	static public void buildCache(Dao dao, short fileCateoryId) {
		final CachedMap<String, CachedFileItem> map = (CachedMap<String, CachedFileItem>) CacheHelper
				.getFileItemMap(new CachedFileCategory(fileCateoryId));
		dao.queryAndSetCache(map, new CachedFileItemConverter(map), cacheSql
				+ " where a.categoryid=?1", fileCateoryId);
	}

	static public boolean fileIdExists(Dao dao, String fileId) {
		return !dao.query("select fileid from filelist a where fileid=?1",
				fileId).isEmpty();
	}

	static public BaseFileItem find(Dao dao, String fileId) {
		String sql = "select " + fieldDefs + " from filelist a "
				+ "where fileid=?1";
		Iterator<BaseFileItem> it = dao.query(converter, sql, fileId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public void add(Dao dao, long loginUserId, BaseFileItem file) {
		if (fileIdExists(dao, file.getId()))
			throw new AppException("文件ID[" + file.getId() + "]已被占用.");
		dao.insert(file, converter);
		addUserLog(dao, loginUserId, "添加文件[" + file.getId() + "]");
	}

	static public BaseFileItem delete(Dao dao, long loginUserId, String fileId) {
		BaseFileItem u = find(dao, fileId);
		if (u == null)
			return null;
		dao.delete(u, converter);
		CachedMap<String, CachedFileItem> map = CacheHelper
				.getFileItemMap(new CachedFileCategory(u.getFileCategoryId()));
		if (map instanceof CachedHashMap) {
			((CachedHashMap<String, CachedFileItem>) map).deleteCheckSum();
		} else
			map.remove(fileId);
		addUserLog(dao, loginUserId, "删除文件[" + u.getId() + "]");
		return u;
	}

	static public BaseFileItem edit(Dao dao, long loginUserId,
			String oldItemId, BaseFileItem file) {
		BaseFileItem u = find(dao, oldItemId);
		if (u == null)
			throw new AppException("要编辑的文件[" + file.getId() + "]不存在.");
		if (!oldItemId.equals(file.getId()))
			if (fileIdExists(dao, file.getId()))
				throw new AppException("文件ID[" + file.getId() + "]已被占用.");
		dao.execute("update filelist set "
				+ "hostid=?1,fileownerid=?2,savepath=?3,"
				+ "originalfilename=?4,md5=?5,fileid=?6 where fileid=?7",
				file.getFileHostId(), file.getFileOwnerId(),
				file.getSavePath(), file.getOriginalFileName(), file.getMd5(),
				file.getFileItemId(), oldItemId);
		CachedMap<String, CachedFileItem> map = CacheHelper
				.getFileItemMap(new CachedFileCategory(u.getFileCategoryId()));
		if (map instanceof CachedHashMap) {
			((CachedHashMap<String, CachedFileItem>) map).deleteCheckSum();
		} else {
			if (oldItemId != file.getId())
				map.remove(oldItemId);
			map.remove(file.getId());
		}
		addUserLog(dao, loginUserId, "编辑文件[" + oldItemId + "]");
		return u;
	}

	static public void updateVisit(Dao dao, String itemId, int visitTimes) {
		dao.execute("update filelist set visittimes=visittimes+?1,"
				+ "lastvisittime=sysdate where fileid=?2", visitTimes, itemId);
	}
}
