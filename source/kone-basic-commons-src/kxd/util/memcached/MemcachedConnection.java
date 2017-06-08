package kxd.util.memcached;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kxd.net.connection.tcp.PooledTcpConnection;
import kxd.util.KeyValue;
import kxd.util.Value;

import org.apache.log4j.Logger;

/**
 * 被套接字连接池管理的套接字
 * 
 * @author 赵明
 * @version 1.0
 */
public class MemcachedConnection extends PooledTcpConnection {
	static Logger logger = Logger.getLogger(MemcachedConnection.class);
	public static final int MAX_LENGTH = 1024 * 1024;
	// return codes
	static final String VALUE = "VALUE"; // start of value line from
	// server
	static final String STATS = "STAT"; // start of stats line from
	// server
	static final String ITEM = "ITEM"; // start of item line from server
	static final String DELETED = "DELETED"; // successful deletion
	static final String NOTFOUND = "NOT_FOUND"; // record not found for
	// delete or incr/decr
	static final String STORED = "STORED"; // successful store of data
	static final String NOTSTORED = "NOT_STORED"; // data not stored
	static final String OK = "OK"; // success
	static final String END = "END"; // end of data from server

	static final String ERROR = "ERROR"; // invalid command name from
	// client
	static final String CLIENT_ERROR = "CLIENT_ERROR"; // client error
	// in input line
	// - invalid
	// protocol
	static final String SERVER_ERROR = "SERVER_ERROR"; // server error

	static final byte[] B_END = "END\r\n".getBytes();
	static final byte[] B_NOTFOUND = "NOT_FOUND\r\n".getBytes();
	static final byte[] B_DELETED = "DELETED\r\r".getBytes();
	static final byte[] B_STORED = "STORED\r\r".getBytes();
	static final byte[] CR = "\r\n".getBytes();

	/**
	 * 构建一个被连接池管理的套接字对象
	 * 
	 */
	public MemcachedConnection() {
	}

	/**
	 * 保持连接
	 */
	synchronized public void keepAlive() {
		if (!isConnected())
			return;
		// 空闲5分钟时，断开连接
		if (System.currentTimeMillis() - getLastAliveTime() < 600000)
			return;
		updateLastAliveTime();
		try {
			dispose();
		} catch (Throwable e) {
		}
	}

	private void updateLastAliveTime() {
		setLastAliveTime(System.currentTimeMillis());
	}

	/**
	 * 连接到主机
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	@Override
	public void open() throws IOException {
		super.open();
		updateLastAliveTime();
	}

	/**
	 * 关闭套接字连接
	 */
	synchronized public void closeSocket() {
		super.closeSocket();
	}

