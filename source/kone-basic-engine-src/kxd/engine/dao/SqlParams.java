package kxd.engine.dao;

import java.util.ArrayList;

public class SqlParams {
	String sql;
	ArrayList<Object> params = new ArrayList<Object>();

	public SqlParams(String sql) {
		super();
		this.sql = sql;
	}

	public SqlParams(String sql, Object... params) {
		super();
		this.sql = sql;
		if (params != null)
			for (Object o : params)
				this.params.add(o);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public ArrayList<Object> getParams() {
		return params;
	}

}
