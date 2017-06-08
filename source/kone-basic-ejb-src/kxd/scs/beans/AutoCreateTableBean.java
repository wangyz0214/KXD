package kxd.scs.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.engine.dao.Dao;
import kxd.remote.scs.interfaces.AutoCreateTableBeanRemote;
import kxd.util.DateTime;
import kxd.util.KoneUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Stateless(name = "kxd-ejb-autoCreateTableBean", mappedName = "kxd-ejb-autoCreateTableBean")
public class AutoCreateTableBean implements AutoCreateTableBeanRemote {
	static class SqlString {
		public String sql;
		public boolean isAddPartition = false;

		public SqlString(String sql, boolean isAddPartition) {
			super();
			this.sql = sql;
			this.isAddPartition = isAddPartition;
		}

		public SqlString(String sql) {
			super();
			this.sql = sql;
		}

		@Override
		public String toString() {
			return sql;
		}

	}

	static class Table {
		int start = 0, end = 1;
		String cycle;
		ArrayList<SqlString> sqls = new ArrayList<SqlString>();
	}

	static Logger logger = Logger.getLogger(AutoCreateTableBean.class);
	static ConcurrentHashMap<String, Table> tables = new ConcurrentHashMap<String, Table>();
	static {
		try {
			loadConfig(KoneUtil.getConfigPath() + "database-config.xml");
		} catch (Throwable e) {
			logger.error("Failure load database-config.xml:", e);
		}
	}

