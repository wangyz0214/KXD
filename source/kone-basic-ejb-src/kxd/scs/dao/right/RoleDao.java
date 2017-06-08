package kxd.scs.dao.right;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.BaseUser;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.right.converters.BaseRoleConverter;

public class RoleDao extends BaseDao {
	final public static BaseRoleConverter baseConverter = new BaseRoleConverter();
	final public static BaseRoleConverter converter = new BaseRoleConverter();
	final private static String fieldDefs = "a.roleid,a.roledesp";
	final private static String tableName = "role";

	static private String jionWhereSql(BaseUser user, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (!user.getUserGroup().isSystemManager()) {
			whereString = "a.roleid=b.roleid and b.userid=" + user.getUserId();
			qlString += ",userrole b";
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.roledesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.roleid";
	}

	static public List<BaseRole> getRoleList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter, "select roleid,roledesp"
				+ jionWhereSql(getBaseUser(dao, loginUserId), keyword));
	}

	static public boolean roleDespExists(Dao dao, String roleCode) {
		return !dao.query("select * from role a where roledesp=?1", roleCode)
				.isEmpty();
	}

	static public boolean hasUser(Dao dao, int roleId) {
		return !dao.queryPage("select * from userrole a where roleid=?1", 0, 1,
				roleId).isEmpty();
	}

	static public BaseRole find(Dao dao, int roleId) {
		String sql = "select " + fieldDefs + " from role a "
				+ "where a.roleid=?1";
		Iterator<BaseRole> it = dao.query(converter, sql, roleId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, BaseRole role) {
		if (roleDespExists(dao, role.getRoleName()))
			throw new AppException("角色描述[" + role.getRoleName() + "]已被占用.");
		dao.insert(role, converter);
		addUserLog(dao, loginUserId, "添加角色[" + role.getRoleName() + "]");
		return role.getRoleId();
	}

	static public void delete(Dao dao, long loginUserId, int[] roleId) {
		for (int i = 0; i < roleId.length; i++) {
			BaseRole u = find(dao, roleId[i]);
			if (u == null)
				throw new AppException("要删除的角色[" + roleId + "]不存在.");
			if (hasUser(dao, roleId[i]))
				throw new AppException("角色[" + u.getRoleName()
						+ "]下定义了用户，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除角色[" + u.getRoleId() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId, BaseRole role) {
		BaseRole u = find(dao, role.getRoleId());
		if (u == null)
			throw new AppException("要编辑的角色[" + role.getRoleId() + "]不存在.");
		if (!role.getRoleName().equals(u.getRoleName())
				&& roleDespExists(dao, role.getRoleName()))
			throw new AppException("角色描述[" + role.getRoleName() + "]已被占用.");
		dao.update(role, converter);
		addUserLog(dao, loginUserId, "编辑角色[" + role.getRoleId() + "]");
	}

	static public QueryResult<BaseRole> queryRole(Dao dao, boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		BaseUser user = getBaseUser(dao, loginUserId);
		return query(dao, converter, queryCount, fieldDefs, jionWhereSql(user,
				filterContent), firstResult, maxResults);
	}

	static public List<Integer> getFunction(Dao dao, int roleId) {
		BaseRole u = find(dao, roleId);
		if (u == null)
			throw new AppException("要查找的角色[" + roleId + "]不存在.");
		return dao.query(integerConverter,
				"select funcid from rolefunc where roleid=?1", roleId);
	}

	static public void setFunction(Dao dao, long loginUserId, int roleId,
			Collection<Integer> addList, Collection<Integer> removeList) {
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
			sb.append(" funcid=" + f);
		}
		if (!first) {
			dao.execute("delete from rolefunc where roleid=" + roleId
					+ " and (" + sb.toString() + ")");
		}
		List<Integer> c = dao.query(integerConverter,
				"select funcid from rolefunc where roleid=?1", roleId);
		it = addList.iterator();
		while (it.hasNext()) {
			Integer f = it.next();
			if (!c.contains(f))
				dao.execute(
						"insert into rolefunc(roleid,funcid) values(?1,?2)",
						roleId, f);
		}
		addUserLog(dao, loginUserId, "编辑角色[" + roleId + "]的权限");
	}

	static public List<Integer> getUserManagedRoleIds(Dao dao, long loginUserId) {
		return dao.query(integerConverter,
				"select roleid from userrole where userid=?1", loginUserId);
	}

}
