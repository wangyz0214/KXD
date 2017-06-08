package kxd.net.connection.jndi;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import kxd.net.connection.ConnectionCreator;
import kxd.net.connection.ConnectionPool;
import kxd.net.connection.PooledConnection;
import kxd.net.naming.Lookuper;
import kxd.net.naming.AliveTestBeanRemote;

public class PooledJndiConnection extends JndiConnection implements
		PooledConnection<NamingException> {
	ConnectionPool<? extends PooledConnection<NamingException>, NamingException> connectionPool;
	private volatile long lastAliveTime = System.currentTimeMillis();
	String checkAliveBeanName;
	AliveTestBeanRemote aliveBean;
	static Logger logger = Logger.getLogger(PooledJndiConnection.class);

	public PooledJndiConnection() {
		super();
	}

	@Override
	public void open() throws NamingException {
		try {
			// logger.info(connectionPool + " - open.");
			super.open();
			aliveBean = null;
			if (checkAliveBeanName != null && !checkAliveBeanName.isEmpty()) {
				aliveBean = lookup(Lookuper.JNDI_TYPE_EJB, checkAliveBeanName,
						AliveTestBeanRemote.class);
				if (!aliveBean.isOk(""))
					throw new NamingException("连接检查失败.");
			}
			setLastAliveTime(System.currentTimeMillis());
			getConnectionPool().setAvailable(true);
			getConnectionPool().setLastAliveTime(lastAliveTime);
		} catch (NamingException e) {
			closeContext();
			getConnectionPool().connectionFailure(this, e);
			throw e;
		}
	}

	@Override
	public void close() throws NamingException {
		connectionPool.returnConnection(this);
	}

	@Override
	public void connectionCreated(
			ConnectionCreator<? extends PooledConnection<NamingException>, NamingException> creator) {
		connectionPool = creator.getConnectionPool();
		JndiConnectionCreator<? extends PooledConnection<NamingException>> c = (JndiConnectionCreator<? extends PooledConnection<NamingException>>) creator;
		url = c.url;
		this.checkAliveBeanName = c.getCheckAliveBeanName();
		properties = c.properties;
		lookuper = c.getLookuper();
	}

	@Override
	public ConnectionPool<? extends PooledConnection<NamingException>, NamingException> getConnectionPool() {
		return connectionPool;
	}

	@Override
	public void keepAlive() throws NamingException {
		if (!isConnected())
			open();
		if (aliveBean != null) {
			try {
				// logger.info(connectionPool + " - call alive test.");
				if (!aliveBean.isOk("")) {
					dispose();
				}
			} catch (Throwable e) {
				try {
					dispose();
				} catch (Throwable ex) {
				}
			}
		}
	}

	@Override
	public long getLastAliveTime() {
		return lastAliveTime;
	}

	@Override
	public void setLastAliveTime(long time) {
		this.lastAliveTime = time;
	}

	@Override
	public void dispose() throws NamingException {
		closeContext();
	}
}
