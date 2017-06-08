package kxd.scs.dao.right;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.AdminHelper;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.util.AppException;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedOrgConverter;
import kxd.scs.dao.right.converters.BaseOrgConverter;
import kxd.scs.dao.right.converters.EditedOrgConverter;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;

public class OrgDao extends BaseDao {
	private static Logger logger = Logger.getLogger(OrgDao.class);
	final public static String FIELDS_QUERY_CACHED_ORG_BASIC = "select a.orgid,a.address,a.contacter"
			+ ",a.email,a.serialnumber,a.orgcode,a.orgfullname,a.orgname,a.telphone,a.depth,a.orgtype,a.parentpath,"
			+ "a.extfield0,a.extfield1,a.extfield2,a.extfield3,a.extfield4,a.standardareacode,children,simplename";
	final public static String SQL_QUERY_CACHED_ORG_BASIC = FIELDS_QUERY_CACHED_ORG_BASIC
			+ " from org a";
	final public static String SQL_QUERY_ALL_CACHED_ORG = SQL_QUERY_CACHED_ORG_BASIC
			+ " order by a.ident";
	final public static String SQL_QUERY_ALL_ORG_ID = "select a.orgid,a.depth from"
			+ " term a order by a.ident";
	final public static String SQL_QUERY_CACHED_ORG_BYID = SQL_QUERY_CACHED_ORG_BASIC
			+ " where a.orgid=?1";
	final public static String SQL_QUERY_ORG_TERMIDS = "select termid from term where orgid=?1";
	final private static String CACHED_ORG_QUERY = FIELDS_QUERY_CACHED_ORG_BASIC
			+ ",configid,businesscategroyids,businessids,startdate,"
			+ "enddate,openmode,opentimes,reason,payways,payitems from org a , orgbusinessclose b where a.orgid = b.orgid(+)";
	final public static CachedOrgConverter cachedOrgConverter = new CachedOrgConverter();

	/**
	 * 通过机构ID，获取机构数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param id
	 *            机构ID
	 * @return 缓存机构对象
	 */
	static public CachedOrg getCachedOrg(Dao dao, int id) {
		return dao.querySingalAndSetCache(CacheHelper.orgMap, new CachedOrg(id,
				true), 600, cachedOrgConverter, CACHED_ORG_QUERY
				+ " and a.orgid=?1 order by a.orgid,b.configid desc", id);
	}

	/**
	 * 通过机构ID，获取机构数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param id
	 *            机构ID
	 * @return 缓存机构对象
	 */
	static public void buildCache(Dao dao) {
		dao.queryAndSetCache(CacheHelper.orgMap, cachedOrgConverter,
				CACHED_ORG_QUERY + " order by a.orgid,configid desc");
	}

