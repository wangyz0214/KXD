package kxd.engine.dao;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kxd.remote.scs.util.AppException;
import kxd.util.DateTime;

import org.apache.log4j.Logger;

abstract public class DaoDelegation {
	protected Connection connection;
	static final private Logger logger = Logger.getLogger(DaoDelegation.class);

	public DaoDelegation(Connection connection) {
		this.connection = connection;
	}

	protected PreparedStatement prepareStatement(final String sql,
			List<?> params) throws SQLException {
		PreparedStatement st = connection.prepareStatement(sql);
		for (int i = 0; i < params.size(); i++)
			setParameter(sql, st, i, params.get(i));
		return st;
	}

	protected CallableStatement prepareCall(final String sql, List<?> params)
			throws SQLException {
		CallableStatement st = connection.prepareCall(sql);
		for (int i = 0; i < params.size(); i++)
			setParameter(sql, st, i, params.get(i));
		return st;
	}

	protected String getDebugSql(String sql, List<?> params) {
		Object[] a = params.toArray();
		for (int i = a.length - 1; i >= 0; i--) {
			Object o = a[i];
			String v;
			if (o == null)
				v = "''";
			else if (o instanceof Date)
				v = "to_date('"
						+ new DateTime((Date) o).format("yyyy-MM-dd HH:mm:ss")
						+ "','yyyy-mm-dd hh24:mi:ss')";
			else if (o instanceof Calendar)
				v = "to_date('"
						+ new DateTime((Calendar) o)
								.format("yyyy-MM-dd HH:mm:ss")
						+ "','yyyy-mm-dd hh24:mi:ss')";
			else if (o instanceof DateTime)
				v = "to_date('" + ((DateTime) o).format("yyyy-MM-dd HH:mm:ss")
						+ "','yyyy-mm-dd hh24:mi:ss')";
			else
				v = "'" + o.toString() + "'";
			sql = sql.replace("?" + Integer.toString(i + 1), v);
		}
		return sql;
	}

	protected void debug(long startTime, String sql, List<?> params) {
		if (logger.isDebugEnabled()) {
			logger.debug("sql(runmillisecs="
					+ DateTime.milliSecondsBetween(startTime,
							System.currentTimeMillis()) + "): "
					+ getDebugSql(sql, params));
		}
	}

	public List<?> queryPage(String sql, int firstResult, int maxResults,
			Object... params) {
		ArrayList<Object> ls = new ArrayList<Object>();
		if (params != null && params.length == 1
				&& params[0] instanceof List<?>) {
			for (Object o : (List<?>) params[0])
				ls.add(o);
		} else {
			for (Object o : params)
				ls.add(o);
		}
		return queryPage(sql, firstResult, maxResults, ls);
	}

	public List<?> queryPage(String sql, int firstResult, int maxResults,
			List<?> params) {
		long now = System.currentTimeMillis();
		try {
			sql = processDateTime(sql, params);
			if (maxResults > 0) {
				if (firstResult < 0)
					firstResult = 0;
				sql = getPageSql(sql, firstResult, maxResults);
			}
			PreparedStatement st = prepareStatement(sql, params);
			ResultSet rset = null;
			try {
				rset = st.executeQuery();
				return resultSetToList(rset);
			} finally {
				debug(now, sql, params);
				try {
					if (rset != null)
						rset.close();
				} catch (Throwable e) {
				}
				try {
					st.close();
				} catch (Throwable e) {
				}
			}
		} catch (Throwable e) {
			if (logger.isDebugEnabled())
				logger.error("queryPage error:", e);
			throw new AppException(e);
		}
	}

	/**
	 * 转换返回数据对象，本函数默认返回原值
	 * 
	 * @param mt
	 *            结果集信息对象
	 * @param rset
	 *            结果集
	 * @param index
	 *            字段索引，从1开始计
	 * @return 值
	 * @throws SQLException
	 */
	protected Object getResultFieldValue(ResultSetMetaData mt, ResultSet rset,
			int index) throws SQLException {
		int type = mt.getColumnType(index);
		if (type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP)
			return rset.getTimestamp(index);
		else
			return rset.getObject(index);
	}

	protected Object getParamFieldValue(CallableOutParameter p,
			CallableStatement st, int index) throws SQLException {
		int type = p.getType();
		if (type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP)
			return st.getTimestamp(index);
		else {
			Object r = st.getObject(index);
			if (r instanceof ResultSet) {
				List<?> ls = resultSetToList((ResultSet) r);
				((ResultSet) r).close();
				return ls;
			} else
				return r;
		}
	}

	/**
	 * 记录集转换成对象列表
	 * 
	 * @param rset
	 *            记录集
	 * @return 列表
	 * @throws SQLException
	 */
	protected List<?> resultSetToList(ResultSet rset) throws SQLException {
		List<Object> ls = new ArrayList<Object>();
		ResultSetMetaData mt = rset.getMetaData();
		if (mt.getColumnCount() == 1) {
			while (rset.next()) {
				ls.add(getResultFieldValue(mt, rset, 1));
			}
		} else
			while (rset.next()) {
				Object[] o = new Object[mt.getColumnCount()];
				for (int i = 0; i < o.length; i++)
					o[i] = getResultFieldValue(mt, rset, i + 1);
				ls.add(o);
			}
		return ls;
	}

