package kxd.scs.dao.right;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.BaseUser;
import kxd.remote.scs.beans.right.EditedFunction;
import kxd.remote.scs.beans.right.EditedUser;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedUser;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.UserGroup;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.right.converters.BaseUserConverter;
import kxd.scs.dao.right.converters.EditedFunctionConverter;
import kxd.scs.dao.right.converters.EditedUserConverter;
import kxd.scs.dao.right.converters.LoginUserConverter;
import kxd.scs.dao.right.converters.QueryedUserConverter;
import kxd.util.DateTime;

public class UserDao extends BaseDao {
	final public static LoginUserConverter loginConverter = new LoginUserConverter();
	final public static BaseUserConverter baseConverter = new BaseUserConverter();
	final public static EditedUserConverter converter = new EditedUserConverter();
	final public static QueryedUserConverter queryConverter = new QueryedUserConverter();

	static public LoginUser getLoginUser(Dao dao, long loginUserId) {
		List<LoginUser> ls = dao.query(loginConverter,
				"select a.userid,a.username,a.usergroup,a.usercode,a.rightscope,a.orgid,"
						+ "orgfullname,a.manufid,manufname,a.userpwd,a.roleid"
						+ " from systemuser a,org b,manuf c where a.userid=?1 "
						+ "and a.orgid=b.orgid and a.manufid=c.manufid(+)",
				loginUserId);
		if (ls.isEmpty())
			return null;
		else
			return ls.get(0);
	}

	static public LoginUser login(Dao dao, String userCode, String userPwd) {
		List<LoginUser> ls = dao
				.query(loginConverter,
						"select a.userid,a.username,a.usergroup,a.usercode,a.rightscope,a.orgid,"
								+ "orgfullname,a.manufid,manufname,a.userpwd,a.roleid"
								+ " from systemuser a,org b,manuf c where a.usercode=?1 "
								+ "and a.orgid=b.orgid and a.manufid=c.manufid(+)",
						userCode);
		Iterator<LoginUser> it = ls.iterator();
		if (it.hasNext()) {
			LoginUser user = (LoginUser) it.next();
			if (userPwd != null && !user.getUserPwd().equals(userPwd)) {
				throw new AppException("您的密码不正确!");
			}
			dao.execute("update systemuser set logincount=logincount+1,"
					+ "lastinlinetime=sysdate where userid=?1", user.getId());
			dao.execute(
					"insert into userloginlog_"
							+ new DateTime().format("yyyyMM")
							+ "(userid,logintime) " + "values(?1,sysdate)",
					user.getId());
			HashSet<Integer> funcs = new HashSet<Integer>();
			if (user.getUserGroup().isCustomer()) { // 如果是自定义用户，则按角色查找功能
				if (user.getRole().getId() != null) {
					Iterator<Integer> fit = RoleDao.getFunction(dao,
							user.getRole().getId()).iterator();
					while (fit.hasNext()) {
						funcs.add(fit.next());
					}
				}
			}
			if (user.getManuf().getManufId() != null)
				if (user.getUserGroup().isSystemManager())
					user.setUserGroup(UserGroup.MANAGER);
			Iterator<Integer> iit = dao.query(
					integerConverter,
					"select funcid from tfunction where usergroup>="
							+ user.getUserGroup().getValue()).iterator();
			while (iit.hasNext()) {
				Integer f = iit.next();
				if (!funcs.contains(f))
					funcs.add(f);
			}
			if (user.getManuf().getId() != null) {
				// 厂商用户，无权限管理机构
				funcs.remove(10220);
				funcs.remove(10221);
			} else if (!user.getUserGroup().isSystemManager()) {
				// 非系统管理员且非厂商用户，无权管理型号
				funcs.remove(14020);
				funcs.remove(14021);
			}
			user.setFuncCollection(funcs);
			return user;
		} else {
			throw new AppException("用户编码不存在!");
		}
	}

