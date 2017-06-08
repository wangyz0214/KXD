package kxd.scs.dao.term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CachedAlarmCode;
import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermConfig;
import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.TermHelper;
import kxd.engine.scs.monitor.CachedMonitoredTerm;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseTerm;
import kxd.remote.scs.beans.BaseTermMoveItem;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.beans.device.QueryedTerm;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.FaultProcFlag;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedTermConverter;
import kxd.scs.dao.right.OrgDao;
import kxd.scs.dao.right.UserDao;
import kxd.scs.dao.term.converters.BaseTermConverter;
import kxd.scs.dao.term.converters.EditedTermConverter;
import kxd.scs.dao.term.converters.QueryedTermConverter;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KeyValue;
import kxd.util.StringUnit;

public class TermDao extends BaseDao {
	final public static String FIELDS_QUERY_CACHED_TERM_BASIC = "select a.termid,a.termcode,a.termdesp,a.typeid,a.appid,a.banktermid,"
			+ "a.orgid,a.alarmstatus,"
			+ "to_char(a.lastfaultrecordtime,'YYYYMMDDHH24MISS'),"
			+ "to_char(a.lastfaulttime,'YYYYMMDDHH24MISS'),"
			+ "to_char(a.lastinlinetime,'YYYYMMDDHH24MISS'),"
			+ "to_char(a.lastprocesstime,'YYYYMMDDHH24MISS'),a.loginsessionid,a.processflag,"
			+ "a.status,a.address,a.areacode,a.opentime,a.closetime,a.contacter,"
			+ "a.dayruntime,a.guid,a.manufno,a.starttime,a.workkey,a.extfield0,a.extfield1,a.extfield2,a.extfield3,a.extfield4,settlement_type";
	// final private static String SQL_QUERY_CACHED_TERM_BASIC =
	// FIELDS_QUERY_CACHED_TERM_BASIC
	// + " from term a";
	// final private static String SQL_QUERY_CACHED_TERM_BYID =
	// SQL_QUERY_CACHED_TERM_BASIC
	// + " where a.termid=?1";
	final private static String TERM_LOGIN_SQL = "update term set workkey=?1,loginsessionid=?2,"
			+ "status=2,lastinlinetime=?3,ip=?4,starttime=?5,loginurl=?6 where termid=?7";
	final private static String TERM_ACTIVE_SQL = "update term set guid=?1,starttime=?2,"
			+ "status=2 where termid=?3";
	final private static String CACHED_TERM_QUERY = FIELDS_QUERY_CACHED_TERM_BASIC // modify by jurstone 20120611
			+ ",c.parentpath,configid,businesscategroyids,businessids,startdate,"
			+ "enddate,openmode,opentimes,reason,payways,payitems,a.ip from term a , termbusinessclose b,org c where a.termid = b.termid(+) and a.orgid=c.orgid";
	final private static CachedTermConverter termConverter = new CachedTermConverter(
			CacheHelper.termMap);

