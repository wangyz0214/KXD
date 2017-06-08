package kxd.net.naming;

import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import kxd.net.connection.ConnectionPool;
import kxd.net.connection.ConnectionPoolListMap;
import kxd.net.connection.LoopConnectionPoolList;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.connection.jndi.PooledJndiConnection;

public class JndiConnecitonPoolListGroup {
	Lookuper lookuper;
	ConnectionPool<PooledJndiConnection, NamingException> localPools;
	// ConcurrentHashMap<String, HashConnectionPoolList<PooledJndiConnection,
	// NamingException>> hashMap = new ConcurrentHashMap<String,
	// HashConnectionPoolList<PooledJndiConnection, NamingException>>();
	ConnectionPoolListMap<LoopConnectionPoolList<PooledJndiConnection, NamingException>> loopMap = new ConnectionPoolListMap<LoopConnectionPoolList<PooledJndiConnection, NamingException>>(
			5);
	ConcurrentHashMap<Object, Object> properties = new ConcurrentHashMap<Object, Object>();
}