	/**
	 * 登录用户修改密码
	 */
	static public void modifyPwd(Dao dao, long userId, String oldPwd,
			String newPwd) {
		LoginUser user = getLoginUser(dao, userId);
		if (!oldPwd.equals(user.getUserPwd()))
			throw new AppException("旧密码不正确!");
		dao.execute("update systemuser set userpwd=?1 where userid=?2", newPwd,
				userId);
	}

	/**
	 * 登录用户修改个人信息
	 */
	static public void editInfo(Dao dao, QueryedUser userInfo) {
		dao.execute("update systemuser set username=?1,email=?2,"
				+ "headimage=?3,telphone=?4,mobile=?5 where userid=?6",
				userInfo.getUserName(), userInfo.getEmail(),
				userInfo.getHeadImage(), userInfo.getTelphone(),
				userInfo.getMobile(), userInfo.getId());
	}

	final private static String fieldDefs = "a.userid,a.username,a.usergroup,a.usercode"
			+ ",a.rightscope,a.orgid,orgfullname,a.manufid,manufname,a.roleid,roledesp,"
			+ "a.telphone,a.email,a.mobile";
	final private static String tableName = "systemuser";

	static private String jionWhereSql(Dao dao, LoginUser user, Integer orgId,
			Integer manufId, String keyword) {
		String qlString = " from " + tableName + " a,org b,manuf c,role d";
		String whereString = " where a.usergroup>="
				+ user.getUserGroup().getValue()
				+ " and a.userid!="
				+ user.getIdString()
				+ " and a.orgid=b.orgid and a.manufid=c.manufid(+) and a.roleid=d.roleid(+)";
		if (user.getManuf().getId() != null) {
			if (manufId == null)
				manufId = user.getManuf().getId();
			else if (!user.getManuf().getId().equals(manufId))
				throw new AppException("你不能管理其他厂商的用户");
		}
		if (orgId == null)
			throw new AppException("机构ID不能为空");
		String str = OrgDao.getOrgFilterString(dao, orgId, "b");
		if (!str.isEmpty())
			whereString += " and " + str;

		if (keyword != null && keyword.length() > 0)
			whereString += " and a.username like '%" + keyword + "%'";
		if (manufId != null) {
			whereString += " and a.manufid=" + manufId;
		}
		return qlString + whereString + " order by a.userid";
	}

	static public boolean userCodeExists(Dao dao, String userCode) {
		return !dao.query("select * from systemuser a where usercode=?1",
				userCode).isEmpty();
	}