	abstract public String getPageSql(String sql, int firstResult,
			int maxResults);

	abstract public <E> String getAutoGenerateIdSql(E o,
			SqlConverter<E> converter);

	abstract protected String processDateTime(String sql, List<?> params);

	public void setParameter(String sql, PreparedStatement st, int index,
			Object o) throws SQLException {
		if (!sql.contains("?" + (index + 1)))
			return;
		if (o == null) {
			st.setString(index + 1, "");
		} else if (o instanceof String) {
			st.setString(index + 1, (String) o);
		} else if (o instanceof Integer) {
			st.setInt(index + 1, (Integer) o);
		} else if (o instanceof Byte) {
			st.setByte(index + 1, (Byte) o);
		} else if (o instanceof Short) {
			st.setShort(index + 1, (Short) o);
		} else if (o instanceof Integer) {
			st.setInt(index + 1, (Integer) o);
		} else if (o instanceof Long) {
			st.setLong(index + 1, (Long) o);
		} else if (o instanceof Float) {
			st.setFloat(index + 1, (Float) o);
		} else if (o instanceof Double) {
			st.setDouble(index + 1, (Double) o);
		} else if (o instanceof Boolean) {
			st.setBoolean(index + 1, (Boolean) o);
		} else if (o instanceof Calendar) {
			st.setTimestamp(index + 1,
					new Timestamp(((Calendar) o).getTimeInMillis()));
		} else if (o instanceof Date) {
			st.setTimestamp(index + 1, new Timestamp(((Date) o).getTime()));
		} else if (o instanceof DateTime) {
			st.setTimestamp(index + 1,
					new Timestamp(((DateTime) o).getTimeInMillis()));
		} else if (o instanceof QueryParam) {
			QueryParam p = (QueryParam) o;
			st.setObject(index + 1, p.getValue(), p.getType());
		} else if (o instanceof StringBuffer) {
			st.setString(index + 1, ((StringBuffer) o).toString());
		} else if (o instanceof byte[]) {
			st.setBytes(index + 1, (byte[]) o);
		} else if (o instanceof BigDecimal) {
			st.setBigDecimal(index + 1, (BigDecimal) o);
		} else if (o instanceof Blob) {
			st.setBlob(index + 1, (Blob) o);
		} else if (o instanceof Clob) {
			st.setClob(index + 1, (Clob) o);
		} else if (o instanceof Array) {
			st.setArray(index + 1, (Array) o);
		} else if (o instanceof CallableOutParameter) {
			CallableOutParameter v = (CallableOutParameter) o;
			if (v.getTypeName() != null)
				((CallableStatement) st).registerOutParameter(index + 1,
						v.getType(), v.getTypeName());
			else if (v.getScale() != null)
				((CallableStatement) st).registerOutParameter(index + 1,
						v.getType(), v.getScale());
			else
				((CallableStatement) st).registerOutParameter(index + 1,
						v.getType());
		} else
			st.setString(index + 1, o + "");
	}

	/**
	 * 检查表名是否存在
	 * 
	 * @param tableName
	 *            表名
	 * @return true/false
	 */
	abstract public boolean tableExists(String tableName);

	/**
	 * 检查表的分区是否存在
	 * 
	 * @param tableName
	 *            表名
	 * @param partitionName
	 *            分区名
	 * @return true/false
	 */
	abstract public boolean tablePartitionExists(String tableName,
			String partitionName);

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
	abstract public <E> void autoGenerateId(E o, SqlConverter<E> converter);

	/**
	 * 更新或插入数据
	 * 
	 * @param sql
	 *            查询sql
	 * @param params
	 *            参数列表，如果参数为QueryParam，则按指定的数据类型设置参数
	 * @return 返回受影响的记录条数
	 */
	public int execute(String sql, List<?> params) {
		long now = System.currentTimeMillis();
		try {
			PreparedStatement st = prepareStatement(sql, params);
			try {
				return st.executeUpdate();
			} finally {
				if (logger.isDebugEnabled()) {
					debug(now, sql, params);
					now = 0;
				}
				st.close();
			}
		} catch (Throwable e) {
			if (logger.isDebugEnabled()) {
				if (now > 0)
					debug(now, sql, params);
				logger.error("execute error:", e);
			}
			throw new AppException(e);
		}
	}

	public int execute(String sql, Object... params) {
		ArrayList<Object> ls = new ArrayList<Object>();
		for (Object o : params)
			ls.add(o);
		return execute(sql, ls);
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
	abstract public void execProcedure(String sql, List<?> params);

	public void execProcedure(String sql, Object... params) {
		ArrayList<Object> ls = new ArrayList<Object>();
		for (Object o : params)
			ls.add(o);
		execProcedure(sql, ls);
	}
}
