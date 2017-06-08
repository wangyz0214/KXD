/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.engine.dao;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.naming.NamingException;

import kxd.net.naming.LoopNamingContext;
import kxd.util.DateTime;

import org.apache.log4j.Logger;

/**
 * 
 * @author Administrator
 */
public class BaseBean {
	final static Logger logger = Logger.getLogger(BaseBean.class);
	/**
	 * 最大返回记录数
	 */
	static public int MAX_RESULTS = 2000;
	private Dao dao;

	public BaseBean() {
		super();

	}

	public Dao getDao() {
		if (dao == null)
			dao = new Dao("kss");
		return dao;
	}

	static public LoopNamingContext getLoopNamingContext(String name)
			throws NamingException {
		return new LoopNamingContext(name);
	}

	protected int getMaxResults() {
		return MAX_RESULTS;
	}

	static protected boolean tableExists(Dao dao, String tablename) {
		return dao.tableExists(tablename);
	}

	/**
	 * 拦截器，处理数据库的事务
	 * 
	 * @param ic
	 * @return
	 * @throws Exception
	 */
	@AroundInvoke
	public Object daoInterceptor(InvocationContext ic) throws Exception {
		long startTime = System.currentTimeMillis();
		try {
			if (dao != null) {
				dao.close();
				dao = null;
			}
			return ic.proceed();
		} finally {
			if (logger.isDebugEnabled())
				logger.debug(ic.getMethod().getName()
						+ " called. [runinterval="
						+ DateTime.milliSecondsBetween(startTime,
								System.currentTimeMillis()) + "]");
			if (dao != null) {
				dao.close();
				dao = null;
			}
		}
	}
}