	static public EditedUser find(Dao dao, long userId) {
		String sql = "select "
				+ fieldDefs
				+ " from systemuser a,org b,manuf c,role d "
				+ "where a.userid=?1 and a.orgid=b.orgid and a.manufid=c.manufid(+) and a.roleid=d.roleid(+)";
		Iterator<EditedUser> it = dao.query(converter, sql, userId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public EditedUser find(Dao dao, String userCode) {
		String sql = "select "
				+ fieldDefs
				+ " from systemuser a,org b,manuf c,role d "
				+ "where a.usercode=?1 and a.orgid=b.orgid and a.manufid=c.manufid(+) and a.roleid=d.roleid(+)";
		Iterator<EditedUser> it = dao.query(converter, sql, userCode)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public QueryedUser findQueryedUser(Dao dao, long userId) {
		String sql = "select "
				+ fieldDefs
				+ ",to_char(a.regtime,'yyyy-mm-dd hh24:mi:ss')"
				+ ",to_char(a.lastinlinetime,'yyyy-mm-dd hh24:mi:ss'),"
				+ "a.logincount,a.headimage"
				+ " from systemuser a,org b,manuf c,role d "
				+ "where a.userid=?1 and a.orgid=b.orgid and a.manufid=c.manufid(+) and a.roleid=d.roleid(+)";
		Iterator<QueryedUser> it = dao.query(queryConverter, sql, userId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public long add(Dao dao, long loginUserId, EditedUser user,
			String userPwd) {
		if (userCodeExists(dao, user.getUserCode()))
			throw new AppException("用户编码[" + user.getUserCode() + "]已被占用.");
		user.setUserPwd(userPwd);
		dao.insert(user, converter);
		addUserLog(dao, loginUserId, "添加用户[" + user.getUserName() + "]");
		return user.getUserId();
	}

	static public void delete(Dao dao, long loginUserId, long[] userId) {
		for (int i = 0; i < userId.length; i++) {
			EditedUser u = find(dao, userId[i]);
			if (u == null)
				throw new AppException("要删除的用户[" + userId[i] + "]不存在.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除用户[" + u.getUserId() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId, EditedUser user,
			String userPwd) {
		EditedUser u = find(dao, user.getUserId());
		if (u == null)
			throw new AppException("要编辑的用户[" + user.getUserId() + "]不存在.");
		if (!user.getUserCode().equals(u.getUserCode())
				&& userCodeExists(dao, user.getUserCode()))
			throw new AppException("用户描述[" + user.getUserName() + "]已被占用.");
		user.setUserPwd(userPwd);
		dao.update(user, converter);
		addUserLog(dao, loginUserId, "编辑用户[" + user.getUserId() + "]");
	}

	static public QueryResult<EditedUser> queryUser(Dao dao,
			boolean queryCount, long loginUserId, Integer orgId,
			Integer manufId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		LoginUser user = getLoginUser(dao, loginUserId);
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(dao, user, orgId, manufId, filterContent),
				firstResult, maxResults);
	}

	protected static final EditedFunctionConverter funcConverter = new EditedFunctionConverter();

	/**
	 * 获得系统所有功能项
	 */
	static public List<EditedFunction> getAllFunction(Dao dao) {
		return dao.query(funcConverter, "select funcid,funcdesp,funcicon,"
				+ "funcurl,funcdepth,usergroup,customenabled from tfunction "
				+ "order by funcid");
	}

	static public Collection<BaseRole> getUserManagedRoles(Dao dao,
			long loginUserId, Long userId, String keyword) {
		BaseUser user = getBaseUser(dao, loginUserId);
		String qlString = "from Role a";
		String whereString = "";
		if (!user.getUserGroup().isSystemManager()) {
			qlString += ",UserRole b";
			whereString = "a.roleid=b.roleid and b.userid=" + user.getUserId();
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "a.roledesp like '%" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		List<BaseRole> ret = dao.query(RoleDao.baseConverter,
				"select a.roleid,a.roledesp " + qlString + whereString);
		if (userId != null) {
			Iterator<?> it = ret.iterator();
			List<Integer> c = RoleDao.getUserManagedRoleIds(dao, userId);
			while (it.hasNext()) {
				BaseRole r = (BaseRole) it.next();
				if (c.contains(r.getId()))
					r.setChecked(true);
			}
		}
		return ret;
	}

	static public void setUserManagedRoles(Dao dao, long loginUserId,
			long userId, Collection<Integer> addList,
			Collection<Integer> removeList) {
		if (addList.size() == 0 && removeList.size() == 0)
			return;
		StringBuffer sb = new StringBuffer();
		Iterator<Integer> it = removeList.iterator();
		boolean first = true;
		while (it.hasNext()) {
			Integer f = it.next();
			if (!first)
				sb.append(" or ");
			else
				first = false;
			sb.append(" roleid=" + f);
		}
		if (!first) {
			dao.execute("delete from userrole where userid=" + userId
					+ " and (" + sb.toString() + ")");
		}
		List<Integer> c = dao.query(integerConverter,
				"select roleid from userrole where userid=?1", userId);
		it = addList.iterator();
		while (it.hasNext()) {
			Integer f = it.next();
			if (!c.contains(f))
				dao.execute(
						"insert into userrole(userid,roleid) values(?1,?2)",
						userId, f);
		}
		addUserLog(dao, loginUserId, "编辑用户[" + userId + "]的管理的角度");
	}

}
