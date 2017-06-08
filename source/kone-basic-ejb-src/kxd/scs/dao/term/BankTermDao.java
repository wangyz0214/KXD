package kxd.scs.dao.term;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.beans.sts.CachedBankTerm;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseBankTerm;
import kxd.remote.scs.beans.device.EditedBankTerm;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedBankTermConverter;
import kxd.scs.dao.term.converters.BaseBankTermConverter;
import kxd.scs.dao.term.converters.EditedBankTermConverter;

import org.apache.log4j.Logger;

public class BankTermDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_BANKTERM_BASIC = "select a.banktermid,a.banktermdesp,a.banktermcode"
			+ ",a.batch,a.extfield,a.mackey,a.workkey,a.merchantaccount,to_char(a.signintime,'YYYYMMDDHH24MISS') from bankterm a";
	final public static String SQL_QUERY_ALL_CACHED_BANKTERM = SQL_QUERY_CACHED_BANKTERM_BASIC
			+ " order by a.banktermid";
	final public static String SQL_QUERY_ALL_BANKTERM_ID = "select a.banktermid from"
			+ " bankterm a order by a.banktermid";
	final public static String SQL_QUERY_CACHED_BANKTERM_BYID = SQL_QUERY_CACHED_BANKTERM_BASIC
			+ " where a.banktermid=?1";
	final public static CachedBankTermConverter cachedBankTermConverter = new CachedBankTermConverter(
			CacheHelper.bankTermMap);
	final public static String BANK_TERM_SIGNIN_SQL = "update bankterm set signintime=?1,workkey=?2,mackey=?3,merchantaccount=?4,"
			+ "batch=?5,extfield=?6 where banktermid=?7";
	final static Logger logger = Logger.getLogger(BankTermDao.class);

	/**
	 * 从数据库获取指定的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的应用ID列表
	 * @return 缓存应用列表
	 */
	static public List<?> getCachedBankTermsList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_BANKTERM_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.banktermid=?" + (i + 1));
			}
			sql.append(")");
		}
		List<CachedBankTerm> ls = dao.query(cachedBankTermConverter,
				sql.toString(), keys);
		if (ls.size() < 500)
			CacheHelper.bankTermMap.putCacheAll(ls);
		return ls;
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
	static public CachedBankTerm getCachedBankTerm(Dao dao, int id) {
		return dao.querySingalAndSetCache(CacheHelper.bankTermMap,
				new CachedBankTerm(id, true), 600, cachedBankTermConverter,
				SQL_QUERY_CACHED_BANKTERM_BYID, id);
	}

	static public void bankTermSignin(Dao dao, CachedBankTerm bankTerm) {
		logger.debug("更新银联终端[workkey=" + bankTerm.getWorkKey() + ",macKey="
				+ bankTerm.getMacKey() + ",merchant="
				+ bankTerm.getMerchantAccount() + ",batch="
				+ bankTerm.getBatch() + "]");
		dao.execute(BANK_TERM_SIGNIN_SQL, new Date(), bankTerm.getWorkKey(),
				bankTerm.getMacKey(), bankTerm.getMerchantAccount(),
				bankTerm.getBatch(), bankTerm.getExtField(), bankTerm.getId());
		CacheHelper.bankTermMap.put(bankTerm.getId(), bankTerm);
	}

	final public static BaseBankTermConverter baseConverter = new BaseBankTermConverter();
	final public static EditedBankTermConverter converter = new EditedBankTermConverter();
	final private static String fields = "a.banktermid,a.banktermcode,a.banktermdesp,a.merchantaccount,a.extfield";
	final private static String tableName = "bankterm";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.banktermcode like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.banktermid";
	}

	static public List<BaseBankTerm> getBankTermList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter,
				"select a.banktermid,a.banktermcode,a.banktermdesp,a.merchantaccount"
						+ jionWhereSql(keyword));
	}

	static public boolean bankTermCodeExists(Dao dao, String code) {
		return !dao.query("select * from bankterm a where banktermcode=?1",
				code).isEmpty();
	}

	static public boolean hasTerm(Dao dao, int id) {
		return !dao.queryPage("select * from term a where banktermid=?1", 0, 1,
				id).isEmpty();
	}

	static public EditedBankTerm find(Dao dao, int id) {
		String sql = "select " + fields + " from bankterm a "
				+ "where a.banktermid=?1";
		Iterator<EditedBankTerm> it = dao.query(converter, sql, id).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public EditedBankTerm find(Dao dao, String termCode,
			String merchantaccount) {
		String sql = "select " + fields + " from bankterm a "
				+ "where a.banktermcode=?1 and a.merchantaccount=?2";
		Iterator<EditedBankTerm> it = dao.query(converter, sql, termCode,
				merchantaccount).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedBankTerm o) {
		if (find(dao, o.getBankTermCode(), o.getMerchantAccount()) != null)
			throw new AppException("银联终端编码[" + o.getBankTermCode() + "]已被占用.");
		dao.insert(o, converter);
		addUserLog(dao, loginUserId, "添加银联终端[" + o.getBankTermDesp() + "]");
		return o.getId();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] bankTermId) {
		for (int i = 0; i < bankTermId.length; i++) {
			EditedBankTerm u = find(dao, bankTermId[i]);
			if (u == null)
				throw new AppException("要删除的银联终端[" + bankTermId[i] + "]不存在.");
			if (hasTerm(dao, u.getId()))
				throw new AppException("有终端使用了银联终端[" + u.getBankTermDesp()
						+ "]，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除银联终端[" + u.getId() + "]");
		}
		CacheHelper.bankTermMap.removeAll(bankTermId);
	}

	static public void edit(Dao dao, long loginUserId, EditedBankTerm o) {
		EditedBankTerm u = find(dao, o.getId());
		if (u == null)
			throw new AppException("要编辑的银联终端[" + o.getId() + "]不存在.");
		if (!(o.getBankTermCode().equals(u.getBankTermCode()) && o
				.getMerchantAccount().equals(u.getMerchantAccount()))
				&& find(dao, o.getBankTermCode(), o.getMerchantAccount()) != null)
			throw new AppException("银联终端编码[" + o.getBankTermCode() + "]已被占用.");
		dao.update(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.bankTermMap.remove(o.getId());
		addUserLog(dao, loginUserId, "编辑银联终端[" + o.getId() + "]");
	}

	static public QueryResult<EditedBankTerm> queryBankTerm(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fields,
				jionWhereSql(filterContent), firstResult, maxResults);
	}

}
