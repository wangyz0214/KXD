package kxd.engine.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kxd.remote.scs.util.AppException;
import kxd.util.DateTime;
import kxd.util.Idable;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.apache.log4j.Logger;

public class OracleDelegation extends DaoDelegation {
	static final private Logger logger = Logger
			.getLogger(OracleDelegation.class);

	public OracleDelegation(Connection connection) {
		super(connection);
	}

	@Override
	public void setParameter(String sql, PreparedStatement st, int index,
			Object o) throws SQLException {
		if (o instanceof ArrayParameter) {
			ArrayParameter p = (ArrayParameter) o;
			Connection con = connection.getMetaData().getConnection();
			ArrayDescriptor ad = new ArrayDescriptor(p.getTypeName(), con);
			StructDescriptor sd = new StructDescriptor(p.getItemTypeName(), con);
			STRUCT[] s = new STRUCT[p.getValues().length];
			for (int i = 0; i < s.length; i++) {
				Object[] values = (Object[]) p.values[i];
				s[i] = new oracle.sql.STRUCT(sd, con, values);
			}
			st.setArray(index + 1, new ARRAY(ad, con, s));
		} else
			super.setParameter(sql, st, index, o);
	}

	@Override
	protected PreparedStatement prepareStatement(final String sql,
			List<?> params) throws SQLException {
		String osql = sql;
		for (int i = params.size(); i >= 1; i--) {
			osql = osql.replace("?" + Integer.toString(i), "?");
		}
		PreparedStatement st = connection.prepareStatement(osql);
		for (int i = 0; i < params.size(); i++)
			setParameter(sql, st, i, params.get(i));
		return st;
	}

	@Override
	protected CallableStatement prepareCall(final String sql, List<?> params)
			throws SQLException {
		String osql = sql;
		for (int i = params.size(); i >= 1; i--) {
			osql = osql.replace("?" + Integer.toString(i), "?");
		}
		CallableStatement st = connection.prepareCall("{ call " + osql + " }");
		for (int i = 0; i < params.size(); i++)
			setParameter(sql, st, i, params.get(i));
		return st;
	}

	@Override
	protected String processDateTime(String sql, List<?> params) {
		for (int i = params.size() - 1; i >= 0; i--) {
			Object o = params.get(i);
			if (o != null) {
				DateTime d = null;
				if (o instanceof Date) {
					d = new DateTime((Date) o);
				} else if (o instanceof Calendar)
					d = new DateTime((Calendar) o);
				else if (o instanceof DateTime)
					d = (DateTime) o;
				if (d != null) {
					sql = sql.replace("?" + (i + 1),
							"to_date('" + d.format("yyyy-MM-dd HH:mm:ss")
									+ "','yyyy-mm-dd hh24:mi:ss')");
				}
			}
		}
		return sql;
	}

	@Override
	public String getPageSql(String sql, int firstResult, int maxResults) {
		return "select * from ( select a.*, rownum rn from (" + sql
				+ ") a where rownum <= " + (firstResult + maxResults)
				+ ") where rn > " + firstResult;
	}

	@Override
	public <E> String getAutoGenerateIdSql(E o, SqlConverter<E> converter) {
		return "select " + converter.getSequenceString(o)
				+ ".nextval from dual";
	}

	@Override
	public boolean tableExists(String tableName) {
		return Integer.valueOf(queryPage(
				"select count(*) from user_tables where table_name=?1", 0, 0,
				tableName.toUpperCase()).iterator().next().toString()) > 0;
	}

	/**
	 * 检查表的分区是否存在
	 * 
	 * @param tableName
	 *            表名
	 * @param partitionName
	 *            分区名
	 * @return true/false
	 */
	public boolean tablePartitionExists(String tableName, String partitionName) {
		return Integer
				.valueOf(queryPage(
						"select count(*) from user_tab_partitions where partition_name=?1 and table_name=?2",
						0, 0, (tableName + "_" + partitionName).toUpperCase(),tableName.toUpperCase())
						.iterator().next().toString()) > 0;
	}

	/**
	 * 自动生成ID
	 * 
	 * @param <E>
	 *            要插入的对象类
	 * @param connection
	 *            数据库连接
	 * @param o
	 *            要插入的对象
	 * @param converter
	 *            转换器
	 * @throws SQLException
	 */
	public <E> void autoGenerateId(E o, SqlConverter<E> converter) {
		if (o instanceof Idable<?>) {
			if (((Idable<?>) o).getId() == null) {
				((Idable<?>) o)
						.setIdString(queryPage(
								"select " + converter.getSequenceString(o)
										+ ".nextval from dual", 0, 0).get(0)
								.toString());
			}
		}
	}

	/**
	 * 调用存储过程
	 * 
	 * @param sql
	 *            存储过程调用SQL
	 * @param params
	 *            参数
	 * @return 输出参数列表
	 */
	public void execProcedure(String sql, List<?> params) {
		long now = System.currentTimeMillis();
		try {
			CallableStatement st = prepareCall(sql, params);
			try {
				st.execute();
				int i = 0;
				for (Object o : params) {
					i++;
					if (o instanceof CallableOutParameter) {
						CallableOutParameter v = (CallableOutParameter) o;
						v.setValue(getParamFieldValue(v, st, i));
					}
				}
			} finally {
				st.close();
				if (logger.isDebugEnabled()) {
					debug(now, sql, params);
					now = 0;
				}
			}
		} catch (Throwable e) {
			if (logger.isDebugEnabled()) {
				if (now > 0)
					debug(now, sql, params);
				logger.error("execProcedure error:", e);
			}

			String msg = e.getMessage();
			int index = msg.indexOf("ORA-20001:");
			if (index >= 0) {
				msg = msg.substring(index + 10);
				index = msg.indexOf("\n");
				if (index >= 0)
					msg = msg.substring(0, index);
				msg = msg.trim();
			}
			throw new AppException(msg, e);
		}
	}

}
