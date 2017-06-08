package kxd.util.watch;

/**
 * 监视线程的任务
 * 
 * @author 赵明
 * 
 */
public interface WatchTask {
	/**
	 * 监视
	 * 
	 */
	public void watch();

	/**
	 * 指示当前监视任务是否已经终止，如果已经终止，则监视线程会自动移除该任务
	 * 
	 * @return true/false
	 */
	public boolean taskTerminated();
}
