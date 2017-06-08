package kxd.util.watch;

/**
 * 监控线程
 * @author 赵明
 *
 */
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class WatchThread extends Thread {
	CopyOnWriteArrayList<WatchTask> tasks = new CopyOnWriteArrayList<WatchTask>();
	private volatile boolean started = false;
	static WatchThread thread = new WatchThread();

	static public WatchThread getDefaultWatchThread() {
		return thread;
	}

	static public void addTask(WatchTask task) {
		thread.add(task);
	}

	static public void removeTask(WatchTask task) {
		thread.remove(task);
	}

	static public void clearTask() {
		thread.clear();
	}

	public WatchThread() {
		setName("Watch");
		setDaemon(true);
	}

	public void start() {
		super.start();
		started = true;
	}

	/**
	 * 添加监视任务，注意：getInterval()小于1的将被加入
	 * 
	 * @param task
	 */
	public void add(WatchTask task) {
		if (task.taskTerminated())
			return;
		tasks.add(task);
		if (!started)
			start();
	}

	public void remove(WatchTask task) {
		tasks.remove(task);
	}

	public int taskCount() {
		return tasks.size();
	}

	public void clear() {
		tasks.clear();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (Throwable e) {
			}
			try {
				Iterator<WatchTask> it = tasks.iterator();
				while (it.hasNext()) {
					WatchTask task = it.next();
					if (task.taskTerminated())
						tasks.remove(task);
					else
						task.watch();
				}
			} catch (Throwable e) {
			}
		}
	}
}
