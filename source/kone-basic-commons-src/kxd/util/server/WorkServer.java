package kxd.util.server;

/**
 * 服务器基类
 * @author 赵明
 *
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.apache.log4j.Logger;

abstract public class WorkServer extends Thread {
	static Logger logger = Logger.getLogger(WorkServer.class);
	/**
	 * 服务器套接字对象
	 */
	private ServerSocket serverSocket;
	/**
	 * 服务器绑定的端口
	 */
	private int port;
	/**
	 * 服务器绑定的本机IP地址
	 */
	private String bindip;
	/**
	 * 最大的客户端连接数
	 */
	private int maxConnections;
	/**
	 * 服务器描述
	 */
	private String desp;
	/**
	 * 线程池
	 */
	protected WorkThreadPool<Runnable> threadPool;
	/**
	 * 服务器的版本号
	 */
	private String version = "4.0";

	/**
	 * 启动服务器
	 * 
	 * @param logger
	 *            日志记录器
	 * @param desp
	 *            服务器描述
	 * @param port
	 *            绑定端口
	 * @param minThreads
	 *            最小线程数
	 * @param maxConnections
	 *            最大线程数
	 * @param queueSize
	 *            队列大小
	 * @param timeout
	 *            超时，秒为单位
	 */
	public void start(String desp, int port, int minThreads, int maxThreads,
			int queueSize, int timeout) {
		this.desp = desp;
		bindip = "127.0.0.1";
		this.port = port;
		this.maxConnections = queueSize;
		BlockingQueue<Runnable> queue;
		if (queueSize <= 0) {
			queue = new SynchronousQueue<Runnable>();
			maxConnections = minThreads;
		} else
			queue = new ArrayBlockingQueue<Runnable>(maxConnections, true);
		threadPool = new WorkThreadPool<Runnable>(minThreads, maxThreads,
				timeout, queue);
		this.setName(desp);
		start();
	}

	/**
	 * 运行服务器
	 * 
	 * @return true 正常退出，可以继续重启动服务器
	 * @return false 异常退出，不继续重启动服务器
	 */
	private boolean runServer() {
		try {
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			InetSocketAddress addr = new InetSocketAddress(port);
			serverSocket.bind(addr, maxConnections);
			logger.info("[" + desp + "] [" + bindip + ":"
					+ Integer.toString(port) + "] start success！");
			try {
				while (!this.isInterrupted()) {
					Socket socket = serverSocket.accept();
					try {
						accept(socket);
					} catch (Throwable e) {
						if (this.isInterrupted())
							break;
						logger.error("[" + desp + "] " + socket
								+ " closed. Reason: " + e.toString());
						socket.close();
					}
				}
			} catch (Throwable e) {
				logger.error("[" + desp + "] Abort,reason:"
						+ Integer.toString(port) + e.toString());
			}
			serverSocket.close();
			serverSocket = null;
			return true;
		} catch (Throwable e) {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (Throwable ee) {
				}
				serverSocket = null;
			}
			logger.error("[" + desp + "] [" + Integer.toString(port)
					+ "] start failed，reason:" + e.toString());
			return false;
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {
			serverSocket.close();
		} catch (Throwable e) {
		}
	}

	public void run() {
		while (!this.isInterrupted() && runServer())
			;
		threadPool.shutdown();
	}

	/**
	 * 接受一个客户端
	 * 
	 * @param socket
	 *            Socket 客户端套接字对象
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws Exception
	 */
	protected void accept(Socket socket) throws InterruptedException,
			IOException {
		WorkTask task = createTask();
		task.server = this;
		task.socket = socket;
		task.logger = logger;
		logger.debug(socket + " executing ...");
		if (!threadPool.execute(task)) {
			logger.debug(socket + " timeout,closed.");
			socket.close();
		}
	}

	/**
	 * 创建工作任务
	 * 
	 * @param socket
	 *            Socket 客户端套接字对象
	 * @return 为此客户端套接字新创建的工作任务
	 */
	public abstract WorkTask createTask();

	public synchronized String getServerHost() {
		return bindip + ":" + port;
	}

	public synchronized String getVersion() {
		return version;
	}

	public synchronized void setVersion(String version) {
		this.version = version;
	}

	public synchronized String getDesp() {
		return desp;
	}
}