	/**
	 * 装入配置文件
	 * 
	 * @param file
	 */
	public static void loadConfig(String file) {
		try {
			Pattern p = Pattern
					.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/"); // sql注释清除
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList groups = doc.getElementsByTagName("table");
			for (int j = 0; j < groups.getLength(); j++) {
				Element e = (Element) groups.item(j);
				String tableName = e.getAttribute("name");
				Table table = new Table();
				table.cycle = e.getAttribute("cycle");
				if (e.hasAttribute("start"))
					table.start = Integer.valueOf(e.getAttribute("start"));
				if (e.hasAttribute("end"))
					table.end = Integer.valueOf(e.getAttribute("end"));
				if (table.cycle.equals("none"))
					table.start = table.end = 0;
				tables.put(tableName, table);
				NodeList list = e.getElementsByTagName("sql");
				for (int i = 0; i < list.getLength(); i++) {
					Element node = (Element) list.item(i);
					String text = p.matcher(node.getTextContent())
							.replaceAll("$1").trim();
					int index = text.indexOf("#day[");
					if (index > -1) {
						StringBuffer sb = new StringBuffer(text.substring(0,
								index));
						while (index > -1) {
							text = text.substring(index + 5);
							index = text.indexOf("]");
							String line = text.substring(0, index).trim();
							for (int k = 1; k <= 31; k++)
								sb.append(line.replace("#{day}",
										Integer.toString(k)));
							text = text.substring(index + 1);
							index = text.indexOf("#day[");
						}
						sb.append(text);
						text = sb.toString();
					}
					boolean b = false;
					if (node.hasAttribute("isaddpartition"))
						b = Boolean
								.valueOf(node.getAttribute("isaddpartition"));
					table.sqls.add(new SqlString(text, b));
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Resource
	TimerService ts;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void startAutoCreateTimer() {
		Iterator<?> it = ts.getTimers().iterator();
		while (it.hasNext()) {
			Timer timer = (Timer) it.next();
			String info = timer.getInfo().toString();
			if (info.equals("autoCreateTimer")) {
				logger.info("timer exists!");
				createTables();
				return;
			}
		}
		ts.createTimer(new Date(), 24 * 60 * 60 * 1000, "autoCreateTimer");
		logger.info("create timer.");
	}

	/**
	 * 上次更新的日期
	 */
	static long lastUpdateTime = 0;

	@Timeout
	public void timer(Timer timer) {
		createTables();
	}

	private void createTables() {
		Dao dao = new Dao("kss");
		try {
			autoCreateTables(dao);
		} finally {
			dao.close();
		}
	}

	synchronized static void autoCreateTables(Dao dao) {
		if (DateTime.minutesBetween(System.currentTimeMillis(), lastUpdateTime) < 60)
			return;
		lastUpdateTime = System.currentTimeMillis();
		DateTime now = new DateTime();
		Enumeration<String> it = tables.keys();
		while (it.hasMoreElements()) {
			String name = it.nextElement();
			Table table = tables.get(name);
			for (int i = table.start; i <= table.end; i++) {
				String delta = "";
				if (table.cycle.equals("month")
						|| table.cycle.equals("partition_month"))
					delta = now.addMonths(i).format("yyyyMM");
				else if (table.cycle.equals("year")
						|| table.cycle.equals("partition_year"))
					delta = now.addYears(i).format("yyyy");
				else if (table.cycle.equals("day")
						|| table.cycle.equals("partition_day"))
					delta = now.addDays(i).format("yyyyMMdd");
				createTable(dao, name, delta, table);
			}
		}
		deleteNoUseDaySerial(dao);

	}

	static void createTable(Dao dao, String tableName, String delta, Table table) {
		try {
			if (table.cycle.equals("partition_day")) {
				if (!dao.tableExists(tableName)) {
					for (SqlString sql : table.sqls) {
						if (!sql.isAddPartition) {
							String str = sql.sql.replace("${day}", delta);
							dao.execute(str);
						}
					}
				}
				if (!dao.tablePartitionExists(tableName, delta)) {
					for (SqlString sql : table.sqls) {
						if (sql.isAddPartition) {
							String str = sql.sql.replace("${day}", delta);
							dao.execute(str);
						}
					}
				}
			} else if (table.cycle.equals("partition_month")) {
				if (!dao.tableExists(tableName)) {
					for (SqlString sql : table.sqls) {
						if (!sql.isAddPartition) {
							String str = sql.sql.replace("${month}", delta);
							dao.execute(str);
						}
					}
				}
				if (dao.tablePartitionExists(tableName, delta)) {
					for (SqlString sql : table.sqls) {
						if (sql.isAddPartition) {
							String str = sql.sql.replace("${month}", delta);
							dao.execute(str);
						}
					}
				}
			} else if (table.cycle.equals("partition_year")) {
				if (!dao.tableExists(tableName)) {
					for (SqlString sql : table.sqls) {
						if (!sql.isAddPartition) {
							String str = sql.sql.replace("${year}", delta);
							dao.execute(str);
						}
					}
				}
				if (dao.tablePartitionExists(tableName, delta)) {
					for (SqlString sql : table.sqls) {
						if (sql.isAddPartition) {
							String str = sql.sql.replace("${year}", delta);
							dao.execute(str);
						}
					}
				}
			} else if (table.cycle.equals("month")) {
				if (!dao.tableExists(tableName + "_" + delta)) {
					for (SqlString sql : table.sqls) {
						if (!sql.isAddPartition) {
							String str = sql.sql.replace("${month}", delta);
							dao.execute(str);
						}
					}
				}
			} else if (table.cycle.equals("year")) {
				if (!dao.tableExists(tableName + "_" + delta)) {
					for (SqlString sql : table.sqls) {
						if (!sql.isAddPartition) {
							String str = sql.sql.replace("${year}", delta);
							dao.execute(str);
						}
					}
				}
			} else if (table.cycle.equals("none")) {
				if (!dao.tableExists(tableName)) {
					for (SqlString sql : table.sqls) {
						dao.execute(sql.sql);
					}
				}
			}
		} catch (Throwable e) {
			logger.error("create table[" + tableName + "] failure:", e);
		}
	}

	static void deleteNoUseDaySerial(Dao dao) {
		try {
			dao.execute("delete from dayserial c where c.day<?1",
					new Object[] { new DateTime().addYears(-1).getFullDay() });
		} catch (Throwable e) {
		}
	}
}