	/**
	 * 存储键值
	 * 
	 * @param cmdname
	 *            存储命令(add/replace/set)
	 * @param key
	 *            键名
	 * @param kv
	 *            键值对象
	 * @return 是否存储成功
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	synchronized public boolean set(String cmdname, String key, int flags,
			byte[] value, int offset, int length, long expiry)
			throws IOException {
		if (value.length > MAX_LENGTH)
			throw new IOException("设置键值对失败，超过最大长度限制");
		String cmd = String.format("%s %s %d %d %d\r\n", cmdname, key, flags,
				(expiry / 1000), value.length);
		String response = "";
		for (int i = 0; i < 2; i++) {
			open();
			try {
				Socket socket = stream.getSocket();
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().write(value, offset, length);
				socket.getOutputStream().write(CR);
				socket.getOutputStream().flush();
				MemCachedInputStream br = new MemCachedInputStream(
						socket.getInputStream());
				response = br.readLine();
				break;
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				closeSocket();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (STORED.equals(response)) {
			// logger.info("set[key=" + key + " len=" + value.length + "]");
			return true;
		} else if (NOTSTORED.equals(response))
			return false;
		else
			throw new MemCachedException(response);
	}

	/**
	 * 增加/减少键值
	 * 
	 * @param cmdname
	 *            命令名(incr,decr)
	 * @param key
	 *            键名
	 * @param inc
	 *            增/减步长
	 * @param value
	 *            返回值
	 * @return true 设置成功
	 * @return false 设置失败，原值不存在
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	synchronized public boolean incrdecr(String cmdname, String key, long inc,
			Value<Long> value) throws IOException {
		String cmd = String.format("%s %s %d\r\n", cmdname, key, inc);
		String response = "";
		for (int i = 0; i < 2; i++) {
			open();
			try {
				Socket socket = stream.getSocket();
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().flush();
				MemCachedInputStream br = new MemCachedInputStream(
						socket.getInputStream());
				response = br.readLine();
				break;
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				closeSocket();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (NOTFOUND.equals(response))
			return false;
		else if (response.matches("\\d+")) {
			value.setValue(Long.parseLong(response));
			return true;
		} else
			throw new MemCachedException(response);
	}

	/**
	 * 获取键值
	 * 
	 * @param key
	 *            键名
	 * @param value
	 *            返回的键值
	 * @return 是否获取成功
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	synchronized public boolean get(String key, MemCachedValue value)
			throws IOException {
		logger.debug("get: " + key);
		String response = "";
		boolean error = false;
		byte[] data = null;
		int flags = 0;
		for (int i = 0; i < 2; i++) {
			open();
			try {
				Socket socket = stream.getSocket();
				socket.getOutputStream().write(
						("get " + key + "\r\n").getBytes());
				socket.getOutputStream().flush();
				MemCachedInputStream br = new MemCachedInputStream(
						socket.getInputStream());
				while (true) {
					response = br.readLine();
					if (END.equals(response)) {
						break;
					} else if (response.startsWith(VALUE)) {
						String[] info = response.split(" ");
						flags = Integer.parseInt(info[2]);
						int length = Integer.parseInt(info[3]);
						data = new byte[length];
						br.read(data);
						br.skipLine();
					} else if (ERROR.equals(response)
							|| response.startsWith(CLIENT_ERROR)
							|| response.startsWith(SERVER_ERROR)) {
						error = true;
						break;
					}
				}
				break;
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				closeSocket();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect: ", e);
			}
		}
		if (error)
			throw new MemCachedException("获取键值失败：" + response);
		else if (data != null) {
			value.setFlags(flags);
			value.setValue(data);
			return true;
		} else
			return false;
	}

	/**
	 * 获取一个或多个键值
	 * 
	 * @param keys
	 *            键值数组
	 * @param map
	 *            返回的键值map
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	synchronized public void get(Collection<String> keys,
			Map<String, MemCachedValue> map) throws IOException {
		StringBuffer cmd = new StringBuffer("get");
		for (String key : keys)
			cmd.append(" " + key);
		cmd.append("\r\n");
		boolean error = false;
		String response = "";
		for (int i = 0; i < 2; i++) {
			byte[] data = null;
			map.clear();
			open();
			try {
				Socket socket = stream.getSocket();
				socket.getOutputStream().write(cmd.toString().getBytes());
				socket.getOutputStream().flush();
				MemCachedInputStream br = new MemCachedInputStream(
						socket.getInputStream());
				while (true) {
					response = br.readLine();
					if (END.equals(response)) {
						break;
					} else if (response.startsWith(VALUE)) {
						String[] info = response.split(" ");
						int flags = Integer.parseInt(info[2]);
						int length = Integer.parseInt(info[3]);
						data = new byte[length];
						br.read(data);
						br.skipLine();
						map.put(info[1], new MemCachedValue(data, flags));
					} else if (ERROR.equals(response)
							|| response.startsWith(CLIENT_ERROR)
							|| response.startsWith(SERVER_ERROR)) {
						error = true;
						break;
					}
				}
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				closeSocket();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (error)
			throw new MemCachedException("获取键值失败：" + response);
	}

	/**
	 * 删除键值
	 * 
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	synchronized public boolean flushAll() throws IOException {
		open();
		String cmd = String.format("flush_all\r\n");
		String response = "";
		Socket socket = stream.getSocket();
		try {
			socket.getOutputStream().write(cmd.getBytes());
			socket.getOutputStream().flush();
			MemCachedInputStream br = new MemCachedInputStream(
					socket.getInputStream());
			response = br.readLine();
		} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
			closeSocket();
			throw e;
		}
		return OK.equals(response);
	}

	/**
	 * 删除
	 * 
	 * @param key
	 *            要删除的键名
	 * @param expiry
	 *            延时的时间，为0则立即删除
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	synchronized public boolean delete(String key, long expiry)
			throws IOException {
		String cmd = String.format("delete %s %d\r\n", key, expiry / 1000);
		String response = "";
		for (int i = 0; i < 2; i++) {
			open();
			try {
				Socket socket = stream.getSocket();
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().flush();
				MemCachedInputStream br = new MemCachedInputStream(
						socket.getInputStream());
				response = br.readLine();
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				closeSocket();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (DELETED.equals(response))
			return true;
		else if (NOTFOUND.equals(response))
			return false;
		else
			throw new MemCachedException("删除数据失败: " + response);
	}

	/**
	 * 获取版本
	 * 
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	synchronized public String version() throws IOException {
		String response = "";
		for (int i = 0; i < 2; i++) {
			open();
			try {
				Socket socket = stream.getSocket();
				socket.getOutputStream().write("version\r\n".getBytes());
				socket.getOutputStream().flush();
				MemCachedInputStream br = new MemCachedInputStream(
						socket.getInputStream());
				response = br.readLine();
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				closeSocket();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (ERROR.equals(response) || response.startsWith(CLIENT_ERROR)
				|| response.startsWith(SERVER_ERROR))
			throw new MemCachedException("获取版本失败：" + response);
		return response;
	}

	/**
	 * 取服务器状态
	 * 
	 * @param item
	 *            状态项。为null，则取全部
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	synchronized public String status(String item) throws IOException {
		String response = "";
		StringBuffer buf = new StringBuffer();
		boolean error = false;
		for (int i = 0; i < 2; i++) {
			open();
			try {
				Socket socket = stream.getSocket();
				if (item == null || item.isEmpty())
					socket.getOutputStream().write("stats\r\n".getBytes());
				else
					socket.getOutputStream().write(
							("stats " + item + "\r\n").getBytes());
				socket.getOutputStream().flush();
				MemCachedInputStream br = new MemCachedInputStream(
						socket.getInputStream());
				while (true) {
					response = br.readLine();
					if (END.equals(response))
						break;
					else if (ERROR.equals(response)
							|| response.startsWith(CLIENT_ERROR)
							|| response.startsWith(SERVER_ERROR)) {
						error = true;
						break;
					} else
						buf.append(response + "\r\n");
				}
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				closeSocket();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (error)
			throw new MemCachedException("取状态失败：" + response);
		return buf.toString();
	}

	class MemCachedInputStream extends BufferedInputStream {
		// InputStream in;

		public MemCachedInputStream(InputStream arg0) {
			super(arg0);
			// in = arg0;
		}

		@Override
		public synchronized int read(byte[] arg0, int arg1, int arg2)
				throws IOException {
			int ret = super.read(arg0, arg1, arg2);
			if (ret == -1)
				throw new IOException("连接已经断开");
			return ret;
		}

		/**
		 * 确保读入指定长度的数据，填充至缓冲区
		 */
		public int read(byte[] b) throws IOException {
			int leftLen = b.length;
			while (leftLen > 0) {
				int len = super.read(b, b.length - leftLen, leftLen);
				if (len > 0) {
					leftLen -= len;
					if (leftLen <= 0)
						break;
				} else
					throw new IOException("已达流末尾");
			}
			return b.length;
		}

