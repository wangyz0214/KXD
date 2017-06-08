package kxd.scs.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.dao.DaoConverter;
import kxd.remote.scs.beans.BaseUser;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.right.converters.BaseUserConverter;

public class BaseDao {
	static public void addUserLog(Dao dao, long userId, String msg) {
		byte[] b = msg.getBytes();
		if (b.length > 255) {
			msg = new String(b, 0, 255);
		}
		long id = Long.valueOf(dao.query("select seq_usolog.nextval from dual")
				.iterator().next().toString());
		dao.execute("insert into useroptionlog(optionid,optiontime,"
				+ "optioncontent,userid) values(?1,sysdate,?2,?3)", id, msg,
				userId);
	}

	final static protected IntegerConverter integerConverter = new IntegerConverter();
	final static protected ShortConverter shortConverter = new ShortConverter();
	final static protected LongConverter longConverter = new LongConverter();
	final static protected StringConverter stringConverter = new StringConverter();
	final static protected BaseUserConverter baseUserConverter = new BaseUserConverter();

	static public BaseUser getBaseUser(Dao dao, long loginUserId) {
		List<BaseUser> ls = dao.query(baseUserConverter,
				"select userid,username,usergroup from systemuser where userid=?1",
				loginUserId);
		if (ls.size() > 0)
			return ls.get(0);
		else
			return null;
	}

	/**
	 * 查询数据
	 * 
	 * @param <E>
	 * @param dao
	 * @param queryCount
	 * @param fieldDefs
	 * @param whereStrings
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	static public <E> QueryResult<E> query(Dao dao, DaoConverter<E> converter,
			boolean queryCount, String fieldsDef, String fromAfterString,
			int firstResult, int maxResults) {
		QueryResult<E> result = new QueryResult<E>(0, new ArrayList<E>());
		if (queryCount) {
			String ql = "select count(*) " + fromAfterString;
			int c = Integer.valueOf(dao.query(ql).get(0).toString());
			result.setCount(c);
		}
		String ql = "select " + fieldsDef + " " + fromAfterString;
		Iterator<E> it = dao.queryPage(converter, ql, firstResult, maxResults)
				.iterator();
		while (it.hasNext()) {
			E u = it.next();
			result.getResultList().add(u);
		}
		return result;
	}
}
