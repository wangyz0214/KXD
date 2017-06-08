package kxd.scs.beans;

import java.util.Hashtable;
import java.util.Iterator;

import kxd.engine.dao.Dao;
import kxd.util.DateTime;

/**
 * 序列号生成器
 * 
 * @author 赵明
 * 
 */

public class SerialFactory {
	static class MySerial {
		public long value = 0;
		public int day = 0;
		public String key;
		long minValue = 0;
		long maxValue;
		boolean dayChange;
		long cachedValue;
		long dayBS;

		public MySerial(String key, boolean dayChange, long minValue,
				long maxValue) {
			this.minValue = minValue;
			this.dayChange = dayChange;
			this.maxValue = maxValue;
			String o = Long.toString(maxValue);
			dayBS = 1;
			for (int i = 0; i < o.length(); i++)
				dayBS *= 10;
			this.key = key;
		}

		synchronized public void init(Dao dao) {
			if (dayChange) {
				int day = new DateTime().getFullDay();
				Iterator<?> it = dao.query(
						"select value from dayserial where "
								+ "serialid=?1 and day=?2", key, day)
						.iterator();
				if (it.hasNext()) {
					Object o = it.next();
					if (o != null) {
						this.day = day;
						this.value = Integer.valueOf(o.toString());
					} else {
						this.value = minValue;
						dao.execute("update dayserial set value=?1 "
								+ "where serialid=?2 and day=?3)", minValue,
								key, day);
					}
				} else {
					this.value = minValue;
					dao.execute("insert into dayserial(serialid,day,value) "
							+ "values(?1,?2,?3)", key, day, minValue);
				}
				this.day = day;
			} else {
				Iterator<?> it = dao
						.query("select value from circleserial where serialid=?1",
								key).iterator();
				if (it.hasNext()) {
					Object o = it.next();
					if (o != null && value < maxValue) {
						this.value = Long.valueOf(o.toString());
					} else {
						this.value = minValue;
						dao.execute("update circleserial set value=?1"
								+ " where serialid=?2", minValue, key);
					}
				} else {
					this.value = minValue;
					dao.execute("insert into circleserial(serialid,value)"
							+ " values(?1,?2)", key, minValue);
				}
			}
			cachedValue = value;
		}

		synchronized public long getAndNext(Dao dao) {
			if (dayChange) {
				int day = new DateTime().getFullDay();
				if (this.day != day) {
					init(dao);
				}
			} else {
				if (value > maxValue) {
					init(dao);
				}
			}
			long curValue = value;
			value++;
			if (dao == null)
				return curValue;
			if (cachedValue <= curValue) { // 牺牲序列号的连续性，提高100倍性能
				cachedValue = curValue + 100;
				if (dayChange) {
					Iterator<?> it = dao.query(
							"select value from dayserial where "
									+ "serialid=?1 and day=?2", key, day)
							.iterator();
					if (it.hasNext()) {
						dao.execute("update dayserial set value=?1"
								+ " where serialid=?2 and day=?3", cachedValue,
								key, day);
					} else {
						dao.execute(
								"insert into dayserial(serialid,day,value) "
										+ "values(?1,?2,?3)", key, day,
								cachedValue);
					}
				} else {
					Iterator<?> it = dao.query(
							"select value from circleserial where serialid=?1",
							key).iterator();
					if (it.hasNext()) {
						dao.execute("update circleserial set value=?1"
								+ " where serialid=?2", cachedValue, key);
					} else {
						dao.execute("insert into circleserial(serialid,value)"
								+ " values(?1,?2)", key, minValue);
					}
				}
			}
			if (dayChange) {
				long r = (long) this.day * dayBS + curValue;
				return r;
			} else
				return curValue;
		}
	}

	static Hashtable<String, MySerial> serials = new Hashtable<String, MySerial>();

	static synchronized private MySerial getSerial(Dao dao, String key,
			boolean dayChange, long minValue, long maxValue) {
		MySerial se = serials.get(key);
		if (se == null) {
			se = new MySerial(key, dayChange, minValue, maxValue);
			serials.put(key, se);
			se.init(dao);
		}
		return se;
	}

	/**
	 * 获取下一个流水号
	 * 
	 * @param key
	 * @param dayChange
	 * @param start
	 * @param max
	 * @return
	 */
	static public long getNextSerial(Dao dao, String key, boolean dayChange,
			long start, long max) {
		return getSerial(dao, key, dayChange, start, max).getAndNext(dao);
	}
}