	/**
	 * 从数据库获取指定的机构数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的机构ID列表
	 * @return 缓存机构列表
	 */
	static public List<?> getCachedOrgsList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(CACHED_ORG_QUERY);
		if (keys.size() > 0) {
			sql.append(" and (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.orgid=?" + (i + 1));
			}
			sql.append(")");
		}
		sql.append(" order by a.orgid,configid desc");
		List<CachedOrg> ls = dao
				.query(cachedOrgConverter, sql.toString(), keys);
		try {
			if (ls.size() < 500)
				CacheHelper.orgMap.putAll(ls);
		} catch (Throwable e) {
			logger.error("set cache object list failure["
					+ CachedOrg.KEY_PREFIX + "]", e);
			throw new AppException(e);
		}
		return ls;
	}

	final public static String SQL_ORG_CHILDREN = "select orgid from org a where ";
	final public static String SQL_ORG_CHILDREN1 = "select ident,lastchildorgid from org where orgid=?1";

	/**
	 * 获取机构下所有子机构ID
	 * 
	 * @param dao
	 * @param orgId
	 * @return
	 */
	static public List<Integer> getOrgChildren(Dao dao, int orgId) {
		Iterator<?> it = dao.query(
				SQL_ORG_CHILDREN + getOrgFilterString(dao, orgId, "a"))
				.iterator();
		List<Integer> ls = new ArrayList<Integer>();
		while (it.hasNext()) {
			ls.add(Integer.valueOf(it.next().toString()));
		}
		return ls;
	}

	final public static String SQL_ORG_TERMS = "select termid from term a,org b where "
			+ "b.orgid=a.orgid and ";

	/**
	 * 获取机构下所有终端ID
	 * 
	 * @param dao
	 * @param orgId
	 * @return
	 */
	static public List<Integer> getOrgTerms(Dao dao, int orgId) {
		Iterator<?> it = dao.query(
				SQL_ORG_TERMS + getOrgFilterString(dao, orgId, "b")).iterator();
		List<Integer> ls = new ArrayList<Integer>();
		while (it.hasNext()) {
			ls.add(Integer.valueOf(it.next().toString()));
		}
		return ls;
	}

	/**
	 * 获取机构集合的过滤字串
	 * 
	 * @param orgIds
	 * @return
	 */
	static public String getOrgsFilterString(Dao dao, List<Integer> orgIds,
			String orgAlias) {
		StringBuffer sb = new StringBuffer();
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int i = 0;
		for (Integer orgId : orgIds) {
			if (i > 0)
				sb.append(" or ");
			i++;
			sb.append(" orgid=" + orgId);
		}
		Iterator<?> it = dao.query(
				"select ident,lastchildorgid from org where " + sb.toString())
				.iterator();
		StringBuffer ret = new StringBuffer();
		sb.setLength(0);
		i = 0;
		int j = 0;
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			if (o[1] == null) {
				if (j > 0)
					ret.append(" or ");
				j++;
				ret.append(orgAlias + ".ident=" + o[0]);
			} else {
				map.put(Integer.valueOf(o[1].toString()),
						Integer.valueOf(o[0].toString()));
				if (i > 0)
					sb.append(" or ");
				i++;
				sb.append(" orgid=" + o[1]);
			}
		}
		if (i > 0) {
			it = dao.query("select ident,orgid from org where " + sb.toString())
					.iterator();
			while (it.hasNext()) {
				Object[] o = (Object[]) it.next();
				Integer ident = Integer.valueOf(o[0].toString());
				Integer orgid = Integer.valueOf(o[1].toString());
				Integer fident = map.get(orgid);
				if (j > 0)
					ret.append(" or ");
				j++;
				ret.append("(" + orgAlias + ".ident>=" + fident + " and "
						+ orgAlias + ".ident<=" + ident + ")");
			}
		}
		return "(" + ret.toString() + ")";
	}

	/**
	 * 获取机构的过滤字串
	 * 
	 * @param orgIds
	 * @return
	 */
	static public String getOrgFilterString(Dao dao, Integer orgId,
			String orgAlias) {
		Iterator<?> it = dao.query(SQL_ORG_CHILDREN1, orgId).iterator();
		StringBuffer ret = new StringBuffer();
		if (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			Object lastChildOrgId = o[1];
			if (lastChildOrgId == null) {
				ret.append(orgAlias + ".ident=" + o[0]);
			} else {
				Integer fident = Integer.valueOf(o[0].toString());
				it = dao.query(SQL_ORG_CHILDREN1, lastChildOrgId).iterator();
				if (it.hasNext()) {
					o = (Object[]) it.next();
					Integer ident = Integer.valueOf(o[0].toString());
					ret.append("(" + orgAlias + ".ident>=" + fident + " and "
							+ orgAlias + ".ident<=" + ident + ")");
				}
			}
		}
		return ret.toString();
	}

	final public static BaseOrgConverter baseConverter = new BaseOrgConverter();
	final public static EditedOrgConverter converter = new EditedOrgConverter();
	final private static String fields = "orgid,orgname,orgfullname,depth,ident,"
			+ "parentorgid,lastchildorgid,orgcode,address,telphone,contacter,"
			+ "email,serialnumber,orgtype,parentpath,extfield0,extfield1,extfield2,extfield3,extfield4,standardareacode,children,termlist,simplename ";

	static public List<Integer> getUserManagedOrgIds(Dao dao, long loginUserId) {
		return dao.query(integerConverter,
				"select orgid from userorg where userid=?1", loginUserId);
	}

	static private boolean orgCodeExists(Dao dao, String orgCode) {
		return !dao.query("select orgid from org where orgcode=?1", orgCode)
				.isEmpty();
	}

	static public EditedOrg find(Dao dao, int orgId) {
		Iterator<EditedOrg> it = dao.query(converter,
				"select " + fields + " from org where orgid=?1", orgId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public EditedOrg findByIdent(Dao dao, int ident) {
		Iterator<EditedOrg> it = dao.query(converter,
				"select " + fields + " from org where ident=?1", ident)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedOrg org) {
		if (orgCodeExists(dao, org.getOrgCode()))
			throw new AppException("机构编码[" + org.getOrgCode() + "]已经被占用!");
		LoginUser user = UserDao.getLoginUser(dao, loginUserId);
		if (user.getManuf().getId() != null)
			throw new AppException("厂商用户不能管理机构");
		if (org.getOrgFullName() == null
				|| org.getOrgFullName().trim().length() == 0)
			org.setOrgFullName(org.getOrgName());

		EditedOrg parentOrg = find(dao, org.getParentOrg().getOrgId());

		// 寻找要插入的结点
		EditedOrg insertedOrg;
		boolean needResetParentLastChild = false; // 当前要插入的结点是最后一个子结点，才需要重置
		EditedOrg tmpOrg = null;
		if (parentOrg.isHasChildren()) { // 假如父结点有最后一个子结点,则要插入的结点就是最后一个子结点
			insertedOrg = find(dao, parentOrg.getLastChildOrg().getId());
			needResetParentLastChild = true;
		} else {
			insertedOrg = parentOrg;
			// 检查要插入的结点是否是父结点的最后一个结点
			if (parentOrg.isHasParent()) {
				tmpOrg = find(dao, parentOrg.getParentOrg().getId());
				if (insertedOrg.equals(tmpOrg.getLastChildOrg()))
					needResetParentLastChild = true;
			}
		}
		dao.execute("update org set ident=ident+1 where ident > ?1",
				insertedOrg.getIdent());
		org.setDepth((short) (parentOrg.getDepth() + 1));
		org.setIdent(insertedOrg.getIdent() + 1);
		org.setId(Integer.valueOf(dao.query("select seq_org.nextval from dual")
				.get(0).toString()));
		org.setParentPath((parentOrg.getParentPath() == null ? "" : (parentOrg
				.getParentPath() + "/")) + parentOrg.getIdString());
		StringBuffer sb = new StringBuffer();
		sb.append("orgid=" + parentOrg.getId());
		if (needResetParentLastChild) {
			tmpOrg = parentOrg;
			while (tmpOrg.isHasParent()) { // 将上级结点中子结点相同的结点，全部重新设置最后子结点为新插入的结点
				tmpOrg = find(dao, tmpOrg.getParentOrg().getId());
				if (insertedOrg.equals(tmpOrg.getLastChildOrg())) {
					sb.append(" or orgid=" + tmpOrg.getId());
				} else
					break;
			}
		}
		dao.execute("insert into org(" + fields
				+ ") values(?1,?2,?3,?4,?5,?6,'',?7,?8,"
				+ "?9,?10,?11,?12,?13,?14,?15,?16,?17,?18,?19,?20,'','',?21)",
				org.getId(), org.getOrgName(), org.getOrgFullName(),
				org.getDepth(), org.getIdent(), org.getParentOrg().getId(),
				org.getOrgCode(), org.getAddress(), org.getTelphone(),
				org.getContacter(), org.getEmail(), org.getSerialNumber(),
				org.getOrgType(), org.getParentPath(), org.getExtField0(),
				org.getExtField1(), org.getExtField2(), org.getExtField3(),
				org.getExtField4(), org.getStandardAreaCode(),
				org.getSimpleName());
		dao.execute("update org set lastchildorgid=?1 where " + sb.toString(),
				org.getId());
		String p = parentOrg.getChildren();
		List<Integer> ls = StringUnit.splitToInt1(p, ",");
		if (!ls.contains(org.getId())) {
			ls.add(org.getId());
			p = StringUnit.listToString(ls);
			dao.execute("update org set children=?1 where orgid=?2", p,
					parentOrg.getOrgId());
		}
		try {
			AdminHelper.orgAdded(org);
		} catch (Throwable e) {
		}
		addUserLog(dao, loginUserId, "添加机构[" + org.getOrgCode() + "]");
		return org.getOrgId();
	}

	static private boolean hasTerm(Dao dao, int orgId) {
		return !dao.queryPage("select orgid from term where orgid=?1", 0, 1,
				orgId).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] orgId) {
		LoginUser user = UserDao.getLoginUser(dao, loginUserId);
		if (user.getManuf().getId() != null)
			throw new AppException("厂商用户不能管理机构");
		for (int i = 0; i < orgId.length; i++) {
			EditedOrg org = find(dao, orgId[i]);
			if (org != null) {
				if (org.getDepth() == 0)
					throw new AppException("根机构不能删除");
				if (org.isHasChildren())
					throw new AppException("机构[" + org.getOrgFullName()
							+ "]下还有子机构，不能删除");
				if (hasTerm(dao, org.getId()))
					throw new AppException("机构[" + org.getOrgFullName()
							+ "]下还有终端，不能删除");
				EditedOrg pOrg = find(dao, org.getParentOrg().getId());
				String p = pOrg.getChildren();
				List<Integer> ls = StringUnit.splitToInt1(p, ",");
				if (ls.contains(org.getId())) {
					ls.remove(org.getId());
					p = StringUnit.listToString(ls);
					dao.execute("update org set children=?1 where orgid=?2", p,
							pOrg.getOrgId());
				}
				if (org.equals(pOrg.getLastChildOrg())) {
					int ident = org.getIdent() - 1;
					EditedOrg lastOrg;
					if (ident == pOrg.getIdent()) {// 已经没有子结点
						pOrg.setLastChildOrg(null);
						lastOrg = pOrg;
						dao.execute("update org set lastchildorgid=''"
								+ " where orgid=?1", pOrg.getId());
					} else {
						lastOrg = findByIdent(dao, ident);
						pOrg.setLastChildOrg(lastOrg);
						dao.execute("update org set lastchildorgid=?1"
								+ " where orgid=?2", lastOrg.getId(),
								pOrg.getId());
					}
					StringBuffer sb = new StringBuffer();
					while (pOrg.isHasParent()) {
						pOrg = find(dao, pOrg.getParentOrg().getId());
						if (org.equals(pOrg.getLastChildOrg())) {
							if (sb.length() > 0)
								sb.append(" or ");
							sb.append("orgid=" + pOrg.getId());
						} else
							break;
					}
					if (sb.length() > 0)
						dao.execute("update org set lastchildorgid=?1 where "
								+ sb.toString(), lastOrg.getId());
				}
				dao.execute("delete from org where orgid=?1", org.getId());
				dao.execute("update org set ident=ident-1 where ident > ?1",
						org.getIdent());
				try {
					CacheHelper.orgCodeMap.remove(org.getOrgCode());
					AdminHelper.orgRemoved(org.getOrgId());
				} catch (Throwable e) {
				}
				addUserLog(dao, loginUserId, "删除机构[" + org.getOrgCode() + "]");
			}
		}
		CacheHelper.orgMap.removeAll(orgId);
	}

	static public void edit(Dao dao, long loginUserId, EditedOrg org) {
		if (org.getOrgFullName() == null
				|| org.getOrgFullName().trim().length() == 0)
			org.setOrgFullName(org.getOrgName());
		LoginUser user = UserDao.getLoginUser(dao, loginUserId);
		if (user.getManuf().getId() != null)
			throw new AppException("厂商用户不能管理机构");
		EditedOrg o = find(dao, org.getOrgId());
		if (o == null)
			throw new AppException("要编辑的机构不存在或已被删除");
		if (!o.getOrgCode().equals(org.getOrgCode())) {
			if (orgCodeExists(dao, org.getOrgCode()))
				throw new AppException("机构编码[" + org.getOrgCode() + "]已经被占用!");
			CacheHelper.orgCodeMap.remove(o.getOrgCode());
		}
		dao.execute(
				"update org set orgcode=?1,orgname=?2,orgfullname=?3,contacter=?4,"
						+ "telphone=?5,address=?6,email=?7,serialnumber=?8,"
						+ "orgtype=?9,extfield0=?10,extfield1=?11,"
						+ "extfield2=?12,extfield3=?13,extfield4=?14,standardareacode=?15,simplename=?16 where orgid=?17",
				org.getOrgCode(), org.getOrgName(), org.getOrgFullName(),
				org.getContacter(), org.getTelphone(), org.getAddress(),
				org.getEmail(), org.getSerialNumber(), org.getOrgType(),
				org.getExtField0(), org.getExtField1(), org.getExtField2(),
				org.getExtField3(), org.getExtField4(),
				org.getStandardAreaCode(), org.getSimpleName(), org.getId());
		try {
			AdminHelper.orgModified(org);
		} catch (NamingException e) {
			throw new AppException(e);
		}
		CacheHelper.orgMap.remove(org.getId());
		addUserLog(dao, loginUserId, "编辑机构[" + org.getOrgId() + "]");
	}

	static public void move(Dao dao, long loginUserId, int orgId,
			int newParentOrgId) {
		if (orgId == newParentOrgId)
			return;
		LoginUser user = UserDao.getLoginUser(dao, loginUserId);
		if (user.getManuf().getId() != null)
			throw new AppException("厂商用户不能管理机构");
		EditedOrg org = find(dao, orgId);
		if (org == null) {
			throw new AppException("找不到要迁移的机构[id=" + orgId + "]");
		}
		if (org.getDepth() == 0)
			throw new AppException("根机构不能迁移");
		if (org.isHasChildren())
			throw new AppException("机构[" + org.getOrgFullName()
					+ "]下还有子机构，不能迁移");
		if (org.getParentOrg().getId().equals(newParentOrgId))
			return;
		EditedOrg parentOrg = find(dao, org.getParentOrg().getId());
		String p = parentOrg.getChildren();
		List<Integer> ls = StringUnit.splitToInt1(p, ",");
		if (ls.contains(org.getId())) {
			ls.remove(org.getId());
			p = StringUnit.listToString(ls);
			dao.execute("update org set children=?1 where orgid=?2", p,
					parentOrg.getOrgId());
		}
		if (org.equals(parentOrg.getLastChildOrg())) {
			int ident = org.getIdent() - 1;
			EditedOrg lastOrg;
			if (ident == parentOrg.getIdent()) {// 已经没有子结点
				parentOrg.setLastChildOrg(null);
				lastOrg = parentOrg;
				dao.execute("update org set lastchildorgid=''"
						+ " where orgid=?1", parentOrg.getId());
			} else {
				lastOrg = findByIdent(dao, ident);
				parentOrg.setLastChildOrg(lastOrg);
				dao.execute("update org set lastchildorgid=?1"
						+ " where orgid=?2", lastOrg.getId(), parentOrg.getId());
			}
			StringBuffer sb = new StringBuffer();
			while (parentOrg.isHasParent()) {
				parentOrg = find(dao, parentOrg.getParentOrg().getId());
				if (org.equals(parentOrg.getLastChildOrg())) {
					if (sb.length() > 0)
						sb.append(" or ");
					sb.append("orgid=" + parentOrg.getId());
				} else
					break;
			}
			if (sb.length() > 0)
				dao.execute(
						"update org set lastchildorgid=?1 where "
								+ sb.toString(), lastOrg.getId());
		}
		dao.execute("update org set ident=ident-1 where ident > ?1",
				org.getIdent());
		// 寻找要插入的结点
		EditedOrg insertedOrg;
		boolean needResetParentLastChild = false; // 当前要插入的结点是最后一个子结点，才需要重置
		EditedOrg tmpOrg = null;
		parentOrg = find(dao, newParentOrgId);
		if (parentOrg == null) {
			throw new AppException("找不到新的父机构[id=" + newParentOrgId + "]");
		}
		p = parentOrg.getChildren();
		ls = StringUnit.splitToInt1(p, ",");
		if (!ls.contains(org.getId())) {
			ls.add(org.getId());
			p = StringUnit.listToString(ls);
			dao.execute("update org set children=?1 where orgid=?2", p,
					parentOrg.getOrgId());
		}
		if (parentOrg.isHasChildren()) { // 假如父结点有最后一个子结点,则要插入的结点就是最后一个子结点
			insertedOrg = find(dao, parentOrg.getLastChildOrg().getId());
			needResetParentLastChild = true;
		} else {
			insertedOrg = parentOrg;
			// 检查要插入的结点是否是父结点的最后一个结点
			if (parentOrg.isHasParent()) {
				tmpOrg = find(dao, parentOrg.getParentOrg().getId());
				if (insertedOrg.equals(tmpOrg.getLastChildOrg()))
					needResetParentLastChild = true;
			}
		}
		dao.execute("update org set ident=ident+1 where ident > ?1",
				insertedOrg.getIdent());
		org.setDepth((short) (parentOrg.getDepth() + 1));
		org.setIdent(insertedOrg.getIdent() + 1);
		org.setParentPath((parentOrg.getParentPath() == null ? "" : (parentOrg
				.getParentPath() + "/")) + parentOrg.getIdString());
		StringBuffer sb = new StringBuffer();
		sb.append("orgid=" + parentOrg.getId());
		if (needResetParentLastChild) {
			tmpOrg = parentOrg;
			while (tmpOrg.isHasParent()) { // 将上级结点中子结点相同的结点，全部重新设置最后子结点为新插入的结点
				tmpOrg = find(dao, tmpOrg.getParentOrg().getId());
				if (insertedOrg.equals(tmpOrg.getLastChildOrg())) {
					sb.append(" or orgid=" + tmpOrg.getId());
				} else
					break;
			}
		}
		dao.execute("update org set lastchildorgid=?1 where " + sb.toString(),
				org.getId());
		dao.execute(
				"update org set depth=?1,ident=?2,parentpath=?3,parentorgid=?4 where orgid=?5",
				org.getDepth(), org.getIdent(), org.getParentPath(),
				newParentOrgId, org.getId());
		try {
			AdminHelper.orgMoved(orgId, newParentOrgId);
		} catch (Throwable e) {
			throw new AppException(e);
		}
		CacheHelper.orgMap.remove(org.getId());
		AdminHelper.cleanOrgTermsCache(org.getOrgId());
		addUserLog(dao, loginUserId, "迁移机构[" + org.getOrgId() + "]");
	}
}