	/**
	 * 从数据库获取指定的终端数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的终端ID列表
	 * @return 缓存终端列表
	 */
	static public List<?> getCachedTermsList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(CACHED_TERM_QUERY);
		if (keys.size() > 0) {
			sql.append(" and (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.termid=?" + (i + 1));
			}
			sql.append(")");
		}
		sql.append(" order by a.termid,configid desc");
		List<CachedTermConfig> terms = dao.query(termConverter, sql.toString(),
				keys);
		if (terms.size() < 500)
			CacheHelper.termMap.putAll(terms);
		return terms;
	}

	/**
	 * 通过终端ID，获取终端数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param termId
	 *            终端ID
	 * @return 缓存终端对象
	 */
	static public CachedTermConfig getCachedTerm(Dao dao, int termId) {
		return dao.querySingalAndSetCache(CacheHelper.termMap,
				new CachedTermConfig(termId, true), 600, termConverter,
				CACHED_TERM_QUERY
						+ " and a.termid=?1 order by a.termid,configid desc",
				termId);
	}

	static public void buildCache(Dao dao) {
		dao.queryAndSetCache(CacheHelper.termMap, termConverter,
				CACHED_TERM_QUERY + " order by a.termid,configid desc");
	}

	static public void termLogin(Dao dao, String ip, String loginurl,
			CachedTerm term) {
		term.setLastInlineTime(System.currentTimeMillis());
		term.setLastLoginTime(term.getLastInlineTime());
		term.setStatus(TermStatus.NORMAL);
		Date startTime;
		if (term.getStartTime() == 0) {
			startTime = new Date();
			term.setStartTime(startTime.getTime());
		} else
			startTime = new Date(term.getStartTime());
		dao.execute(TERM_LOGIN_SQL, term.getWorkKey(),
				term.getLoginSessionID(), new Date(), ip, startTime,
				DataUnit.checkBytesLength(loginurl, 255), term.getId());
		try {
			TermHelper.termLogined(term.getId());
		} catch (NamingException e) {
		}
		CachedTermConfig t = CacheHelper.termMap.get(term.getId());
		if (t != null) {
			t.setTerm(term);
			CacheHelper.termMap.putCache(term.getId(), t);
		}
	}

	static public int termActive(Dao dao, String manufNo, String guid) {
		Iterator<?> it = dao
				.query("select status,guid,termid from term where manufno=?1",
						manufNo).iterator();
		if (it.hasNext()) {
			Object o[] = (Object[]) it.next();
			if (!guid.equals(o[1]))
				throw new AppException("非法终端：终端的GUID不一致.");
			int id = Integer.valueOf(o[2].toString());
			TermStatus status = TermStatus.valueOfIntString(o[0]);
			switch (status) {
			case NOTACTIVE:
			case NOTINSTALL: {
				dao.execute(TERM_ACTIVE_SQL, guid, new Date(), id);
				CacheHelper.termMap.remove(id);
				try {
					TermHelper.termActived(id);
				} catch (Throwable e) {
				}
				return id;
			}
			default:
				throw new AppException("终端[出厂编码=" + manufNo + "]已经激活，不能再次激活");
			}
		} else
			throw new AppException("未找到出厂编码为[" + manufNo + "]的终端");

	}

	static public void termPauseResume(Dao dao, Collection<Integer> terms,
			boolean pause) {
		if (terms.size() == 0)
			return;
		StringBuffer sb = new StringBuffer("update term set status="
				+ (pause ? 3 : 2) + " where status>1 and (");
		boolean first = true;
		for (int id : terms) {
			if (!first) {
				sb.append(" or ");
			} else
				first = false;
			sb.append("termid=" + id);
		}
		sb.append(")");
		dao.execute(sb.toString());
		try {
			AdminHelper.termPauseResume(terms, pause);
		} catch (Throwable e) {
		}
	}

	final public static String UPDATE_TERM_STATUS_SQL = "update term set status=?1,alarmstatus=?2,"
			+ "lastinlinetime=?3,lastfaulttime=?4,lastprocesstime=?5,"
			+ "processflag=?6 where termid=?7";

	/**
	 * 更新终端状态
	 * 
	 * @param dao
	 * @param term
	 */
	static public void updateTermStatus(Dao dao, CachedMonitoredTerm term) {
		String sql = "update term set status="
				+ Integer.toString(term.getStatus().getValue())
				+ ",alarmstatus="
				+ Integer.toString(term.getAlarmStatus().getValue())
				+ ",lastinlinetime=to_date('"
				+ new DateTime(term.getLastOnlineTime())
						.format("yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		if (term.getLastFaultTime() == null)
			sql += ",lastfaulttime=null";
		else
			sql += ",lastfaulttime=to_date('"
					+ new DateTime(term.getLastFaultTime())
							.format("yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		if (term.getLastProcessTime() == null)
			sql += ",lastprocesstime=null";
		else
			sql += ",lastprocesstime=to_date('"
					+ new DateTime(term.getLastProcessTime())
							.format("yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		sql += ",processflag=" + term.getProcessFlag().getValue()
				+ " where termid=" + term.getIdString();
		dao.execute(sql);
	}

	/**
	 * 更新终端某个设备的状态
	 * 
	 * @param dao
	 * @param term
	 */
	final public static String UPDATE_TERM_DEVICESTATUS_SQL = "update termdevicestatus set status=?1,statusdesp=?2,faultformid=?3 "
			+ " where termid=?4 and deviceid=?5";
	final public static String INSERT_TERM_DEVICESTATUS_SQL = "insert into termdevicestatus"
			+ "(status,statusdesp,faultformid,termid,deviceid) values(?1,?2,?3,?4,?5)";

	/**
	 * 更新终端设备状态
	 * 
	 * @param dao
	 * @param term
	 * @param status
	 * @param oldLeve
	 * @param newLevel
	 */
	static public void updateTermDeviceStatus(Dao dao,
			CachedMonitoredTerm term, CachedDeviceStatus status,
			KeyValue<CachedAlarmCode, AlarmLevel> oldLevel,
			KeyValue<CachedAlarmCode, AlarmLevel> newLevel) {
		if (newLevel.getValue().equals(AlarmLevel.FAULT)) {
			int categoryid = 999;
			if (newLevel.getKey() != null)
				categoryid = newLevel.getKey().getAlarmCategoryId();
			if (status.getFaultWorkFormId() != null && oldLevel != null
					&& oldLevel.getValue().equals(AlarmLevel.FAULT)) {
				int oldcategoryid = 999;
				if (oldLevel.getKey() != null)
					oldcategoryid = oldLevel.getKey().getAlarmCategoryId();
				if (oldcategoryid != categoryid) {// 故障升级
					dao.execute("update faultworkform set "
							+ "processflag=?1 where faultformid=?2",
							FaultProcFlag.FAULT_UPGRADED.getValue());
					status.setFaultWorkFormId(null);// 以便生成新的故障工单
				}
			}
			if (status.getFaultWorkFormId() == null) { // 生成故障工单
				status.setFaultWorkFormId(Long.valueOf(dao
						.query("select seq_workform.nextval from dual").get(0)
						.toString()));
				String msg = (status.getMessage() == null ? "" : status
						.getMessage()) + "[" + status.getStatus() + "]";
				dao.execute("insert into faultworkform(faultformid,"
						+ "termid,alarmcategoryid,deviceid,"
						+ "faultdesp,faulttime,restoretime) "
						+ "values(?1,?2,?3,?4,?5,sysdate,?6)", status
						.getFaultWorkFormId(), term.getId(), categoryid, status
						.getId(), msg, new DateTime().addYears(100).getTime());
			}
		} else if (status.getFaultWorkFormId() != null) { // 恢复
			dao.execute("update faultworkform set processflag=?1,"
					+ "restoretime=?2,processresult=?3 "
					+ "where faultformid=?4 and processflag<2",
					FaultProcFlag.AUTO_RESTORED.getValue(), new Date(), "自动恢复",
					status.getFaultWorkFormId());
			status.setFaultWorkFormId(null);
		}
		if (dao.execute(UPDATE_TERM_DEVICESTATUS_SQL, status.getStatus(),
				status.getMessage(), status.getFaultWorkFormId(), term.getId(),
				status.getId()) == 0) { // 更新失败，则插入
			dao.execute(INSERT_TERM_DEVICESTATUS_SQL, status.getStatus(),
					status.getMessage(), status.getFaultWorkFormId(),
					term.getId(), status.getId());
		}
	}

	/**
	 * 更新终端开机率报表
	 * 
	 * @param dao
	 * @param termId
	 * @param code
	 */
	static public void updateTermOpenRateReport(Dao dao, int termId,
			int busyTimes, int idleTimes) {
		DateTime now = new DateTime();
		int month = Integer.valueOf(now.format("yyyyMM"));
		int day = now.getDay();
		if (dao.execute("update report_opened_term_" + month
				+ " set idle_time_" + day + "=idle_time_" + day + "+"
				+ idleTimes + ",busy_time_" + day + "=busy_time_" + day + "+"
				+ busyTimes + " where termid=" + termId) == 0)
			dao.execute("insert into report_opened_term_" + month
					+ "(termid,idle_time_" + day + ",busy_time_" + day
					+ ") values(?1,?2,?3)", termId, idleTimes, busyTimes);
	}

	static public void submitCashModify(Dao dao, int termId, int day,
			boolean detailModified, boolean traceModified) {
		String sql = "select termid from billmodify where termid=" + termId
				+ " and day=" + day;
		if (dao.query(sql).isEmpty()) {
			dao.execute("insert into billmodify(termid,day,"
					+ "detailmodified,tracemodified,lastmodified) values("
					+ termId + "," + day + "," + (detailModified ? 1 : 0) + ","
					+ (traceModified ? 1 : 0) + ",sysdate)");
		} else if (detailModified || traceModified) {
			sql = "";
			if (detailModified)
				sql = "detailmodified=1";
			if (traceModified) {
				if (!sql.isEmpty())
					sql += ",";
				sql += "tracemodified=1";
			}
			sql = "update billmodify set " + sql
					+ ",lastmodified=sysdate where termid=" + termId
					+ " and day=" + day;
			dao.execute(sql);
		}
	}

	static public void submitTermFilesAttr(Dao dao, int termId, String filesAttr) {
		if (dao.execute("update termfileattr set files=?1,lastmodified=sysdate"
				+ " where termid=?2", filesAttr, termId) == 0)
			dao.execute("insert into termfileattr(termid,files,lastmodified)"
					+ " values(?1,?2,sysdate)", termId, filesAttr);
	}

	final public static BaseTermConverter baseConverter = new BaseTermConverter();
	final public static EditedTermConverter converter = new EditedTermConverter();
	final public static QueryedTermConverter queryedConverter = new QueryedTermConverter();
	final private static String fields = "a.termid,a.termcode,a.termdesp,"
			+ "a.typeid,typedesp,a.appid,appdesp,a.banktermid,banktermcode,merchantaccount,"
			+ "a.orgid,a.status,a.alarmstatus,a.manufno,a.address,a.contacter,a.dayruntime"
			+ ",a.opentime,a.closetime,a.guid,a.areacode,a.extfield0,a.extfield1,a.extfield2,"
			+ "a.extfield3,a.extfield4,a.settlement_type";
	final private static String queryedFields = "a.termid,a.termcode,a.termdesp,"
			+ "a.typeid,typedesp,a.appid,appdesp,a.orgid,orgfullname,a.banktermid,"
			+ "a.status,a.alarmstatus,a.settlement_type";
	final private static String tableName = "term";

	static private String jionWhereSql(Dao dao, LoginUser user,
			String extTables, String extWheres, Integer orgId, Integer manufId,
			Integer typeId, String bankTermNo, String manufNo, String keyword) {
		String qlString = " from " + tableName + " a,termtype b,app c ";
		String whereString = "a.typeid=b.typeid and a.appid=c.appid and a.orgid=e.orgid";
		if (extTables != null) {
			qlString += "," + extTables;
			whereString += " and " + extWheres;
		}
		if (orgId != null) {
			qlString += ",org e";
			whereString += " and " + OrgDao.getOrgFilterString(dao, orgId, "e");
		} else if (!user.getUserGroup().isSystemManager()) {
			qlString += ",org e";
			whereString += " and "
					+ OrgDao.getOrgFilterString(dao, user.getOrg().getId(), "e");
		}
		BaseManuf manuf = user.getManuf();
		if (manuf.getManufId() != null) {
			whereString += " and b.manufid=" + manuf.getManufId();
		} else if (manufId != null)
			whereString += " and b.manufid=" + manufId;
		if (typeId != null) {
			whereString += " and b.typeid=" + typeId;
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "(a.termcode like '" + keyword
						+ "%' or a.termdesp like '" + keyword + "%')";
			}
		}
		if (bankTermNo != null) {
			if (bankTermNo.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "(a.banktermcode like '" + bankTermNo + "%')";
			}
		}
		if (manufNo != null) {
			if (manufNo.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "(a.manufno like '" + manufNo + "%')";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.termid";
	}

	static public List<BaseTerm> getTermList(Dao dao, long loginUserId,
			String keyword) {
		String sql = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (sql.length() > 0)
					sql += " and ";
				sql = "(a.termcode like '" + keyword
						+ "%' or a.termdesp like '" + keyword + "%')";
			}
		}
		sql = "select a.termid,a.termcode,a.termdesp from term" + sql;
		return dao.query(baseConverter, sql);
	}

	static public boolean termCodeExists(Dao dao, String code) {
		return !dao.query("select * from term a where termcode=?1", code)
				.isEmpty();
	}

	static public boolean manufNoExists(Dao dao, String code) {
		return !dao.query("select * from term a where manufno=?1", code)
				.isEmpty();
	}

	static public EditedTerm find(Dao dao, int id) {
		String sql = "select " + fields
				+ " from term a,termtype b,app c,bankterm d,org e "
				+ "where a.termid=?1 and a.typeid=b.typeid and a.appid=c.appid"
				+ " and a.banktermid=d.banktermid(+) and a.orgid=e.orgid";
		Iterator<EditedTerm> it = dao.query(converter, sql, id).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public EditedTerm find(Dao dao, String manufNo) {
		String sql = "select "
				+ fields
				+ " from term a,termtype b,app c,bankterm d,org e "
				+ "where a.manufno=?1 and a.typeid=b.typeid and a.appid=c.appid"
				+ " and a.banktermid=d.banktermid(+) and a.orgid=e.orgid";
		Iterator<EditedTerm> it = dao.query(converter, sql, manufNo).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedTerm o) {
		if (termCodeExists(dao, o.getTermCode()))
			throw new AppException("终端编码[" + o.getTermCode() + "]已被占用.");
		if (manufNoExists(dao, o.getManufNo()))
			throw new AppException("出厂编码[" + o.getManufNo() + "]已被占用.");
		if (o.getBankTerm() != null
				&& o.getBankTerm().getBankTermCode() != null
				&& o.getBankTerm().getMerchantAccount() != null
				&& !o.getBankTerm().getBankTermCode().trim().isEmpty()
				&& !o.getBankTerm().getMerchantAccount().trim().isEmpty()) {
			o.setBankTerm(BankTermDao.find(dao, o.getBankTerm()
					.getBankTermCode(), o.getBankTerm().getMerchantAccount()));
			if (o.getBankTerm() == null)
				throw new AppException("找不到银联终端编码");
		}
		o.setAlarmStatus(AlarmStatus.NORMAL);
		o.setStatus(TermStatus.NOTINSTALL);
		dao.insert(o, converter);
		EditedOrg org = OrgDao.find(dao, o.getOrg().getId());
		List<Integer> ls = StringUnit.splitToInt1(org.getTerms(), ",");
		if (!ls.contains(o.getId())) {
			ls.add(o.getId());
			dao.execute("update org set termlist=?1 where orgid=?2",
					StringUnit.listToString(ls), o.getOrg().getId());
		}
		try {
			AdminHelper.termAdded(o);
		} catch (Throwable e) {
			throw new AppException("更新缓存失败：", e);
		}
		addUserLog(dao, loginUserId, "添加终端[" + o.getTermDesp() + "]");
		return o.getId();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] termId) {
		for (int i = 0; i < termId.length; i++) {
			EditedTerm u = find(dao, termId[i]);
			if (u == null)
				throw new AppException("要删除的终端[" + termId[i] + "]不存在.");
			dao.delete(u, converter);
			EditedOrg org = OrgDao.find(dao, u.getOrg().getId());
			List<Integer> ls = StringUnit.splitToInt1(org.getTerms(), ",");
			if (ls.contains(u.getId())) {
				ls.remove(u.getId());
				dao.execute("update org set termlist=?1 where orgid=?2",
						StringUnit.listToString(ls), u.getOrg().getId());
			}
			try {
				CacheHelper.termCodeMap.remove(u.getTermCode());
				AdminHelper.termRemoved(u);
			} catch (Throwable e) {
				throw new AppException("更新缓存失败：", e);
			}
			addUserLog(dao, loginUserId, "删除终端[" + u.getId() + "]");
		}
		CacheHelper.termMap.removeAll(termId);
	}

	static public void edit(Dao dao, long loginUserId, EditedTerm o) {
		EditedTerm u = find(dao, o.getId());
		if (u == null)
			throw new AppException("要编辑的终端[" + o.getId() + "]不存在.");
		if (o.getTermCode() != null) {
			if (!o.getTermCode().equals(u.getTermCode())) {
				if (termCodeExists(dao, o.getTermCode()))
					throw new AppException("终端编码[" + o.getTermCode() + "]已被占用.");
				CacheHelper.termCodeMap.remove(u.getTermCode());
			}
			if (!o.getManufNo().equals(u.getManufNo())
					&& manufNoExists(dao, o.getManufNo()))
				throw new AppException("出厂编码[" + o.getManufNo() + "]已被占用.");
			if (o.getBankTerm() != null
					&& o.getBankTerm().getBankTermCode() != null
					&& o.getBankTerm().getMerchantAccount() != null
					&& !o.getBankTerm().getBankTermCode().trim().isEmpty()
					&& !o.getBankTerm().getMerchantAccount().trim().isEmpty()) {
				if (!(o.getBankTerm().getBankTermCode()
						.equals(u.getBankTerm().getBankTermCode()) && o
						.getBankTerm().getMerchantAccount()
						.equals(u.getBankTerm().getMerchantAccount()))) {
					o.setBankTerm(BankTermDao.find(dao, o.getBankTerm()
							.getBankTermCode(), o.getBankTerm()
							.getMerchantAccount()));
					if (o.getBankTerm() == null)
						throw new AppException("找不到银联终端编码");
				} else
					o.setBankTerm(u.getBankTerm());
			} else
				o.getBankTerm().setBankTermId(null);
			o.setOrg(u.getOrg());
			if (o.getStatus() == null)
				o.setStatus(u.getStatus());
			if (!u.getTermType().equals(o.getTermType())) { // 型号变化，删除之前的状态
				dao.execute("delete from termdevicestatus where termid="
						+ o.getIdString());
			}
			dao.update(o, converter);
			try {
				AdminHelper.termModified(u, o);
			} catch (Throwable e) {
				throw new AppException("更新缓存失败：", e);
			}
		} else
			dao.update(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.termMap.remove(o.getId());
		addUserLog(dao, loginUserId, "编辑终端[" + o.getId() + "]");
	}

	static public QueryResult<QueryedTerm> queryTerm(Dao dao, String extTables,
			String extWheres, boolean queryCount, long loginUserId,
			Integer orgId, Integer manufId, Integer typeId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(
				dao,
				queryedConverter,
				queryCount,
				queryedFields,
				jionWhereSql(dao, UserDao.getLoginUser(dao, loginUserId),
						extTables, extWheres, orgId, manufId, typeId, "", "",
						filterContent), firstResult, maxResults);
	}

	/**
	 * 2012-6-14 增加查找终端编码，出厂编码，银联终端编码
	 * 
	 * @param dao
	 * @param extTables
	 * @param extWheres
	 * @param queryCount
	 * @param loginUserId
	 * @param orgId
	 * @param manufId
	 * @param typeId
	 * @param termNo
	 * @param bankTermNo
	 * @param manufNo
	 * @param filter
	 * @param filterContent
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	static public QueryResult<QueryedTerm> queryTerm(Dao dao, String extTables,
			String extWheres, boolean queryCount, long loginUserId,
			Integer orgId, Integer manufId, Integer typeId, String bankTermNo,
			String manufNo, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return query(
				dao,
				queryedConverter,
				queryCount,
				queryedFields,
				jionWhereSql(dao, UserDao.getLoginUser(dao, loginUserId),
						extTables, extWheres, orgId, manufId, typeId,
						bankTermNo, manufNo, filterContent), firstResult,
				maxResults);
	}

	static public void move(Dao dao, long loginUserId, int termId, int orgId) {
		EditedTerm u = find(dao, termId);
		if (u != null && !u.getOrg().getOrgId().equals(orgId)) {
			long id = Long.valueOf(dao
					.query("select seq_termmovelist.nextval from dual").get(0)
					.toString());
			EditedOrg org = OrgDao.find(dao, u.getOrg().getId());
			List<Integer> ls = StringUnit.splitToInt1(org.getTerms(), ",");
			if (ls.contains(u.getId())) {
				ls.remove(u.getId());
				dao.execute("update org set termlist=?1 where orgid=?2",
						StringUnit.listToString(ls), u.getOrg().getId());
			}
			org = OrgDao.find(dao, orgId);
			ls = StringUnit.splitToInt1(org.getTerms(), ",");
			if (!ls.contains(u.getId())) {
				ls.add(u.getId());
				dao.execute("update org set termlist=?1 where orgid=?2",
						StringUnit.listToString(ls), orgId);
			}
			dao.execute("insert into termmovelist(termmoveid,termid,"
					+ "orgid,movetime) values(?1,?2,?3,sysdate)", id, termId, u
					.getOrg().getId());
			dao.execute("update term set orgid=?1 where termid=?2", orgId,
					u.getId());
			CacheHelper.termMap.remove(termId);
			try {
				u.setOrg(new BaseOrg(orgId));
				AdminHelper.termModified(u, u);
			} catch (Throwable e) {
				throw new AppException("更新缓存失败：", e);
			}
			addUserLog(dao, loginUserId, "迁移终端[" + u.getTermId() + "]");
		}
	}

	static public List<BaseTermMoveItem> getTermMoveList(Dao dao,
			long loginUserId, Integer termId) {
		Iterator<?> it = dao.query(
				"select a.orgid,b.orgfullname,"
						+ "to_char(a.movetime,'yyyy-mm-dd hh24:mi:ss')"
						+ " from termmovelist a," + "org b where "
						+ "a.termid=?1 and a.orgid=b.orgid "
						+ "order by a.movetime asc", termId).iterator();
		List<BaseTermMoveItem> ls = new ArrayList<BaseTermMoveItem>();
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			BaseTermMoveItem t;
			try {
				t = new BaseTermMoveItem(new BaseOrg(Integer.valueOf(o[0]
						.toString())), new DateTime(o[2].toString(),
						"yyyy-MM-dd HH:mm:ss").getTime());
				t.getOrg().setOrgFullName((String) o[1]);
				ls.add(t);
			} catch (Throwable e) {
			}
		}
		return ls;
	}
}
