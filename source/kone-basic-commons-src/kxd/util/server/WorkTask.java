package kxd.util.server;

/**
 * 工作任务
 * @author Administrator
 *
 */

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import kxd.util.stream.SocketStream;

import org.apache.log4j.Logger;

abstract public class WorkTask implements Runnable {
	final static public int WAITING = 0;
	final static public int PREARING = 1;
	final static public int RUNING = 2;
	final static public int TERMINATING = 3;
	final static public int TERMINATED = 4;
	protected WorkServer server;
	protected Socket socket;
	protected volatile long createTime = System.currentTimeMillis();
	protected AtomicInteger runStatus = new AtomicInteger(WAITING);

	protected Logger logger;

	/**
	 * 当前的运行状态
	 */
	public WorkTask() {

	}

	public WorkTask(WorkServer server, Socket socket, Logger logger) {
		this.server = server;
		this.socket = socket;
		this.logger = logger;
	}

	public void run() {
		if (runStatus.get() >= TERMINATING) // 如果已经被外部终止，则直接返回
			return;
		SocketStream stream = null;
		Throwable ex = null;
		try {
			stream = new SocketStream(socket);
			runStatus.set(PREARING);
			executeBefore(stream);
			runStatus.set(RUNING);
			execute(stream);
			socket.getOutputStream().flush();
		} catch (Throwable e) {
			ex = e;
		} finally {
			executeAfter(stream, ex);
			try {
				logger.debug(socket + " closed.");
				socket.close();
			} catch (Throwable e) {
			}
			this.runStatus.set(TERMINATED);
		}
	}

	/**
	 * 运行在 execute() 函数之前，用于初始化套接字流，在该类中设置了套接字关闭时立即终止收
	 * 发数据，并设置了读数据的超时时间。可以重载此函数来定制需要配置的套接字
	 * 
	 * @param stream
	 *            套接字流
	 * @throws Exception
	 *             操作失败时抛出
	 */
	public void executeBefore(SocketStream stream) throws IOException,
			InterruptedException {
		Socket socket = stream.getSocket();
		socket.setSoLinger(true, 0);
	}

	/**
	 * 运行 execute() 函数之后，不论 execute() 函数有没有抛出异常，该函数都将被执行。
	 * 可重载该函数来完成具体的动作。本类中此函数的功能是等待30秒，如果客户端套接字还未 关闭，则强行关闭.
	 * 
	 * @param stream
	 *            套接字流
	 * @param ex
	 *            如果为null，则表示未抛出异常
	 */
	public void executeAfter(SocketStream stream, Throwable ex) {
		if (ex == null && stream != null && runStatus.get() < TERMINATING) {
			try {
				socket.setSoTimeout(30000);
				stream.read(1, 30000);
			} catch (Throwable e) {
			}
		}
	}

	/**
	 * 执行任务的主体函数。必须重载该函数来实现任务的功能。
	 * 
	 * @param stream
	 *            套接字流
	 * @throws Exception
	 *             如果执行失败，抛出此异常
	 */
	abstract public void execute(SocketStream stream) throws IOException,
			InterruptedException;

	/**
	 * 获得任务创建的时间
	 * 
	 * @return
	 */
	public final long getCreateTime() {
		return createTime;
	}

	/**
	 * 获得远程服务器的IP
	 * 
	 * @return
	 */
	synchronized public String getRemoteAddress() {
		return socket.getInetAddress().getHostAddress();
	}

	/**
	 * 终止任务的运行
	 * 
	 */
	synchronized public final void terminate() {
		int status = this.runStatus.get();
		if (status <= RUNING) {
			this.runStatus.set(TERMINATING);
			try {
				socket.close();
			} catch (Throwable e) {
			}
		}
	}

	/**
	 * 获得任务的运行状态
	 * 
	 * @return
	 */
	public final int getRunStatus() {
		return runStatus.get();
	}

	/**
	 * 检查任务是否已经终止，如果已经终止，则抛出异常
	 * 
	 * @throws TradeError
	 */
	public final void checkTerminated() throws Exception {
		if (runStatus.get() >= TERMINATING)
			throw new Exception("任务已经终止");
	}

	public synchronized WorkServer getServer() {
		return server;
	}
}
