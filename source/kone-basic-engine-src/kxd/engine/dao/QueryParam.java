package kxd.engine.dao;

public class QueryParam {
	private int type;
	private Object value;

	/**
	 * 构造器
	 * 
	 * @param type
	 *            参数类型。参见<code>java.sql.Types</code>
	 * @param value
	 *            参数值
	 * @see java.sql.Types
	 */
	public QueryParam(int type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	/**
	 * 获取参数类型
	 * 
	 * @return 参数类型。参见<code>java.sql.Types</code>
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置参数类型
	 * 
	 * @param type
	 *            参数类型 。参见<code>java.sql.Types</code>
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 获取参数值
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * 设置参数值
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}