		/**
		 * 读取一行数据
		 * 
		 * @return 读取到的数据
		 * @throws IOException
		 */
		public String readLine() throws IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1];
			byte r = 0, n = 0;
			while (read(b, 0, 1) != -1) {
				r = n;
				n = b[0];
				if (r == '\r' && n == '\n')
					break;
				bos.write(b);
			}
			return bos.toString().trim();
		}

		/**
		 * 跳过一行数据
		 * 
		 * @return 读取到的数据
		 * @throws IOException
		 */
		public void skipLine() throws IOException {
			byte[] b = new byte[1];
			byte r = 0, n = 0;
			while (read(b, 0, 1) != -1) {
				r = n;
				n = b[0];
				if (r == '\r' && n == '\n')
					return;
			}
		}
	}

	/**
	 * 设置一个键值对
	 * 
	 * @param cmdname
	 * @param key
	 * @param value
	 * @param expiry
	 * @throws IOException
	 */
	public void set(MemCachedValueConverter converter, boolean compress,
			String cmdname, String key, Object value, Date expiry)
			throws IOException {
		MemCachedValue v = converter.objectToBytes(value, compress);
		byte[] buf = v.getValue();
		int flags = v.getFlags();
		if (!set(cmdname, key, flags, buf, 0, buf.length, expiry == null ? 0
				: expiry.getTime()))
			throw new MemCachedException("设置键[" + key + "]失败");
	}

	/**
	 * 设置多个键值对
	 * 
	 * @param cmdname
	 * @param map
	 * @param expiry
	 * @throws IOException
	 */
	public void set(MemCachedValueConverter converter, boolean compress,
			String cmdname, Map<String, Object> map, Date expiry)
			throws IOException {
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			set(converter, compress, cmdname, key, map.get(key), expiry);
		}
	}

	/**
	 * 设置多个键值对
	 * 
	 * @param cmdname
	 * @param map
	 * @param expiry
	 * @throws IOException
	 */
	public void set(MemCachedValueConverter converter, boolean compress,
			String cmdname, List<KeyValue<String, Object>> keyValueList,
			Date expiry) throws IOException {
		for (KeyValue<String, Object> o : keyValueList) {
			set(converter, compress, cmdname, o.getKey(), o.getValue(), expiry);
		}
	}

	/**
	 * 获取一个对象
	 * 
	 * @param key
	 * @param converter
	 * @return
	 * @throws IOException
	 */
	protected Object get(MemCachedValueConverter converter, String key)
			throws IOException {
		MemCachedValue value = new MemCachedValue();
		if (get(key, value)) {
			return converter.bytesToObject(value);
		} else
			return null;
	}

	/**
	 * 获取多个键值对
	 * 
	 * @param keys
	 * @param map
	 * @throws IOException
	 */
	public void get(MemCachedValueConverter converter, Collection<String> keys,
			Map<String, Object> map) throws IOException {
		HashMap<String, MemCachedValue> valueMap = new HashMap<String, MemCachedValue>();
		get(keys, valueMap);
		Iterator<String> itx = valueMap.keySet().iterator();
		while (itx.hasNext()) {
			String key = itx.next();
			MemCachedValue v = valueMap.get(key);
			map.put(key, converter.bytesToObject(v));
		}
	}

	/**
	 * 删除多个键
	 * 
	 * @param keys
	 * @param expiry
	 * @return 是否全部删除成功
	 * @throws IOException
	 */
	protected boolean delete(Collection<Object> keys, Date expiry)
			throws IOException {
		for (Object o : keys)
			if (!delete(o.toString(), expiry == null ? 0 : expiry.getTime()))
				return false;
		return true;
	}

	/**
	 * 删除多个键
	 * 
	 * @param keys
	 * @param expiry
	 * @return 是否全部删除成功
	 * @throws IOException
	 */
	public boolean delete(Object[] keys, Date expiry) throws IOException {
		for (Object o : keys)
			if (!delete(o.toString(), expiry == null ? 0 : expiry.getTime()))
				return false;
		return true;
	}
}
