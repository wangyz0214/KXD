package kxd.util.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class WorkThreadPool<E extends Runnable> {
	CopyOnWriteArrayList<Thread> threadList = new CopyOnWriteArrayList<Thread>();
	volatile int minThreads, maxThreads;
	AtomicInteger busyThreads = new AtomicInteger();
	BlockingQueue<E> queue;
	volatile int waitTimeout;
	Logger logger = Logger.getLogger(WorkThreadPool.class);

	public WorkThreadPool(int minThreads, int maxThreads, int waitTimeout,
			BlockingQueue<E> queue) {
		this.maxThreads = maxThreads;
		this.minThreads = minThreads;
		this.queue = queue;
		this.waitTimeout = waitTimeout;
	}

	protected void addThread() {
		if (threadList.size() >= maxThreads) {
			logger.info("Thread has the maximum[" + maxThreads + "]");
			return;
		}
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						try {
							E r = queue.poll(waitTimeout, TimeUnit.SECONDS);
							if (r == null && threadList.size() > minThreads)
								break;
							if (r != null) {
								busyThreads.incrementAndGet();
								try {
									r.run();
								} finally {
									busyThreads.decrementAndGet();
								}
							}
						} catch (InterruptedException e) {
							break;
						}
					}
				} finally {
					threadList.remove(Thread.currentThread());
					logger.info("remove thread[" + Thread.currentThread()
							+ "]{size:" + threadList.size() + ";busy:"
							+ busyThreads.get());
				}
			}
		});
		logger.info("create thread[" + thread + "]{size:" + threadList.size()
				+ ";busy:" + busyThreads.get() + "}");
		threadList.add(thread);
		thread.start();
	}

	public boolean execute(E runnable) throws InterruptedException {
		if (!queue.isEmpty() || busyThreads.get() >= threadList.size())
			addThread();
		return queue.offer(runnable, waitTimeout, TimeUnit.SECONDS);
	}

	public void shutdown() {
		for (Thread o : threadList) {
			o.interrupt();
		}
	}
}
